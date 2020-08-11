package com.yunya.framework.common.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.PageQueryParams;
import com.yunya.framework.common.utils.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 业务基础Biz
 *
 * @author chow
 */
public abstract class BaseBiz<M extends Mapper<T>, T> {

  @Autowired protected M mapper;

  public void setMapper(M mapper) {
    this.mapper = mapper;
  }

  public T selectOne(T entity) {
    return mapper.selectOne(entity);
  }

  public T selectById(Object id) {
    return mapper.selectByPrimaryKey(id);
  }

  public List<T> selectList(T entity) {
    return mapper.select(entity);
  }

  public List<T> selectListAll() {
    return mapper.selectAll();
  }

  public Long selectCount(T entity) {
    return (long) mapper.selectCount(entity);
  }

  public void insert(T entity) {
    EntityUtils.setCreatAndUpdatInfo(entity);
    mapper.insert(entity);
  }

  public void insertSelective(T entity) {
    EntityUtils.setCreatAndUpdatInfo(entity);
    mapper.insertSelective(entity);
  }

  public void delete(T entity) {
    mapper.delete(entity);
  }

  public void deleteById(Object id) {
    mapper.deleteByPrimaryKey(id);
  }

  public void updateById(T entity) {
    EntityUtils.setUpdatedInfo(entity);
    mapper.updateByPrimaryKey(entity);
  }

  public void updateSelectiveById(T entity) {
    EntityUtils.setUpdatedInfo(entity);
    mapper.updateByPrimaryKeySelective(entity);
  }

  /**
   * 根据对象查询列表
   *
   * @param tableObj 表对象
   * @return
   */
  public List<T> selectByObj(Object tableObj) {
    Example example = parseObjToExample(tableObj);
    return mapper.selectByExample(example);
  }

  /**
   * 根据条件查询列表
   *
   * @param example 查询条件
   * @return
   */
  public List<T> selectByExample(Example example) {
    return mapper.selectByExample(example);
  }

  /**
   * 根据条件查询对象
   *
   * @param example
   * @return
   */
  public int selectCountByExample(Example example) {
    return mapper.selectCountByExample(example);
  }

  /**
   * 根据条件查询并分页
   *
   * @param pageQueryParams 参数封装
   * @return
   */
  public PageInfo<T> selectByQuery(PageQueryParams pageQueryParams) {
    Class<T> clazz =
        (Class<T>)
            ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    Example example = new Example(clazz);
    if (pageQueryParams.entrySet().size() > 0) {
      Example.Criteria criteria = example.createCriteria();
      for (Map.Entry<String, Object> entry : pageQueryParams.entrySet()) {
        criteria.andLike(entry.getKey(), "%" + entry.getValue().toString() + "%");
      }
    }
    if (pageQueryParams.getWhetherPage()) {
      PageHelper.startPage(pageQueryParams.getPageNum(), pageQueryParams.getPageSize());
    }
    List<T> list = mapper.selectByExample(example);
    return new PageInfo<T>(list);
  }

  /**
   * obj转map
   *
   * @param obj 对象
   * @return map
   */
  private Map<String, Object> objectToMap(Object obj) {
    Map<String, Object> map = new HashMap<>(16);
    Class<?> clazz = obj.getClass();
    try {
      for (Field field : clazz.getDeclaredFields()) {
        // 设置可以访问私有变量
        field.setAccessible(true);
        String fieldName = field.getName();
        Object value = null;
        if (field.get(obj) != null) {
          value = field.get(obj);
        }
        map.put(fieldName, value);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return map;
  }

  /** 模糊查询的字段名 */
  private static final String FIELD_NAME = "name";

  /**
   * 对象转查询条件
   *
   * @param obj 对象
   * @return
   */
  private Example parseObjToExample(Object obj) {
    Example example = new Example(obj.getClass());
    Map<String, Object> objectMap = objectToMap(obj);
    Set<Map.Entry<String, Object>> entries = objectMap.entrySet();
    if (entries.size() > 0) {
      Example.Criteria criteria = example.createCriteria();
      for (Map.Entry<String, Object> entry : entries) {
        String k = entry.getKey();
        Object v = entry.getValue();
        if (null != v) {
          if (FIELD_NAME.equals(k) && StringUtils.isNotBlank(v.toString())) {
            criteria.andLike(k, "%" + v + "%");
          } else {
            criteria.andEqualTo(k, v);
          }
        }
      }
    }
    return example;
  }

}
