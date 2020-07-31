package com.yunya.modules.discount.biz;

import com.yunya.models.discount.CardClinic;
import com.yunya.models.discount.Voucher;
import com.yunya.modules.discount.form.DiscountQueryForm;
import com.yunya.modules.discount.form.DiscountUpdateForm;
import com.yunya.modules.discount.mapper.VoucherMapper;
import com.yunya.modules.discount.vo.DiscountVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.BaseException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.modules.discount.constant.ExceptionCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.NAME_IS_OCCUPIED;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-09 17:23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class VoucherBiz extends BaseBiz<VoucherMapper, Voucher> {
    // 代金券类型
    private static final Integer VOUCHER_TYPE = 0;
    // 状态
    private static final Integer FINISH = 1;
    private static final Integer PLAN = 0;

    @Autowired
    private CardClinicBiz cardClinicBiz;

    /**
     * 新增
     *
     * @param voucher
     */
    public Integer saveVoucher(Voucher voucher) {
        Voucher data = new Voucher();
        data.setName(voucher.getName());
        if (mapper.selectOne(data) != null) {
            throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
        }

        insertSelective(voucher);
        return voucher.getId();
    }

    /**
     * 修改
     *
     * @param discountUpdateForm
     */
    public void updateVoucher(DiscountUpdateForm discountUpdateForm) {
        Integer id = discountUpdateForm.getId();
        boolean flag = true;
        // 判断是否完成分配
        CardClinic cardClinic = new CardClinic();
        cardClinic.setRelevanceId(id);
        cardClinic.setType(VOUCHER_TYPE);
        cardClinic.setStatus(FINISH);
        if (cardClinicBiz.selectList(cardClinic).isEmpty()) {
            // 未完成分配
            flag = false;
        }
        Voucher voucher = new Voucher();
        if (flag) {
            // 只能修改时间
            voucher.setSellingStartDate(discountUpdateForm.getSellingStartDate());
            voucher.setSellingEndDate(discountUpdateForm.getSellingEndDate());
            voucher.setEffectiveDays(discountUpdateForm.getEffectiveDays());
            voucher.setActivationDeadline(discountUpdateForm.getActivationDeadline());
        } else {
            // 重名判断
            String name = discountUpdateForm.getName();
            if (StringUtils.isNotBlank(name)) {
                Voucher data = new Voucher();
                data.setName(name);
                if (mapper.select(data).size() >= 2) {
                    throw new BaseException("产品名称已经被占用", NAME_IS_OCCUPIED);
                }
            }
            voucher = EntityUtils.build(discountUpdateForm, Voucher.class);
        }
        updateSelectiveById(voucher);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteVoucher(Integer id) {
        // 判断是否完成分配
        CardClinic cardClinic = new CardClinic();
        cardClinic.setRelevanceId(id);
        cardClinic.setType(VOUCHER_TYPE);
        cardClinic.setStatus(FINISH);
        if (cardClinicBiz.selectList(cardClinic).isEmpty()) {
            throw new BaseException("卡券已完成分配，无法删除", ExceptionCode.CARD_EXIST);
        }

        deleteById(id);
        // 删除分配计划中的记录
        cardClinic.setStatus(PLAN);
        cardClinicBiz.delete(cardClinic);
    }

    /**
     * 查询列表
     *
     * @param discountQueryForm
     * @return
     */
    public List<DiscountVO> search(DiscountQueryForm discountQueryForm) {
        return mapper.selectVOs(discountQueryForm.getMarketProductTypeId(), discountQueryForm.getName(),
                discountQueryForm.getStartDate(), discountQueryForm.getEndDate());
    }
}
