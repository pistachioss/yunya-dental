package com.yunya.modules.discount.controller;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.models.discount.PackageCouponItem;
import com.yunya.models.discount.SpecialPackageCouponItem;
import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.modules.discount.biz.CouponCommonInfoBiz;
import com.yunya.modules.discount.biz.PackageCouponItemBiz;
import com.yunya.modules.discount.biz.SpecialPackageCouponItemBiz;
import com.yunya.modules.discount.biz.VoucherDiscountItemBiz;
import com.yunya.modules.discount.enums.DiscountError;
import com.yunya.modules.discount.form.PackageCouponItemForm;
import com.yunya.modules.discount.form.SpecialPackageCouponItemForm;
import com.yunya.modules.discount.form.VoucherDiscountItemForm;
import com.yunya.modules.discount.form.VoucherDiscountItemQueryForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseCoupon;

/**
 * 设置适用项目
 * @author 杨柳絮
 * @className VoucherDiscountItemController
 * @description
 * @date 2020/8/20 15:03
 */
@Api(tags = "设置适用项目")
@RestController
@RequestMapping("/voucherDiscountItem")
public class VoucherDiscountItemController {

    @Autowired private VoucherDiscountItemBiz voucherDiscountItemBiz;
    @Autowired private PackageCouponItemBiz packageCouponItemBiz;
    @Autowired private SpecialPackageCouponItemBiz specialPackageCouponItemBiz;
    @Autowired private CouponCommonInfoBiz couponCommonInfoBiz;
    @Resource
    private RemoteRabbitMqServiceFeign mqServiceFeign;
    /**
     * 新增代金券折扣券适用项目
     *
     * @param
     * @return
     */
    @PostMapping("/saveVouAndDis")
    @ApiOperation("新增和修改代金券折扣券适用项目(每次修改都会清空之前关联的项目，所以要传回所有的适用项目进行重新新增)")
    @CurrentUser
    public ResponseResult saveVouAndDis(@RequestBody @Valid List<VoucherDiscountItemForm> voucherDiscountItems) {
        Date date = new Date();
        if(voucherDiscountItems.size()>0) {
            VoucherDiscountItem voucherDiscountItem = new VoucherDiscountItem();
            voucherDiscountItem.setCouponId(voucherDiscountItems.get(0).getCouponId());
            //清除之前的适用项目
            voucherDiscountItemBiz.delete(voucherDiscountItem);
            voucherDiscountItems.forEach(t -> {
                t.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
                t.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
                t.setUpdTime(date);
                t.setCrtTime(date);
            });
        }
        return ResponseUtil.success(voucherDiscountItemBiz.saveVouAndDis(voucherDiscountItems));
    }
    /**
     * 查询代金券折扣券适用项目
     *
     * @param
     * @return
     */
    @PostMapping("/findList")
    @ApiOperation("查询代金券折扣券适用项目")
    @CurrentUser
    public ResponseResult findList(@RequestBody @Valid VoucherDiscountItemQueryForm voucherDiscountItemQueryForm){
        VoucherDiscountItem voucherDiscountItem = new VoucherDiscountItem();
        BeanUtils.copyProperties(voucherDiscountItemQueryForm,voucherDiscountItem);
        return ResponseUtil.success(voucherDiscountItemBiz.selectList(voucherDiscountItem));
    }

