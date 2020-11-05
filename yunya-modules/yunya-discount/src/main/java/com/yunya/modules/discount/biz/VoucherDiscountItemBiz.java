package com.yunya.modules.discount.biz;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.models.discount.VoucherDiscountItem;
import com.yunya.modules.discount.form.PackageCouponItemForm;
import com.yunya.modules.discount.form.SpecialPackageCouponItemForm;
import com.yunya.modules.discount.mapper.VoucherDiscountItemMapper;
import com.yunya.modules.discount.form.VoucherDiscountItemForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseCoupon;
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

   public int saveVouAndDis(List<VoucherDiscountItemForm> list){
       int a = mapper.saveVouAndDis(list);
       if(a>0&&list.size()>0){
           mqServiceFeign.sendMessage(list.get(0).getCouponId(), BusinessConstants.ADD, BaseCouponItem);
       }
        return a;
    }

    public int savePackage(List<PackageCouponItemForm> list){
        int a = mapper.savePackage(list);
        if(a>0&&list.size()>0){
            mqServiceFeign.sendMessage(list.get(0).getCouponId(), BusinessConstants.ADD, BaseCouponItem);
        }
        return a;
    }

    public int saveSpecial(List<SpecialPackageCouponItemForm> list){
        int a = mapper.saveSpecial(list);
        if(a>0&&list.size()>0){
            mqServiceFeign.sendMessage(list.get(0).getCouponId(), BusinessConstants.ADD, BaseCouponItem);
        }
       return a;
    }
}
