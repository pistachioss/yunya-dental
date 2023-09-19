package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseCard;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;

public interface BaseCardMapper extends Mapper<BaseCard> {
    /**
     * 查询产品售出激活明细（代金、折扣、兑换、套餐）
     */
    List<CardStatisticsVo> listCardByParam(
            @Param("cardNumber") String cardNumber,
            @Param("allocateOrgIds") List<Integer> allocateOrgIds,
            @Param("soldTypes") List<Integer> soldTypes,
            @Param("soldStartDate") LocalDate soldStartDate,
            @Param("soldEndDate") LocalDate soldEndDate,
            @Param("activeOrgIds") List<Integer> activeOrgIds,
            @Param("activeStartDate") LocalDate activeStartDate,
            @Param("activeEndDate") LocalDate activeEndDate,
            @Param("soldWays") List<Integer> soldWays,
            @Param("chargeStatus") List<Integer> chargeStatus,
            @Param("remark") String remark,
            @Param("couponId") Integer couponId);

    /**
     * 查询产品售出激活明细（充值卡）
     */
    List<RechargeCardStatisticsVo> listRechargeUsedByParam(
            @Param("cardNumber") String cardNumber,
            @Param("allocateOrgIds") List<Integer> allocateOrgIds,
            @Param("soldTypes") List<Integer> soldTypes,
            @Param("soldStartDate") LocalDate soldStartDate,
            @Param("soldEndDate") LocalDate soldEndDate,
            @Param("rechargeOrgIds") List<Integer> rechargeOrgIds,
            @Param("rechargeStartDate") LocalDate rechargeStartDate,
            @Param("rechargeEndDate") LocalDate rechargeEndDate,
            @Param("soldWays") List<Integer> soldWays,
            @Param("couponId") Integer couponId);

    /**
     * 查询自有平台卡券售出明细
     */
    List<CouponSoldDetailVo> listCouponSoldDetailByParam(
            @Param("cardNumber") String cardNumber,
            @Param("soldTarget") String soldTarget,
            @Param("allocateOrgIds") List<Integer> allocateOrgIds,
            @Param("soldTypes") List<Integer> soldTypes,
            @Param("soldStartDate") LocalDate soldStartDate,
            @Param("soldEndDate") LocalDate soldEndDate,
            @Param("soldWays") List<Integer> soldWays,
            @Param("chargeStatus") List<Integer> chargeStatus,
            @Param("remark") String remark,
            @Param("couponId") Integer couponId);

    /**
     * 查询三方平台卡券激活明细
     */
    List<CouponActiveDetailVo> listCouponActiveByParam(
            @Param("cardNumber") String cardNumber,
            @Param("soldTarget") String soldTarget,
            @Param("soldChannelIds") List<Integer> soldChannelIds,
            @Param("activeStartDate") LocalDate activeStartDate,
            @Param("activeEndDate") LocalDate activeEndDate,
            @Param("activeOrgIds") List<Integer> activeOrgIds,
            @Param("couponId") Integer couponId);

    /**
     * 产品售出统计-时间维度
     */
    List<CardSoldStatisticsVo> listAllCardSoldParam(
            @Param("couponName") String couponName,
            @Param("couponTypes") List<Integer> couponTypes,
            @Param("cardNumber") String cardNumber,
            @Param("soldTarget") String soldTarget,
            @Param("allocateOrgIds") List<Integer> allocateOrgIds,
            @Param("soldTypes") List<Integer> soldTypes,
            @Param("soldStartDate") LocalDate soldStartDate,
            @Param("soldEndDate") LocalDate soldEndDate,
            @Param("soldWays") List<Integer> soldWays,
            @Param("remark") String remark,
            @Param("chargeStatus") List<Integer> chargeStatus);

    /**
     * 充值卡充值统计-充值统计
     */
    List<RechargeDetailVo> listRechargeDetailByParam(
            @Param("cardNumber") String cardNumber,
            @Param("patientKeyWord") String patientKeyWord,
            @Param("rechargeOrgIds") List<Integer> rechargeOrgIds,
            @Param("rechargeAccount") String rechargeAccount,
            @Param("rechargeStartDate") LocalDate rechargeStartDate,
            @Param("rechargeEndDate") LocalDate rechargeEndDate,
            @Param("couponId") Integer couponId);

