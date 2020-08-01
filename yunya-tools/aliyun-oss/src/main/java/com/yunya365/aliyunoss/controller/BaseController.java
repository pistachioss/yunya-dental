package com.yunya365.aliyunoss.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.aliyunoss.enums.BucketFolderEnum;
import com.yunya365.aliyunoss.form.OssCopyForm;
import com.yunya365.aliyunoss.form.OssUploadForm;
import com.yunya365.aliyunoss.form.OssUrlForm;
import com.yunya365.aliyunoss.util.OssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Map;
import java.util.UUID;


@RestController
@Api(tags = "资源相关接口，oss_category分为1:诊所,2:员工,3:患者,4:优惠活动，其它将放入0:临时文件夹")
public class BaseController {

    private String getSuffixName(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String getFileName(String fullName) {
        return fullName.substring(fullName.lastIndexOf("/") + 1);
    }

    private String makeObjectFullName(OssUploadForm ossUploadForm) {
        String fileName = ossUploadForm.getFile().getOriginalFilename();
        String suffixName = getSuffixName(fileName);
        if (ossUploadForm.getOssCategory() < 0 || ossUploadForm.getOssCategory() >= BucketFolderEnum.values().length) {
            ossUploadForm.setOssCategory(0);
        }
        BucketFolderEnum bucketFolderEnum = BucketFolderEnum.values()[ossUploadForm.getOssCategory()];
        String subFolder = bucketFolderEnum.getBaseFolder(ossUploadForm.getCompanyId(), ossUploadForm.getObjectId());
        String objectName = subFolder + UUID.randomUUID().toString() + suffixName;
        return objectName;
    }

    private String makeObjectFullName(OssUrlForm ossUrlForm) {
        BucketFolderEnum bucketFolderEnum = BucketFolderEnum.values()[ossUrlForm.getOssCategory()];
        String subFolder = bucketFolderEnum.getBaseFolder(ossUrlForm.getCompanyId(), ossUrlForm.getObjectId());
        String objectName = subFolder + ossUrlForm.getOssFilename();
        return objectName;
    }

    private String makeObjectFullName(OssUrlForm ossUrlForm, Boolean isNewFileName) {
        BucketFolderEnum bucketFolderEnum = BucketFolderEnum.values()[ossUrlForm.getOssCategory()];
        String subFolder = bucketFolderEnum.getBaseFolder(ossUrlForm.getCompanyId(), ossUrlForm.getObjectId());
        String objectName;
        if(isNewFileName){
            String suffixName = getSuffixName(ossUrlForm.getOssFilename());
            objectName = subFolder + UUID.randomUUID().toString() + suffixName;
        }
        else{
            objectName = subFolder + ossUrlForm.getOssFilename();
        }
        return objectName;
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ApiOperation("1.单资源：上传")
    public ResponseResult uploadfile(@RequestBody OssUploadForm ossUploadForm) throws Exception {

        String objectName = makeObjectFullName(ossUploadForm);
        OssUtil.putObject(objectName, ossUploadForm.getFile().getInputStream());
        return ResponseUtil.success(getFileName(objectName));
    }

    @RequestMapping(value = "url", method = RequestMethod.POST)
    @ApiOperation("2.单资源：获取外网访问URL")
    public ResponseResult getUrl(@RequestBody OssUrlForm ossUrlForm) throws Exception {

        String objectName = makeObjectFullName(ossUrlForm);
        URL url = OssUtil.getSignatureUrl(objectName, ossUrlForm.getIsThumb());
        return ResponseUtil.success(url);
    }

    @RequestMapping(value = "copy", method = RequestMethod.POST)
    @ApiOperation("3.单资源：复制资源文件（可实现重命名）")
    public ResponseResult copy(@RequestBody OssCopyForm ossCopyForm) throws Exception {
        if(ossCopyForm.getDestForm().getCompanyId() != ossCopyForm.getSrcForm().getCompanyId()){
            throw new Exception("不能复制到不同注册公司");
        }
        String src = makeObjectFullName(ossCopyForm.getSrcForm());
        String dest = makeObjectFullName(ossCopyForm.getDestForm(), ossCopyForm.getIsNewFileName());
        OssUtil.copyObject(src, dest);
        return ResponseUtil.success(getFileName(dest));
    }
}
