package com.yunya.modules.appointment.util.pageUtil.model;

import java.util.List;

/**
 * @program: yunya-dental
 * @description: 分页参数
 * @author: LHB
 * @create: 2020-09-27 14:19
 **/
public class  Page<T> {
    /**
     * 当前页码
     */
    private int pageNum = 1;
    /**
     * 每页最大条数
     */
    private int pageCount;
    /**
     * 总条数
     */
    private int total;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 总列表
     */
    private List<T> list;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
