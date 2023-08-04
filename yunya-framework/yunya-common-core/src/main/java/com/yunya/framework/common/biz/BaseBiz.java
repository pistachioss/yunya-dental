package com.yunya.framework.common.biz;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.PageQueryParams;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.MapUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
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

  @Autowired private QrConfig qrConig;

  public void setMapper(M mapper) {
    this.mapper = mapper;
  }

  /**
   * 根据实体对象查询
   *
   * @param entity 实体对象
   * @return
   */
  public T selectOne(T entity) {
    return mapper.selectOne(entity);
  }

  /**
   * 根据实体对象ID查询
   *
   * @param id 实体对象ID
   * @return
   */
  public T selectById(Object id) {
    return mapper.selectByPrimaryKey(id);
  }

  /**
   * 根据实体对象查询列表
   *
   * @param entity 实体对象
   * @return
   */
  public List<T> selectList(T entity) {
    return mapper.select(entity);
  }

  /**
   * 查询全部实体对象列表
   *
   * @return
   */
  public List<T> selectListAll() {
    return mapper.selectAll();
  }

  /**
   * 根据实体对象查询数量
   *
   * @param entity 实体对象
   * @return
   */
  public Long selectCount(T entity) {
    return (long) mapper.selectCount(entity);
  }

  /**
   * 插入实体对象(全部字段)
   *
   * @param entity 实体对象
   * @return int
   */
  public int insert(T entity) {
    EntityUtils.setCreatAndUpdatInfo(entity);
    return mapper.insert(entity);
  }

  /**
   * 插入实体对象(不为空字段)
   *
   * @param entity 实体对象
   * @return int
   */
  public int insertSelective(T entity) {
    EntityUtils.setCreatAndUpdatInfo(entity);
    return mapper.insertSelective(entity);
  }

  /**
   * 根据实体对象删除
   *
   * @param entity 实体对象
   * @return int
   */
  public int delete(T entity) {
    return mapper.delete(entity);
  }

  /**
   * 根据实体对象ID删除
   *
   * @param id 实体对象ID
   * @return int
   */
  public int deleteById(Object id) {
    return mapper.deleteByPrimaryKey(id);
  }

  /**
   * 根据实体对象ID更新（全部字段）
   *
   * @param entity 实体对象
   * @return int
   */
  public int updateById(T entity) {
    EntityUtils.setUpdatedInfo(entity);
    return mapper.updateByPrimaryKey(entity);
  }

  /**
   * 根据实体对象ID更新（不为空字段）
   *
   * @param entity 实体对象
   * @return int
   */
  public int updateSelectiveById(T entity) {
    EntityUtils.setUpdatedInfo(entity);
    return mapper.updateByPrimaryKeySelective(entity);
  }

  /**
   * 根据对象查询列表
   *
   * @param tableObj 表对象
   * @return list
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
    return MapUtil.conversionObjToMap(obj);
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

  /**
   * 生成二维码并保存到指定文件
   *
   * @param content
   * @param file
   */
  public void generateFile(String content, File file){
    //生成到本地文件
    QrCodeUtil.generate(content, qrConig, file);
  }

  /**
   * 生成二维码并输出到响应流
   *
   * @param content
   * @param response
   * @throws IOException
   */
  public void generateAsStream(String content, HttpServletResponse response) throws IOException {
    QrCodeUtil.generate(content, qrConig,"png", response.getOutputStream());
  }
}
