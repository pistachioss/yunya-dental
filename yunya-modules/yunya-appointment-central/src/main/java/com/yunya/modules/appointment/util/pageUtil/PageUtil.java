package com.yunya.modules.appointment.util.pageUtil;
import com.yunya.feign.appointment.vo.AppointmentDimensionVo;
import com.yunya.modules.appointment.util.pageUtil.model.AssistantPageModel;
import com.yunya.modules.appointment.util.pageUtil.model.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分页，创建对象时传入总列表以及每页最大数，<br>
 *     可通过getPaging(int page)或getPaging()方法获取想要的分页，<br>
 *         其中 page 为想要的分页页码，默认为1。
 * @param <T> 传入的总列表中元素的类型
 */
public class PageUtil<T> {

    private Page page = null;

    public PageUtil(int pageNum, int pageCount) {
        page = new Page();
        page.setPageNum(pageNum);
        page.setPageCount(pageCount);
    }


    /**
     * 设置总数(医生维度分页用)
     * @param obj
     * @return
     */
    private void setTotal(List<AppointmentDimensionVo> obj) {
        int total = 0;
        List<AppointmentDimensionVo> pageResult = new ArrayList<>();
        if (null != obj && !obj.isEmpty()) {
            // 计算大医生数
            int distentCount = obj.size();
            // 助手数
            int assistantCount = 0;
            // 循环统计大医生下的助手总数
            for (AppointmentDimensionVo appointmentDimensionVo : obj) {
                // 计算大医生下的助手的总数
                List<AppointmentDimensionVo> appointmentAssistants = appointmentDimensionVo.getAppointmentAssistants();
                if (null != appointmentAssistants && !appointmentAssistants.isEmpty()) {
                    assistantCount = appointmentAssistants.size();
                }
            }
            // 计算助手+大医生的总数量
            total = distentCount + assistantCount;
            // 设置分页对象中的总数
            page.setTotal(total);
            // 设置分页
            setPageTotal();
        }
    }

    /**
     * 设置总页数
     */
    private void setPageTotal() {
        int total = page.getTotal();
        int pageCount = page.getPageCount();
        double result = (double) total / (double) pageCount;
        int totalPage = (int) Math.ceil(result);
        page.setTotalPage(totalPage);
    }



    /**
     * 医生维度分页
     * 返回大医生下的助手分页
     * @param obj
     * @return
     */
    public Page getPageAssistantData(List<AppointmentDimensionVo> obj) {
        // 设置总数
        setTotal(obj);
        // 大医生下的所有助手
        List<AssistantPageModel> assistantVoData = getAssistantVoData(obj);
        // 分页之后存储list
        List<AppointmentDimensionVo> result = new ArrayList<>();
        // 分页开始位置
        int start = (page.getPageNum() - 1) * page.getPageCount();
        // 分页结束位置
        int end = 0;
        if (start + page.getPageCount() >= assistantVoData.size()) {
            end = assistantVoData.size();
        } else {
            end = start + page.getPageCount();
        }

        // 先对助手分页;---助手分页的时候，每页显示的最大条数 = pageCount - 1(大医生)
        List<AssistantPageModel> assistantPageModels = assistantVoData.subList(start, end);
        Map<Integer, List<AssistantPageModel>> collect = assistantPageModels.stream().collect(Collectors.groupingBy(AssistantPageModel::getDistentIndex));

        for (Map.Entry<Integer,List<AssistantPageModel>> entry : collect.entrySet()) {
            if (null != obj && !obj.isEmpty()) {
                AppointmentDimensionVo appointmentDimensionVo = obj.get(entry.getKey());
                // 获取助手列表
                List<AssistantPageModel> value = entry.getValue();
                List<AppointmentDimensionVo> assistantList = new ArrayList<>();
                // 将AssistantPageModel====>AppointmentDimensionVo
                for (AssistantPageModel assistantPageModel : value) {
                    AppointmentDimensionVo assistantVo = assistantPageModel.getAssistantVo();
                    if (null != assistantVo) {
                        assistantList.add(assistantVo);
                    }
                }
                appointmentDimensionVo.setAppointmentAssistants(assistantList);
                // 将助手加入分解结果列表中
                result.add(appointmentDimensionVo);
            }
        }
        // 将助手加入到分页对象中
        page.setList(result);
        return page;

    }

    /**
     * 将助手从大医生下面全部拆分出来
     * @param obj
     */
    private List<AssistantPageModel> getAssistantVoData(List<AppointmentDimensionVo> obj) {
        List<AssistantPageModel> assistantPageModels = new ArrayList<>();
        int index = 0;
        if (null != obj && !obj.isEmpty()) {
            for(AppointmentDimensionVo appointmentDimensionVo : obj) {
                List<AppointmentDimensionVo> appointmentAssistants = appointmentDimensionVo.getAppointmentAssistants();
                if (null != appointmentAssistants && !appointmentAssistants.isEmpty()) {
                    for(AppointmentDimensionVo appointmentDimensionVo1 : appointmentAssistants) {
                        AssistantPageModel assistantPageModel = new AssistantPageModel();
                        // 保存大医生的索引
                        assistantPageModel.setDistentIndex(index);
                        // 设置助手ID
                        assistantPageModel.setAssistantId(appointmentDimensionVo1.getDentistId());
                        // 设置大医生ID
                        assistantPageModel.setDistentId(appointmentDimensionVo.getDentistId());
                        // 设置助手信息
                        assistantPageModel.setAssistantVo(appointmentDimensionVo1);
                        assistantPageModels.add(assistantPageModel);
                    }
                } else {
                    AssistantPageModel assistantPageModel = new AssistantPageModel();
                    // 保存大医生的索引
                    assistantPageModel.setDistentIndex(index);
                    // 设置大医生ID
                    assistantPageModel.setDistentId(appointmentDimensionVo.getDentistId());
                    assistantPageModels.add(assistantPageModel);
                }
                index++;
            }
        }
        return assistantPageModels;
    }

    /**
     * 患者维度分页
     * 获取期待页码的列表，从1开始
     * @param obj 要分页的列表
     * @return 当前分页显示的数据列表
     */
    public Page getPaging(List<AppointmentDimensionVo> obj){
        if (null != obj && !obj.isEmpty()) {
            // 设置总数
            int total = obj.size();
            page.setTotal(total);
            // 设置总页数
            this.setPageTotal();
            // 分页之后存储list
            List<AppointmentDimensionVo> result = new ArrayList<>();
            // 分页开始位置
            int start = (page.getPageNum() - 1) * page.getPageCount();
            // 分页结束位置
            int end = 0;
            if (start + page.getPageCount() >= total) {
                end = total;
            } else {
                end = start + page.getPageCount();
            }
            // 截取分页范围
            List<AppointmentDimensionVo> appointmentDimensionVos = obj.subList(start, end);
            // 设置分页列表
            page.setList(appointmentDimensionVos);
        }
        return page;
    }


}
