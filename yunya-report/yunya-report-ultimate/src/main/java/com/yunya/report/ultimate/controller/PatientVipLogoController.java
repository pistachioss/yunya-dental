package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.VipLogoQueryForm;
import com.yunya.feign.report.domain.vo.VipLogoVo;
import com.yunya.feign.report.domain.vo.VipRateVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.report.ultimate.biz.PatientVipLogoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("viplogo")
@Api(tags = "公司端报表-报表统计-CRM报表-会员分析")
public class PatientVipLogoController {

    /**
     * 注入服务
     */
    @Autowired
    private PatientVipLogoBiz patientVipLogoBiz;

    @GetMapping("/update/{id}")
    public ResponseResult update(@PathVariable("id") Integer id) {
        if (id != 9527 && id != 9528) {
            return ResponseUtil.error(500, "reject");
        }
        patientVipLogoBiz.updateVipLogoInfo(id);
        return ResponseUtil.success();
    }

    @ApiOperation("会员统计-根据条件查询会员统计列表")
    @PostMapping("/findlist")
    public ResponseResult<PageInfo<VipLogoVo>> findAll(@RequestBody VipLogoQueryForm form) {
        PageInfo<VipLogoVo> vipLogoVoPageInfo = patientVipLogoBiz.findVipLogoList(form);
        if (StringHelper.isNotNull(vipLogoVoPageInfo)) {
            return ResponseUtil.success(vipLogoVoPageInfo);
        }
        return ResponseUtil.fail(
                OperationCodeConstants.RETURN_VALUE_ISNULL, "暂无相关数据", null);
    }

    @ApiOperation("会员统计-根据条件查询会员统计列表并导出Excel")
    @PostMapping(value = "/export", name = "根据条件查询会员统计列表并导出Excel")
    public void exportVipLogoList(
            HttpServletResponse response, @RequestBody @Validated VipLogoQueryForm query)
            throws IOException {
        patientVipLogoBiz.exportVipLogoList(response, query);
    }

    @ApiOperation("会员占比-获取老会员与365卡会员的占比情况")
    @GetMapping("/viprate")
    public ResponseResult<List<VipRateVo>> getVipRate() {
        List<VipRateVo> vipRateVos = patientVipLogoBiz.getVipRate();
        return ResponseUtil.success(vipRateVos);
    }
}
