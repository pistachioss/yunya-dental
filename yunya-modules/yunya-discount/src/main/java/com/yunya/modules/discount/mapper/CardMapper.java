package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.bo.*;
import com.yunya.feign.discount.domain.query.CardSaleCashReceiptQuery;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.models.discount.*;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.*;


@Mapper
public interface CardMapper extends tk.mybatis.mapper.common.Mapper<Card> {

    int countByAllocateId(@Param("couponAllocateIds") List<Integer> couponAllocateIds);

    int getSumNumByCouponId(@Param("couponId") Integer couponId);

    void insertList(@Param("List") List<Card> cardList);

    List<ViewAllocateBo> listViewVosByParam(@Param("couponId") Integer couponId, @Param("couponAllocateIds") List<Integer> couponAllocateIds);

    List<Card> listExportVosByParam(@Param("couponId") Integer couponId, @Param("couponAllocateIds") List<Integer> couponAllocateIds);

    List<CouponSaleBo> listSaleInfoByParam(@Param("couponTypeList") List<Integer> couponTypeList, @Param("couponName") String couponName,
                                           @Param("orgId") Integer orgId);

    List<Card> listCardInfosByParam(@Param("cardNumber") String cardNumber, @Param("soldTypeList") List<Integer> soldTypeList,
                                    @Param("cardStatsList") List<Integer> cardStatsList, @Param("phoneNumber") String phoneNumber,
                                    @Param("couponId") Integer couponId, @Param("orgId") Integer orgId);

    int getOrgCardSoldInfoByParam(@Param("couponId") Integer couponId, @Param("orgId") Integer orgId);

    CardActiveDetailVo findByCardNumAndPass(@Param("cardNumber") String cardNumber, @Param("cardPassword") String cardPassword);

    List<PatientCardBo> listPatientCardsByParam(@Param("patientId") Integer patient, @Param("couponName") String couponName,
                                                @Param("couponTypeList") List<Integer> couponTypeList, @Param("queryType") Integer queryType);

    List<PatientBenefitBo> listBenefitByPatientId(@Param("patientId") Integer patientId, @Param("orgId") Integer orgId);

    /**
     * 查询卡券可用门诊
     * @param couponIds  优惠券id集合
     * @return list
     */
    List<UseClinicBo> getUseClinicIdStr(@Param("couponIds") List<Integer> couponIds);

    /**
     * 查询卡券可单个账单限制数量
     * @param couponId  优惠券id
     * @return list
     */
    List<UseClinicBo> getLimitCountByParam(@Param("couponId") Integer couponId);

    /**
     * 查询卡券使用数量信息
     * @param couponId 优惠券
     * @param cardId 卡券
     * @param type 项目类型
     * @return list
     */
    List<CouponItemUseBo> getCouponItemUseInfo(@Param("couponId") Integer couponId, @Param("cardId") Integer cardId,
                                           @Param("type") Integer type, @Param("couponType") Integer couponType);

    /**
     * 根据条件查询卡券售出现金收款总和
     *
     * @param query 查询条件
     * @return BigDecimal
     */
    BigDecimal selectCardSaleCashReceipt(@Param("query") CardSaleCashReceiptQuery query);
}