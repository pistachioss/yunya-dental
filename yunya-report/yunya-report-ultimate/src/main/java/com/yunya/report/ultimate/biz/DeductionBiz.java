//package com.yunya.report.ultimate.biz;
//
//import com.github.pagehelper.Page;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import com.google.common.collect.Maps;
//import com.yunya.feign.discount.RemoteDiscountFeign;
//import com.yunya.feign.report.domain.query.*;
//import com.yunya.feign.report.domain.vo.*;
//import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
//import com.yunya.feign.wechat.domain.model.WxTemplateMsgModel;
//import com.yunya.feign.wechat.enums.TemplateDataEnum;
//import com.yunya.framework.common.utils.StringHelper;
//import com.yunya.framework.common.utils.poi.ExcelUtil;
//import com.yunya.models.report.*;
//import com.yunya.models.tariff.BaseOralTariff;
//import com.yunya.models.tariff.BaseTariff;
//import com.yunya.report.ultimate.mapper.*;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.*;
//
//import static com.yunya.feign.wechat.enums.TemplateEnum.*;
//import static java.util.stream.Collectors.*;
//
///**
// * @author xiangyang
// * @date 2020/10/26
// */
//@Service
//@Slf4j
//public class DeductionBiz {
//    @Resource
//    private BaseCouponMapper couponMapper;
//    @Resource
//    private BaseCardMapper cardMapper;
//    @Resource
//    private BaseCouponItemMapper itemMapper;
//    @Resource
//    private BaseBenefitMapper benefitMapper;
//    @Resource
//    private BaseOrganizationMapper orgMapper;
//    @Resource
//    private RemoteDiscountFeign discountFeign;
//    @Resource
//    private BaseEmployeeMapper employeeMapper;
//    @Resource
//    private BasePatientMapper patientMapper;
//    @Resource
//    private RemoteTreatmentServiceFeign treatmentServiceFeign;
//
//    /**
//     * 产品售出激活统计
//     *
//     * @param query query
//     * @return page
//     */
//    public PageInfo<CouponStatisticsVo> getCouponStatisticsPage(CouponStatisticsQuery query) {
//        Page<CouponStatisticsVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
//        couponMapper.listCouponStatistics(
//                query.getCouponName(), query.getCouponCategoryIds(), query.getCouponTypes());
//        return new PageInfo<>(page);
//    }
//
//    /**
//     * 产品售出激活卡券明细（代金、折扣、兑换、套餐）
//     *
//     * @param query query
//     * @return page
//     */
//    public PageInfo<CardStatisticsVo> getCardStatisticsPage(
//            Integer couponId, CardStatisticsQuery query) {
//        Page<CardStatisticsVo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
//        if (query.getSoldWays().contains(2)) {
//            query.setRemark("小程序虚拟服务售卖");
//        }
//        List<CardStatisticsVo> cardStatisticsVos =
//                cardMapper.listCardByParam(
//                        query.getCardNumber(),
//                        query.getAllocateOrgIds(),
//                        query.getSoldTypes(),
//                        query.getSoldStartDate(),
//                        query.getSoldEndDate(),
//                        query.getActiveOrgIds(),
//                        query.getActiveStartDate(),
//                        query.getActiveEndDate(),
//                        query.getSoldWays(),
//                        query.getChargeStatus(),
//                        query.getRemark(),
//                        couponId);
//        cardStatisticsVos.forEach(
//                vo -> {
//                    if (StringUtils.isNotBlank(vo.getCardPassword())) {
//                        vo.setCardPassword(new String(Base64.getDecoder().decode(vo.getCardPassword().trim())));
//                    }
//                });
//        return new PageInfo<>(page);
//    }
//
//
//    /**
//     * 导出卡券使用记录列表
//     *
//     * @param response
//     * @param query
//     */
//    public void exportCardConsumeRecord(HttpServletResponse response, CardConsumeQuery query)
//            throws IOException {
//        query.setWhetherPage(false);
//        PageInfo<CardConsumeRecordVO> list = getCardConsumeRecordList(query);
//        ExcelUtil<CardConsumeRecordVO> excelUtil = new ExcelUtil<>(CardConsumeRecordVO.class);
//        String fileName =
//                String.format(
//                        "%s%s%s%s",
//                        "销售渠道消费报表统计", query.getActivationStartDate(), "-", query.getActivationEndDate());
//        excelUtil.exportExcel(response, list.getList(), "卡券消费统计列表", fileName);
//    }
//}
