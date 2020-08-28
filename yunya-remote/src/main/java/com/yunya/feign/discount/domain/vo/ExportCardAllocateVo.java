package com.yunya.feign.discount.domain.vo;

import com.alibaba.excel.annotation.*;
import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/25
 */
@Getter
@Setter
@ApiModel(value = "卡券导出模型")
public class ExportCardAllocateVo {
    @ExcelProperty("卡号")
    private String cardNumber;
    @ExcelProperty("卡密")
    private String cardPassword;
    @ExcelProperty("配给对象")
    private String allocateOrgName;
}
