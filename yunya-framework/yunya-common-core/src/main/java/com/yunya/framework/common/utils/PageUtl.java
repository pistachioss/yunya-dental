package com.yunya.framework.common.utils;

import com.github.pagehelper.PageInfo;

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
     * 手动分页
     *
     * @param pageNum
     * @param pageSize
     * @param resultList
     * @return
     */
    public static <T> PageInfo<T> doPage(Integer pageNum, Integer pageSize, List<T> resultList) {
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
