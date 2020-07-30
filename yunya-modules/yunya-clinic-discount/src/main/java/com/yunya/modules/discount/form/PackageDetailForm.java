package com.yunya.modules.discount.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-16 13:20
 */
@ApiModel("套餐明细表单")
@Data
public class PackageDetailForm implements Serializable {
    @NotNull(message = "套餐ID不能为空")
    private Integer id;
    /**
     * 价目表明细列表
     */
    private List<PackageDetailNode> tariffs;
    /**
     * 商品明细列表
     */
    private List<PackageDetailNode> oralTariffs;
}
