package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.bo.*;
import com.yunya.feign.discount.domain.query.CardIyOr365ActivedQuery;
import com.yunya.feign.discount.domain.vo.CardActiveDetailVo;
import com.yunya.feign.discount.domain.vo.CardIyOr365VO;
import com.yunya.feign.discount.domain.vo.CardWxDetailVO;
import com.yunya.feign.discount.domain.vo.CardWxVO;
import com.yunya.feign.discount.domain.vo.WxPatientEffectiveVo;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.patient_central.domain.vo.web.PatientEventVO;
import com.yunya.feign.report.domain.vo.WxCardUsageVo;
import com.yunya.models.discount.Card;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;


@Mapper
public interface CardMapper extends tk.mybatis.mapper.common.Mapper<Card> {

     List<CardWxVO> findCardWxList(@Param("patientId")Integer patientId);

     List<CardWxDetailVO> findCardWxDetail(@Param("couponId")Integer couponId);

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
    BigDecimal selectCardSaleCashReceipt(@Param("query") CashReceiptOrRefundQuery query);

    /**
     * 查询患者该产品最新一次绑定的卡券
     */
    String getRecentCard(@Param("patientId") Integer patientId, @Param("couponId") Integer couponId);

    int countPatientCoupon(@Param("patientId") Integer patientId, @Param("couponId") Integer couponId);

    /**
     * 微信公众号-查询患者有效的卡券
     */
    List<WxPatientEffectiveVo> listPatientEffectiveCard(@Param("patientId") Integer patientId);

    /**
     * 微信公众号-用户的礼包使用详情
     */
    WxCardUsageVo getCardUsage(@Param("cardId") Integer cardId);

    void updateList(@Param("list") List<Card> list);

    List<Integer> listPatientAllCard(@Param("patientId") Integer patientId);

    List<CardIyOr365VO> selectIyOr365CardActivedList(@Param("query") CardIyOr365ActivedQuery query);

    /**
     * 根据患者id查询患者卡券轨迹
     *
     * @param patientId
     * @return
     */
    List<PatientEventVO> selectPatientCardTrajectory(@Param("patientId") Integer patientId);

    List<Card> listRemaining(@Param("couponIds") Collection<Integer> couponIds, @Param("orgId") Integer orgId);

    void soldList(@Param("list") List<Card> list);
}