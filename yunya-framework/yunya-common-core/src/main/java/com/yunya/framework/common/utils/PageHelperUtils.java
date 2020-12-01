package com.yunya.framework.common.utils;

import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2020-12-01 17:48
 **/
public class PageHelperUtils {
    /**
     * 对List集合分页，并且将结果注入到PageHelper中
     * @param pageInfo 分页对象
     * @param list 结果集
     * @param pageSize 每页大小
     */
    public static void pageFromList(PageInfo<T> pageInfo,List<T> list,int pageSize) {
        // 设置分页
        pageInfo.setList(list);
        int size = pageInfo.getList().size();
        if (size > 0){
            pageInfo.setTotal(size);
            int pages = size % pageSize == 0 ? (size / pageSize) : (size / pageSize + 1);
            pageInfo.setPages(pages);
            int[] navPages = new int[pages];
            for (int i = 0; i < pages; i++) {
                navPages[i] = i+1;
            }
            pageInfo.setNavigatepageNums(navPages);
        } else {
            pageInfo.setTotal(0);
            pageInfo.setNavigatepageNums(new int[]{1});
        }
    }
}
