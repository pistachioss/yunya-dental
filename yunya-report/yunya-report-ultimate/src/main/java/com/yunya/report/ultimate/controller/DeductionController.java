//package com.yunya.report.ultimate.controller;
//
//import com.alibaba.excel.EasyExcel;
//import com.github.pagehelper.PageInfo;
//import com.yunya.feign.report.domain.query.CouponUseQuery;
//import com.yunya.feign.report.domain.query.DeductionPeriodQuery;
//import com.yunya.feign.report.domain.vo.CouponUseVo;
//import com.yunya.framework.common.model.ResponseResult;
//import com.yunya.framework.common.utils.ResponseUtil;
//import com.yunya.report.ultimate.biz.DiscountBiz;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.Valid;
//import java.io.IOException;
//
//@Api(tags = {"划扣表接口"})
//@Slf4j
//@RestController
//public class DeductionController {
//	@Resource
//	private DiscountBiz discountBiz;
//
//	@ApiOperation(value = "公司端-报表统计-市场报表-产品使用报表")
//	@PostMapping("/coupon/use/page")
//	public ResponseResult<PageInfo<CouponUseVo>> getCouponUse(@RequestBody DeductionPeriodQuery query) {
//		return ResponseUtil.success(discountBiz.getCouponUse(query));
//	}
//	@ApiOperation(value = "公司端-产品使用报表-导出")
//	@PostMapping("/coupon/use/page/export")
//	public void getCouponUseExport(HttpServletResponse response,@Valid @RequestBody CouponUseQuery query) throws IOException {
//		discountBiz.buildResponse(response, "产品使用记录");
//		EasyExcel.write(response.getOutputStream(), CouponUseVo.class)
//				.sheet("sheet").doWrite(discountBiz.getCouponUse(query).getList());
//	}
//}
