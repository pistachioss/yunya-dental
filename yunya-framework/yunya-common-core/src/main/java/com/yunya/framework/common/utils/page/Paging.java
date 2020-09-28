package com.yunya.framework.common.utils.page;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页，创建对象时传入总列表以及每页最大数，<br>
 *     可通过getPaging(int page)或getPaging()方法获取想要的分页，<br>
 *         其中 page 为想要的分页页码，默认为1。
 * @param <T> 传入的总列表中元素的类型
 */
public class Paging<T> {

    private Page page = null;
    private List<List<T>> pageAfterList = new ArrayList<>();

    public Paging(int pageNum, int pageCount) {
        page = new Page();
        page.setPageNum(pageNum);
        page.setPageCount(pageCount);
    }

    public Page getPageData(List<T> obj) {
        if (null != obj && !obj.isEmpty()) {
            // 总列表
            List<T> totalList = obj;
            // 当前页列表
            List<T> pageList = new ArrayList<>();
            // 设置列表中总对象数
            page.setTotal(obj.size());
            // 设置总页数
            this.setTotalPage();
            // 想要获取的分页的数据在总列表中开始处的索引值
            int start = (page.getPageNum() - 1) * page.getPageCount();
            // 结束处的索引
            int end = start + page.getPageCount();
            if (end > totalList.size()){
                // 如果结束处的索引值比总列表的长度大，那么结束处的索引为总列表长度
                end = totalList.size();
            }
            for (int index = 1; index < page.getTotalPage()+1; index++) {
                for (int i = start; i < end; i++) {
                    // 通过循环将总列表中指定索引区间内的元素添加进新列表
                    pageList.add(totalList.get(i));
                }
                pageAfterList.add(pageList);
            }
            page.setList(pageAfterList);
        }
        return page;
    }

    /**
     * 设置总页数
     */
    private void setTotalPage(){
        double a = (double) page.getTotal() / (double) page.getPageCount();
        int totalPage = (int) Math.ceil(a);
        page.setTotalPage(totalPage);
    }


}
