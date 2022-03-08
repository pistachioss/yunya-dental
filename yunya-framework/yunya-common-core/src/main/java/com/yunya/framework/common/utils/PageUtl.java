package com.yunya.framework.common.utils;

import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.PageQuery;

import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/4/14 15:58
 * @since: 1.0.0
 */
public class PageUtl<T> {


    /**
     * 手动分页（默认分页）
     *
     * @param pageQuery 分页查询对象
     * @param resultList 数据集
     * @return
     */
    public static <T> PageInfo<T> doPage(PageQuery pageQuery, List<T> resultList) {
        return doPage(pageQuery.getPageNum(), pageQuery.getPageSize(), resultList, pageQuery.getWhetherPage());
    }

    /**
     * 手动分页（默认分页）
     *
     * @param pageNum 第几页
     * @param pageSize 条数
     * @param resultList 数据集
     * @return
     */
    public static <T> PageInfo<T> doPage(Integer pageNum, Integer pageSize, List<T> resultList) {
        return doPage(pageNum, pageSize, resultList, true);
    }

    /**
     * 手动分页
     *
     * @param pageNum 第几页
     * @param pageSize 条数
     * @param resultList 数据集
     * @param whetherPage 是否分页
     * @return
     */
    public static <T> PageInfo<T> doPage(Integer pageNum, Integer pageSize, List<T> resultList, Boolean whetherPage) {
        if (!whetherPage || StringHelper.isEmpty(resultList)) {
            return new PageInfo<>(resultList);
        }
        int total = resultList.size();
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        List<T> list =
                resultList.subList(pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
        pageInfo.setList(list);
        return pageInfo;
    }
}
