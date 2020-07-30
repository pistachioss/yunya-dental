package com.yunya.modules.discount.biz;

import com.yunya.models.discount.CardClinic;
import com.yunya.models.discount.VoucherDetail;
import com.yunya.modules.discount.form.DiscountDetailForm;
import com.yunya.modules.discount.form.DiscountDetailNode;
import com.yunya.modules.discount.mapper.VoucherDetailMapper;
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
 * @create 2020-07-13 14:04
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VoucherDetailBiz extends BaseBiz<VoucherDetailMapper, VoucherDetail> {
    private static Integer TARIFF_CATEGORY = 0;
    private static Integer TARIFF = 1;
    private static Integer ORAL_TARIFF_CATEGORY = 2;
    private static Integer ORAL_TARIFF = 3;
    private static Integer ALL = 4;
    private static final Integer VOUCHER_TYPE = 0;
    private static final Integer FINISH = 1;
    @Autowired
    private CardClinicBiz cardClinicBiz;


    /**
     * 新增明细
     *
     * @param discountDetailForm
     */
    public void saveVoucherDetail(DiscountDetailForm discountDetailForm) {
        Integer id = discountDetailForm.getId();
        Integer type = discountDetailForm.getRange();
        Boolean tariffFlag = false;
        Boolean oralTariffFlag = false;
        if (type == ALL) {
            // 使用全部项目
            VoucherDetail voucherDetail = new VoucherDetail();
            voucherDetail.setVoucherId(id);
            voucherDetail.setType(4);
            insertSelective(voucherDetail);
        } else {
            List<VoucherDetail> voucherDetails = new ArrayList<>();
            // 添加价目表目录
            List<Integer> tariffCategoryIds = discountDetailForm.getTariffCategoryIds();
            if ((tariffCategoryIds != null) && (!tariffCategoryIds.isEmpty())) {
                tariffFlag = true;
                for (Integer tariffCategoryId : tariffCategoryIds) {
                    VoucherDetail voucherDetail = new VoucherDetail();
                    voucherDetail.setVoucherId(id);
                    voucherDetail.setType(TARIFF_CATEGORY);
                    voucherDetail.setDetailId(tariffCategoryId);
                    voucherDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                    voucherDetail.setCrtName(BaseContextHandler.getUsername());
                    voucherDetails.add(voucherDetail);
                }
            }

            // 添加商品目录
            List<Integer> oralTariffCategoryIds = discountDetailForm.getOralTariffCategoryIds();
            if ((oralTariffCategoryIds != null) && (!oralTariffCategoryIds.isEmpty())) {
                oralTariffFlag = true;
                for (Integer oralTariffCategoryId : oralTariffCategoryIds) {
                    VoucherDetail voucherDetail = new VoucherDetail();
                    voucherDetail.setDetailId(oralTariffCategoryId);
                    voucherDetail.setVoucherId(id);
                    voucherDetail.setType(ORAL_TARIFF_CATEGORY);
                    voucherDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                    voucherDetail.setCrtName(BaseContextHandler.getUsername());
                    voucherDetails.add(voucherDetail);
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
                                    VoucherDetail voucherDetail = new VoucherDetail();
                                    voucherDetail.setDetailId(tariffId);
                                    voucherDetail.setVoucherId(id);
                                    voucherDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                                    voucherDetail.setCrtName(BaseContextHandler.getUsername());
                                    voucherDetail.setType(TARIFF);
                                    voucherDetails.add(voucherDetail);
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
                                    VoucherDetail voucherDetail = new VoucherDetail();
                                    voucherDetail.setVoucherId(id);
                                    voucherDetail.setDetailId(tariffId);
                                    voucherDetail.setType(ORAL_TARIFF);
                                    voucherDetail.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                                    voucherDetail.setCrtName(BaseContextHandler.getUsername());
                                    voucherDetails.add(voucherDetail);
                                }
                            }
                        }
                    }
                }
            }
            if (!voucherDetails.isEmpty()) {
                mapper.batchInsert(voucherDetails);
            }
        }
    }

    /**
     * 修改
     */
    public void updateVoucherDetail(DiscountDetailForm discountDetailForm) {
        Integer id = discountDetailForm.getId();
        // 判断是否完成分配
        CardClinic cardClinic = new CardClinic();
        cardClinic.setRelevanceId(id);
        cardClinic.setType(VOUCHER_TYPE);
        cardClinic.setStatus(FINISH);
        if (!cardClinicBiz.selectList(cardClinic).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法修改代金券项目", ExceptionCode.CARD_EXIST);
        }
        VoucherDetail data = new VoucherDetail();
        data.setVoucherId(id);
        // 删除
        delete(data);

        // 重新新增
        saveVoucherDetail(discountDetailForm);
    }
}
