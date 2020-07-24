package com.clinic.discount.biz;

import com.clinic.discount.entity.CardClinic;
import com.clinic.discount.entity.PackageCouponDetail;
import com.clinic.discount.form.PackageDetailForm;
import com.clinic.discount.form.PackageDetailNode;
import com.clinic.discount.mapper.PackageCouponDetailMapper;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.clinic.discount.constant.ExceptionCode.CARD_EXIST;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-10 14:21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PackageCouponDetailBiz extends BaseBiz<PackageCouponDetailMapper, PackageCouponDetail> {
    private static Integer TARIFF = 1;
    private static Integer ORAL_TARIFF = 3;
    private static final Integer PACKAGE_COUPON_TYPE = 2;
    private static final Integer FINISH = 1;
    @Autowired
    private CardClinicBiz cardClinicBiz;

    /**
     * 新增明细
     *
     * @param packageDetailForm
     */
    public void savePackageCouponDetail(PackageDetailForm packageDetailForm) {
        Integer id = packageDetailForm.getId();
        List<PackageCouponDetail> datas = new ArrayList<>();
        //  添加价目表明细
        List<PackageDetailNode> tariffs = packageDetailForm.getTariffs();
        if ((tariffs != null) && (!tariffs.isEmpty())) {
            for (PackageDetailNode packageDetailNode : tariffs) {
                PackageCouponDetail packageCouponDetail = new PackageCouponDetail();
                packageCouponDetail.setCount(packageDetailNode.getCount());
                packageCouponDetail.setDetailId(packageDetailNode.getTariffId());
                packageCouponDetail.setPackageCouponId(id);
                packageCouponDetail.setPackageCouponPrice(packageDetailNode.getPrice());
                packageCouponDetail.setWorkloadRate(packageDetailNode.getWorkload());
                packageCouponDetail.setType(TARIFF);
                datas.add(packageCouponDetail);
            }
        }

        //  添加商品明细
        List<PackageDetailNode> oralTariffs = packageDetailForm.getOralTariffs();
        if ((oralTariffs != null) && (!oralTariffs.isEmpty())) {
            for (PackageDetailNode packageDetailNode : oralTariffs) {
                PackageCouponDetail packageCouponDetail = new PackageCouponDetail();
                packageCouponDetail.setPackageCouponId(id);
                packageCouponDetail.setCount(packageDetailNode.getCount());
                packageCouponDetail.setDetailId(packageDetailNode.getTariffId());
                packageCouponDetail.setPackageCouponPrice(packageDetailNode.getPrice());
                packageCouponDetail.setWorkloadRate(packageDetailNode.getWorkload());
                packageCouponDetail.setType(ORAL_TARIFF);
                datas.add(packageCouponDetail);
            }
        }
        mapper.batchInsert(datas);
    }

    /**
     * 修改
     */
    public void updatePackageCouponDetail(PackageDetailForm packageDetailForm) {
        Integer id = packageDetailForm.getId();
        // 判断是否完成分配
        CardClinic cardClinic = new CardClinic();
        cardClinic.setRelevanceId(id);
        cardClinic.setType(PACKAGE_COUPON_TYPE);
        cardClinic.setStatus(FINISH);
        if (!cardClinicBiz.selectList(cardClinic).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法修改套餐券项目", CARD_EXIST);
        }
        PackageCouponDetail data = new PackageCouponDetail();
        data.setPackageCouponId(id);
        // 删除
        delete(data);

        // 重新新增
        savePackageCouponDetail(packageDetailForm);
    }
}