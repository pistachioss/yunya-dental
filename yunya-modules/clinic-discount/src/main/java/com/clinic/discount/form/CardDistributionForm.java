package com.clinic.discount.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-17 13:07
 */
@Data
@ApiModel("卡券分配表单")
public class CardDistributionForm implements Serializable {
    /**
     * 类型 0:代金券,1:折扣券,2:套餐券,4:充值卡
     */
    @ApiModelProperty("类型 0:代金券,1:折扣券,2:套餐券,4:充值卡")
    @NotNull(message = "类型不能为空")
    private Integer type;

    /**
     * 优惠方式的ID
     */
    @ApiModelProperty("优惠方式的ID")
    @NotNull(message = "优惠方式的ID不能为空")
    private Integer relevanceId;

    /**
     * 优惠方式的名称
     */
    @ApiModelProperty("优惠方式的名称")
    private String relevanceName;
    /**
     * 批次
     */
    @ApiModelProperty("批次")
    private Integer revision;

    private List<Node> nodes;

    public static class Node {
        private Integer clinicId;
        private Integer count;
        private Integer orgType;

        public Integer getClinicId() {
            return clinicId;
        }

        public void setClinicId(Integer clinicId) {
            this.clinicId = clinicId;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getOrgType() {
            return orgType;
        }

        public void setOrgType(Integer orgType) {
            this.orgType = orgType;
        }

    }
}
