package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.oss.RemoteOssServiceFeign;
import com.yunya.feign.oss.domain.model.Base64UploadForm;
import com.yunya.feign.oss.domain.model.OssUrlForm;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.Base64Utils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.UUIDUtils;
import com.yunya.models.system.ClinicLiveCode;
import com.yunya.modules.system.domain.model.ClinicLiveCodeModel;
import com.yunya.modules.system.domain.query.ClinicLiveCodeQueryForm;
import com.yunya.modules.system.mapper.ClinicLiveCodeMapper;
import com.yunya.modules.system.vo.ClinicLiveCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 简介：门店店长活码
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/5/18 15:50
 * @since: 1.0.0
 */
@Service
public class ClinicLiveCodeBiz extends BaseBiz<ClinicLiveCodeMapper, ClinicLiveCode> {

    /** 文件上传*/
    @Autowired
    private RemoteOssServiceFeign remoteOssServiceFeign;
    /** 域名路径 */
    @Value("${domainUrl}")
    private String domainUrl;

    /**
     * 条件查询门店店长活码列表
     *
     * @param query
     * @return
     */
    public PageInfo<ClinicLiveCodeVO> findList(ClinicLiveCodeQueryForm query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<ClinicLiveCodeVO> result = mapper.selectClinicLiveCodeList(query);
        fullPath(result);
        return new PageInfo<>(result);
    }

    /**
     * 填充二维码图片的完整路径
     *
     * @param result
     */
    private void fullPath(List<ClinicLiveCodeVO> result) {
        if (StringHelper.isNotEmpty(result)) {
            List<OssUrlForm> forms = new ArrayList<>();
            result.forEach(vo->{
                String qrcode = vo.getQrcode();
                if (StringHelper.isNotEmpty(qrcode)) {
                    OssUrlForm form = new OssUrlForm();
                    form.setCompanyId(0);
                    form.setIsThumb(false);
                    form.setOssCategory(1);
                    form.setObjectId(vo.getOrgId());
                    form.setOssFilename(qrcode);
                    forms.add(form);
                }
            });
            Map<String, String> urlMap = remoteOssServiceFeign.getUrlMap(forms).getData();
            if (StringHelper.isNotEmpty(urlMap)) {
                result.forEach(vo -> {
                    String url = urlMap.get(vo.getQrcode());
                    if (StringHelper.isNotEmpty(url)) {
                        vo.setQrFullPath(domainUrl + url);
                    }
                });
            }
        }
    }

    /**
     * 添加门店店长活码
     *
     * @param file
     * @param model
     */
    public void add(MultipartFile file, ClinicLiveCodeModel model) {
        Integer orgId = model.getOrgId();
        Base64UploadForm form = new Base64UploadForm();
        form.setCompanyId(0);
        form.setOssCategory(1);
        form.setObjectId(orgId);
        form.setData(Base64Utils.getImageStr(file));
        form.setFileName(UUIDUtils.generateShortUuid());
        String qrcode = (String) remoteOssServiceFeign.uploadBase64Image(form).getData();
        ClinicLiveCode entity = new ClinicLiveCode();
        entity.setOrgId(orgId);
        entity.setQrcode(qrcode);
        entity.setCrtId(1);
        entity.setCrtTime(new Date(System.currentTimeMillis()));
        mapper.insertSelective(entity);
    }
}
