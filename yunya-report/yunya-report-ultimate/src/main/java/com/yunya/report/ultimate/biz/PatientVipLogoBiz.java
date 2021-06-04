package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.VipLogoQueryForm;
import com.yunya.feign.report.domain.vo.VipLogoVo;
import com.yunya.feign.report.domain.vo.VipRateVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BasePatient;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class PatientVipLogoBiz extends BaseBiz<BasePatientMapper, BasePatient> {

    public void updateVipLogoInfo(Integer id) {
        mapper.updateFirstVisitInfo();
        mapper.updateLastVisitInfo();
        mapper.updateNumberOfVisit();
        mapper.updateTotalAmount();
        if (id == 9528) {
            mapper.updateVipLogoOld();
        }
        mapper.updateVipLogo();
    }

    public PageInfo<VipLogoVo> findVipLogoList(VipLogoQueryForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<VipLogoVo> basePatientNotSeenVoList = mapper.findVipLogoList(form);
        return new PageInfo<>(basePatientNotSeenVoList);
    }

    public void exportVipLogoList(HttpServletResponse response, VipLogoQueryForm query) throws IOException {
        query.setWhetherPage(false);
        List<VipLogoVo> vipLogoVos = findVipLogoList(query).getList();
        ExcelUtil<VipLogoVo> excelUtil = new ExcelUtil<>(VipLogoVo.class);
        String fileName = "会员统计表" + DateTime.now().toString("yyyyMMddHHmmss");
        excelUtil.exportExcel(response, vipLogoVos, "会员统计表", fileName);
    }

    public List<VipRateVo> getVipRate() {
        return mapper.getVipRate();
    }
}
