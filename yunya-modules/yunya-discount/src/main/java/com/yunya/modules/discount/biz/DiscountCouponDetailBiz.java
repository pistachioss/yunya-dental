package com.yunya.modules.discount.biz;

import com.yunya.models.discount.CardClinic;
import com.yunya.models.discount.DiscountCouponDetail;
import com.yunya.modules.discount.form.DiscountDetailForm;
import com.yunya.modules.discount.form.DiscountDetailNode;
import com.yunya.modules.discount.mapper.DiscountCouponDetailMapper;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.modules.discount.constant.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-14 17:40
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DiscountCouponDetailBiz extends BaseBiz<DiscountCouponDetailMapper, DiscountCouponDetail> {
    private static Integer TARIFF_CATEGORY = 0;
    private static Integer TARIFF = 1;
    private static Integer ORAL_TARIFF_CATEGORY = 2;
    private static Integer ORAL_TARIFF = 3;
    private static Integer ALL = 4;
    private static final Integer DISCOUNT_COUPON_TYPE = 1;
    private static final Integer FINISH = 1;

    @Autowired
    private CardClinicBiz cardClinicBiz;

    /**
     * 新增明细
     *
     * @param discountDetailForm
     */
    public void saveDiscountCouponDetail(DiscountDetailForm discountDetailForm) {
        Integer id = discountDetailForm.getId();
        Integer type = discountDetailForm.getRange();
        Boolean tariffFlag = false;
        Boolean oralTariffFlag = false;
        if (type == ALL) {
            // 使用全部项目
            DiscountCouponDetail discountCouponDetail = new DiscountCouponDetail();
            discountCouponDetail.setDiscountCouponId(discountDetailForm.getId());
            discountCouponDetail.setType(4);
            insertSelective(discountCouponDetail);
        } else {
            List<DiscountCouponDetail> discountCouponDetails = new ArrayList<>();
            // 添加价目表目录
            List<Integer> tariffCategoryIds = discountDetailForm.getTariffCategoryIds();
            if ((tariffCategoryIds != null) && (!tariffCategoryIds.isEmpty())) {
                tariffFlag = true;
                for (Integer tariffCategoryId : tariffCategoryIds) {
                    DiscountCouponDetail discountCouponDetail = new DiscountCouponDetail();
                    discountCouponDetail.setDiscountCouponId(id);
                    discountCouponDetail.setType(TARIFF_CATEGORY);
                    discountCouponDetail.setDetailId(tariffCategoryId);
                    discountCouponDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                    discountCouponDetail.setCrtName(BaseContextHandler.getUsername());
                    discountCouponDetails.add(discountCouponDetail);
                }
            }

            // 添加商品目录
            List<Integer> oralTariffCategoryIds = discountDetailForm.getOralTariffCategoryIds();
            if ((oralTariffCategoryIds != null) && (!oralTariffCategoryIds.isEmpty())) {
                oralTariffFlag = true;
                for (Integer oralTariffCategoryId : oralTariffCategoryIds) {
                    DiscountCouponDetail discountCouponDetail = new DiscountCouponDetail();
                    discountCouponDetail.setDiscountCouponId(id);
                    discountCouponDetail.setDetailId(oralTariffCategoryId);
                    discountCouponDetail.setType(ORAL_TARIFF_CATEGORY);
                    discountCouponDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                    discountCouponDetail.setCrtName(BaseContextHandler.getUsername());
                    discountCouponDetails.add(discountCouponDetail);
                }
            }

            //  添加价目表明细
            List<DiscountDetailNode> tariffNodes = discountDetailForm.getTariffIds();
            if ((tariffNodes != null) && (!tariffNodes.isEmpty())) {
                for (DiscountDetailNode discountDetailNode : tariffNodes) {
                    if (tariffFlag) {
                        if (!tariffCategoryIds.contains(discountDetailNode.getCategoryId())) {
                            List<Integer> tariffIds = discountDetailNode.getTariffIds();
                            if (!tariffIds.isEmpty()) {
                                for (Integer tariffId : tariffIds) {
                                    DiscountCouponDetail discountCouponDetail = new DiscountCouponDetail();
                                    discountCouponDetail.setDiscountCouponId(id);
                                    discountCouponDetail.setDetailId(tariffId);
                                    discountCouponDetail.setType(TARIFF);
                                    discountCouponDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                                    discountCouponDetail.setCrtName(BaseContextHandler.getUsername());
                                    discountCouponDetails.add(discountCouponDetail);
                                }
                            }

                        }
                    }
                }
            }

            // 添加商品明细
            List<DiscountDetailNode> oralTariffNodes = discountDetailForm.getOralTariffIds();
            if ((oralTariffNodes != null) && (!oralTariffNodes.isEmpty())) {
                for (DiscountDetailNode discountDetailNode : oralTariffNodes) {
                    if (oralTariffFlag) {
                        if (!tariffCategoryIds.contains(discountDetailNode.getCategoryId())) {
                            List<Integer> tariffIds = discountDetailNode.getTariffIds();
                            if (!tariffIds.isEmpty()) {
                                for (Integer tariffId : tariffIds) {
                                    DiscountCouponDetail discountCouponDetail = new DiscountCouponDetail();
                                    discountCouponDetail.setDiscountCouponId(id);
                                    discountCouponDetail.setDetailId(tariffId);
                                    discountCouponDetail.setType(ORAL_TARIFF);
                                    discountCouponDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                                    discountCouponDetail.setCrtName(BaseContextHandler.getUsername());
                                    discountCouponDetails.add(discountCouponDetail);
                                }
                            }
                        }
                    }
                }
            }
            if (!discountCouponDetails.isEmpty()) {
                mapper.batchInsert(discountCouponDetails);
            }
        }
    }

    /**
     * 修改
     */
    public void updateDiscountCouponDetail(DiscountDetailForm discountDetailForm) {
        Integer id = discountDetailForm.getId();
        // 判断是否完成分配
        CardClinic cardClinic = new CardClinic();
        cardClinic.setType(DISCOUNT_COUPON_TYPE);
        cardClinic.setStatus(FINISH);
        cardClinic.setRelevanceId(id);
        if (!cardClinicBiz.selectList(cardClinic).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法修改折扣券项目", ExceptionCode.CARD_EXIST);
        }
        DiscountCouponDetail data = new DiscountCouponDetail();
        data.setDiscountCouponId(id);
        // 删除
        delete(data);

        // 重新新增
        saveDiscountCouponDetail(discountDetailForm);
    }
}
