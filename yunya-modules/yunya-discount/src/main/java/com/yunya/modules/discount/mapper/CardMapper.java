package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.bo.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.models.discount.*;
import org.apache.ibatis.annotations.*;

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
}