    /**
     * 产品记录-产品售出记录
     */
    List<CouponSoldRecordVo> listCardSoldRecord(
            @Param("orgId") Integer orgId,
            @Param("soldStartDate") LocalDate soldStartDate,
            @Param("soldEndDate") LocalDate soldEndDate,
            @Param("couponName") String couponName,
            @Param("cardNumber") String cardNumber,
            @Param("soldTarget") String soldTarget,
            @Param("soldPhoneNumber") String soldPhoneNumber,
            @Param("couponTypes") List<Integer> couponTypes);

    /**
     * 根据条件查询产品售出明细记录
     *
     * @param query 查询条件
     * @return List<StatementProductSoldDetailVO>
     */
    List<StatementProductSoldDetailVO> selectProductSoldDetailList(
            @Param("query") StatementProductSoldDetailQuery query);

    /**
     * 根据卡ID查询卡券售出收费信息
     *
     * @param cardId 卡ID
     * @return StatementPaymentVO
     */
    StatementPaymentVO selectStatementPaymentByCardId(@Param("cardId") Integer cardId);

  /**
   * 产品激活明细
   */
    List<CardActiveVo> listCardActive(@Param("patientKeyword") String patientKeyword, @Param("activeOrgIds") List<Integer> activeOrgIds,
                                      @Param("activeStartDate") LocalDate activeStartDate, @Param("activeEndDate") LocalDate activeEndDate,
                                      @Param("couponId") Integer couponId, @Param("saleChannelId") Integer saleChannelId);
    List<CardDetaVo> listCardDetail(@Param("patientKeyword") String patientKeyword, @Param("activeOrgIds") List<Integer> activeOrgIds,
                                      @Param("activeStartDate") LocalDate activeStartDate, @Param("activeEndDate") LocalDate activeEndDate
            , @Param("cardIds") List<Integer> cardIds);

    /**
     * 产品激活报表
     * @param query
     * @return
     */
    List<CardActiveRecoedVO> getCardActiveRecoedPage(CardActiveRecoedQuery query);

    /**
     * 0-产品激活未使用提醒 1-产品即将到期提醒 2-产品到期提醒
     */
    List<WxCardEventVo> listWxPushCard(@Param("noticeType") Integer noticeType);

    List<CouponUseVo> getCouponUse(CouponUseQuery query);

    List<BaseCard> selectCardCouponSoldList(@Param("query") CardCouponUsedQueryForm query);

    /**
     * 查询产品卡券使用统计
     *
     * @param query
     * @return
     */
    List<CardCouponUsedDetailVO> selectCardCouponUsedDetail(@Param("query") CardCouponUsedDetailQueryForm query);

    List<BaseCard> selectProductSoldList(@Param("query") MultiClinicDateRangeQueryForm query);

    /**
     * 365卡产品售出激活统计表
     *
     * @param query
     * @return
     */
    List<Coupon365SoldActivedStatisticsVO> selectCoupon365SoldActivedStatstics(@Param("query") Coupon365SoldActivedStatisticsQuery query);

    /**
     * 根据条件查询365卡产品售出明细表
     *
     * @param query
     * @return
     */
    List<Coupon365SoldDetailVO> selectCoupon365SoldDetail(@Param("query") Coupon365SoldDetailQuery query);

    /**
     * 根据条件查询365卡产品激活明细表
     *
     * @param query
     * @return
     */
    List<Coupon365ActivedDetailVO> selectCoupon365ActivedDetail(@Param("query") Coupon365ActivedDetailQuery query);

    List<StatementDeductionSoldDetailVO> selectDeductionSoldDetailList(
            @Param("query") StatementDeductionSoldDetailQuery query);

    List<StatementDeductionRefundDetailVO> selectDeductionRefundDetailList(
            @Param("query") StatementDeductionRefundDetailQuery query);
}
