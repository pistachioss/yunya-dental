package com.yunya.modules.discount.biz;

import com.yunya.feign.discount.domain.vo.VouDisAfterOptimizationVO;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya.models.tariff.BaseOralTariffCategory;
import com.yunya.models.tariff.BaseTariff;
import com.yunya.models.tariff.BaseTariffCategory;
import com.yunya.modules.discount.form.PackageCouponItemForm;
import com.yunya.modules.discount.form.SpecialPackageCouponItemForm;
import com.yunya.modules.discount.form.VoucherDiscountItemForm;
import com.yunya.modules.discount.mapper.VoucherDiscountItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseCouponItem;

/**
 * @author 杨柳絮
 * @className VoucherDiscountItemBiz
 * @description
 * @date 2020/8/20 15:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VoucherDiscountItemBiz extends BaseBiz<VoucherDiscountItemMapper, VoucherDiscountItem> {

    @Resource
    private RemoteRabbitMqServiceFeign mqServiceFeign;
    @Autowired
    private RemoteTreatmentServiceFeign remoteTreatmentServiceFeign;
    @Resource
    private DeductionPeriodBiz deductionPeriodBiz;

    public int saveVouAndDis(List<VoucherDiscountItemForm> list) {
        int a = mapper.saveVouAndDis(list);
        if (a > 0 && list.size() > 0) {
            mqServiceFeign.sendMessage(list.get(0).getCouponId(), BusinessConstants.ADD, BaseCouponItem);
        }
        return a;
    }

    public int savePackage(List<PackageCouponItemForm> list) {
        int a = mapper.savePackage(list);
        if (a > 0 && list.size() > 0) {
            mqServiceFeign.sendMessage(list.get(0).getCouponId(), BusinessConstants.ADD, BaseCouponItem);
        }
        return a;
    }

    public int saveSpecial(List<SpecialPackageCouponItemForm> list) {
        int a = mapper.saveSpecial(list);
        deductionPeriodBiz.save(list);
        if (a > 0 && list.size() > 0) {
            mqServiceFeign.sendMessage(list.get(0).getCouponId(), BusinessConstants.ADD, BaseCouponItem);
        }
        return a;
    }

    public List<VouDisAfterOptimizationVO> findListAfterOptimization(VoucherDiscountItem voucherDiscountItem) {
        //关联项目集合
        List<VoucherDiscountItem> list = mapper.select(voucherDiscountItem);
        //价目表分类列表
        List<BaseTariffCategory> baseTariffCategoryList = remoteTreatmentServiceFeign.findBaseTariffCategoryList(new BaseTariffCategory());
        Map<String, BaseTariffCategory> btcMap = new HashMap(16);
        baseTariffCategoryList.forEach(z -> btcMap.put(z.getId() + "", z));

        //价目表列表
        List<BaseTariff> baseTariffList = remoteTreatmentServiceFeign.findBaseTariffList(new BaseTariff());
        Map<String, BaseTariff> btfMap = new HashMap(16);
        baseTariffList.forEach(z -> btfMap.put(z.getId() + "", z));

        //商品分类列表
        List<BaseOralTariffCategory> baseOralTariffCategoryList = remoteTreatmentServiceFeign.findBaseOralTariffCategoryList(new BaseOralTariffCategory());
        Map<String, BaseOralTariffCategory> bofMap = new HashMap(16);
        baseOralTariffCategoryList.forEach(z -> bofMap.put(z.getId() + "", z));
        //商品项目列表
        List<BaseOralTariff> baseOralTariffList = remoteTreatmentServiceFeign.findBaseOralTariffList(new BaseOralTariff());
        Map<String, BaseOralTariff> bolMap = new HashMap(16);
        baseOralTariffList.forEach(z -> bolMap.put(z.getId() + "", z));
        List<VouDisAfterOptimizationVO> reList = new ArrayList<>();
        for (VoucherDiscountItem v : list) {
            VouDisAfterOptimizationVO vouDisAfterOptimizationVO = new VouDisAfterOptimizationVO();
            vouDisAfterOptimizationVO.setCouponId(v.getCouponId());
            vouDisAfterOptimizationVO.setChoiceRangType(v.getChoiceRangType());
            vouDisAfterOptimizationVO.setType(v.getType());
            vouDisAfterOptimizationVO.setId(v.getItemId());
            if (v.getType().equals(0)) {
                //若为价目表分类 进行赋值
                if (v.getChoiceRangType().equals(new Byte("1"))) {
                    vouDisAfterOptimizationVO.setItemNum(btcMap.get(v.getItemId() + "").getNumber());
                    vouDisAfterOptimizationVO.setItemName(btcMap.get(v.getItemId() + "").getName());
                //若为价目表明细 进行赋值
                } else if (v.getChoiceRangType().equals(new Byte("2"))) {
                    vouDisAfterOptimizationVO.setItemName(btfMap.get(v.getItemId() + "").getName());
                    vouDisAfterOptimizationVO.setItemNum(btfMap.get(v.getItemId() + "").getItemNumber());
                    vouDisAfterOptimizationVO.setUnit(btfMap.get(v.getItemId() + "").getUnit());
                    vouDisAfterOptimizationVO.setUnitPrice(btfMap.get(v.getItemId() + "").getPrice());
                }
            } else {
                //若为商品表分类 进行赋值
                if (v.getChoiceRangType().equals(new Byte("1"))) {
                    vouDisAfterOptimizationVO.setItemNum(bofMap.get(v.getItemId() + "").getNumber());
                    vouDisAfterOptimizationVO.setItemName(bofMap.get(v.getItemId() + "").getName());
                //若为商品表明细 进行赋值
                } else if (v.getChoiceRangType().equals(new Byte("2"))) {
                    vouDisAfterOptimizationVO.setItemName(bolMap.get(v.getItemId() + "").getName());
                    vouDisAfterOptimizationVO.setItemNum(bolMap.get(v.getItemId() + "").getItemNumber());
                    vouDisAfterOptimizationVO.setUnit(bolMap.get(v.getItemId() + "").getUnit());
                    vouDisAfterOptimizationVO.setUnitPrice(bolMap.get(v.getItemId() + "").getPrice());
                }

            }
            reList.add(vouDisAfterOptimizationVO);
        }
        return reList;
    }

}
