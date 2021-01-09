package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 查看优惠明细视图VO
 * @author: LHB
 * @create: 2021-01-08 17:28
 **/
@ApiModel(value = "BillDiscountVO",description = "查看优惠明细视图VO")
@Data
public class BillDiscountVO implements Serializable {
    private String operateUserName;
    private List<BillDiscountDetailInifoVO> billDiscountDetail;
}
