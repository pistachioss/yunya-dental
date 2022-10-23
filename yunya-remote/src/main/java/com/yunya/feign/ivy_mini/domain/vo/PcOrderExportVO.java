package com.yunya.feign.ivy_mini.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2022/10/20 10:52
 **/
@Data
@ApiModel(description = "pc小程序订单导出")
@ContentRowHeight(15)
@HeadRowHeight(20)
@ColumnWidth(25)
public class PcOrderExportVO {
    @ExcelProperty({"艾维口腔线上商城订单发货信息表","订单编号"})
    private String orderSn;
    @ExcelProperty({"艾维口腔线上商城订单发货信息表","商品列表"})
    private String itemStr;
    @ExcelProperty({"艾维口腔线上商城订单发货信息表","收货信息"})
    private String receiverAddress;
}
