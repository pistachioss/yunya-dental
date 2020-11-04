package com.yunya.modules.appointment.util.pageUtil.model;

import com.yunya.feign.appointment.vo.AppointmentDimensionVo;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2020-09-28 12:49
 **/
public class AssistantPageModel {
    private int distentIndex;
    private int distentId;
    private int assistantId;
    private AppointmentDimensionVo assistantVo;

    public int getDistentId() {
        return distentId;
    }

    public void setDistentId(int distentId) {
        this.distentId = distentId;
    }

    public int getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(int assistantId) {
        this.assistantId = assistantId;
    }

    public AppointmentDimensionVo getAssistantVo() {
        return assistantVo;
    }

    public void setAssistantVo(AppointmentDimensionVo assistantVo) {
        this.assistantVo = assistantVo;
    }

    public int getDistentIndex() {
        return distentIndex;
    }

    public void setDistentIndex(int distentIndex) {
        this.distentIndex = distentIndex;
    }
}