    /**
     * 新增兑换券适用项目
     *
     * @param
     * @return
     */
    @PostMapping("/savePackage")
    @ApiOperation("新增和修改兑换券适用项目(每次修改都会清空之前关联的项目，所以要传回所有的适用项目进行重新新增)")
    @CurrentUser
    public ResponseResult savePackage(@RequestBody @Valid List<PackageCouponItemForm> packageCouponItemItems) {
        Date date = new Date();
        if (StringHelper.isEmpty(packageCouponItemItems)) {
            return ResponseUtil.fail(OperationCodeConstants.PARAMETERS_IS_ILLEGAL,"请添加兑换券项目再进行保存",null);
        }
        if(packageCouponItemItems.size()>0){
            PackageCouponItem packageCouponItem = new PackageCouponItem();
            packageCouponItem.setCouponId(packageCouponItemItems.get(0).getCouponId());
            //清除之前的适用项目
            packageCouponItemBiz.delete(packageCouponItem);
            packageCouponItemItems.forEach(t -> {
                t.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
                t.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
                t.setUpdTime(date);
                t.setCrtTime(date);
            });
            BigDecimal saleAmount = new BigDecimal("0");
            for(PackageCouponItemForm pi:packageCouponItemItems){
                saleAmount = saleAmount.add(pi.getSaleAmount());
            }
            CouponCommonInfo couponCommonInfo = new CouponCommonInfo();
            couponCommonInfo.setId(packageCouponItemItems.get(0).getCouponId());
            couponCommonInfo.setSoldAmount(saleAmount);
            //插入卡券售出金额
            couponCommonInfoBiz.updateSelectiveById(couponCommonInfo);
            mqServiceFeign.sendMessage(couponCommonInfo.getId(), BusinessConstants.UPDATE, BaseCoupon);
        }
        return ResponseUtil.success(voucherDiscountItemBiz.savePackage(packageCouponItemItems));
    }

    /**
     * 查询兑换券适用项目
     *
     * @param
     * @return
     */
    @PostMapping("/findPackageList")
    @ApiOperation("查询兑换券适用项目")
    @CurrentUser
    public ResponseResult findPackageList(@RequestBody @Valid VoucherDiscountItemQueryForm voucherDiscountItemQueryForm){
        PackageCouponItem packageCouponItem = new PackageCouponItem();
        BeanUtils.copyProperties(voucherDiscountItemQueryForm,packageCouponItem);
        return ResponseUtil.success(packageCouponItemBiz.selectList(packageCouponItem));
    }
    /**
     * 新增套餐券适用项目
     *
     * @param
     * @return
     */
    @PostMapping("/saveSpecial")
    @ApiOperation("新增和修改套餐券适用项目(每次修改都会清空之前关联的项目，所以要传回所有的适用项目进行重新新增)")
    @CurrentUser
    public ResponseResult saveSpecial(@RequestBody @Valid List<SpecialPackageCouponItemForm> specialPackageCouponItemForms) {
        Date date = new Date();
        if (StringHelper.isNotEmpty(specialPackageCouponItemForms)) {
            if (specialPackageCouponItemForms.size() > 0) {
                SpecialPackageCouponItem specialPackageCouponItem = new SpecialPackageCouponItem();
                specialPackageCouponItem.setCouponId(specialPackageCouponItemForms.get(0).getCouponId());
                //清除之前的适用项目
                specialPackageCouponItemBiz.delete(specialPackageCouponItem);
                specialPackageCouponItemForms.forEach(t -> {
                    t.setCrtId(Integer.parseInt(BaseContextHandler.getUserID()));
                    t.setUpdId(Integer.parseInt(BaseContextHandler.getUserID()));
                    t.setUpdTime(date);
                    t.setCrtTime(date);
                });
            }
            return ResponseUtil.success(voucherDiscountItemBiz.saveSpecial(specialPackageCouponItemForms));
        }
        return ResponseUtil.fail(DiscountError.BENEFIT_PACKAGE_ITEM_EMPTY.getCode(),
                DiscountError.BENEFIT_PACKAGE_ITEM_EMPTY.getMessage(),null);
    }

    /**
     * 查询套餐券适用项目
     *
     * @param
     * @return
     */
    @PostMapping("/findSpecialList")
    @ApiOperation("查询套餐券适用项目")
    @CurrentUser
    public ResponseResult findSpecialList(@RequestBody @Valid VoucherDiscountItemQueryForm voucherDiscountItemQueryForm){
        SpecialPackageCouponItem specialPackageCouponItem = new SpecialPackageCouponItem();
        BeanUtils.copyProperties(voucherDiscountItemQueryForm,specialPackageCouponItem);
        return ResponseUtil.success(specialPackageCouponItemBiz.selectList(specialPackageCouponItem));
    }

}
