package com.yunya365.aliyunoss.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.aliyunoss.enums.BucketFolderEnum;
import com.yunya365.aliyunoss.form.OssCopyForm;
import com.yunya365.aliyunoss.form.OssFolderForm;
import com.yunya365.aliyunoss.form.OssUploadForm;
import com.yunya365.aliyunoss.form.OssUrlForm;
import com.yunya365.aliyunoss.util.OssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@Api(tags = "资源相关接口，oss_category分为1:诊所,2:员工,3:患者,4:优惠活动，其它将放入0:临时文件夹")
public class BaseController {

    private String getSuffixName(String fileName) {
        if (fileName == null) return "";
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String getFileName(String fullName) {

        return fullName.substring(fullName.lastIndexOf("/") + 1);
    }

    private String getSubFolder(Integer compId, Integer catId, Integer objId) {

        BucketFolderEnum bucketFolderEnum = BucketFolderEnum.values()[catId];
        return bucketFolderEnum.getBaseFolder(compId, objId);
    }

    private String getSubFolder(OssFolderForm ossFolderForm) {

        return getSubFolder(ossFolderForm.getCompanyId(), ossFolderForm.getOssCategory(), ossFolderForm.getObjectId());
    }

    private String makeObjectFullName(OssUploadForm ossUploadForm) {

        String fileName = ossUploadForm.getFile().getOriginalFilename();
        String suffixName = getSuffixName(fileName);
        if (ossUploadForm.getOssCategory() < 0 || ossUploadForm.getOssCategory() >= BucketFolderEnum.values().length) {
            ossUploadForm.setOssCategory(0);
        }
        return getSubFolder(ossUploadForm) + UUID.randomUUID().toString() + suffixName;
    }

    private String makeObjectFullName(OssUrlForm ossUrlForm) {

        return getSubFolder(ossUrlForm) + ossUrlForm.getOssFilename();
    }

    private String makeObjectFullName(OssUrlForm ossUrlForm, Boolean isNewFileName) {

        String subFolder = getSubFolder(ossUrlForm);
        String objectName;
        if (isNewFileName) {
            String suffixName = getSuffixName(ossUrlForm.getOssFilename());
            objectName = subFolder + UUID.randomUUID().toString() + suffixName;
        } else {
            objectName = subFolder + ossUrlForm.getOssFilename();
        }
        return objectName;
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ApiOperation("1.单资源：上传")
    public ResponseResult uploadfile(@RequestParam("file") MultipartFile file, OssUploadForm ossUploadForm) throws Exception {

        ossUploadForm.setFile(file);
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

    @RequestMapping(value = "url/multi", method = RequestMethod.POST)
    @ApiOperation("2.多资源：获取外网访问URL列表")
    public ResponseResult getUrl(@RequestBody List<OssUrlForm> ossUrlForms) throws Exception {

        List<URL> urls = new ArrayList<>();
        ossUrlForms.forEach(ossUrlForm -> {
            String objectName = makeObjectFullName(ossUrlForm);
            URL url = OssUtil.getSignatureUrl(objectName, ossUrlForm.getIsThumb());
            urls.add(url);
        });

        return ResponseUtil.success(urls);
    }

    @RequestMapping(value = "copy", method = RequestMethod.POST)
    @ApiOperation("3.单资源：复制资源文件（可实现重命名）")
    public ResponseResult copy(@RequestBody OssCopyForm ossCopyForm) throws Exception {

        if (!ossCopyForm.getDestForm().getCompanyId().equals(ossCopyForm.getSrcForm().getCompanyId())) {
            throw new Exception("不能复制到不同注册公司");
        }
        String src = makeObjectFullName(ossCopyForm.getSrcForm());
        String dest = makeObjectFullName(ossCopyForm.getDestForm(), ossCopyForm.getIsNewFileName());
        OssUtil.copyObject(src, dest);
        return ResponseUtil.success(getFileName(dest));
    }

    @RequestMapping(value = "signature", method = RequestMethod.POST)
    @ApiOperation("0.获取签名，以便上传或获取文件等（服务器签名后js直传）")
    public ResponseResult getSignedUrl(OssFolderForm ossFolderForm) throws Exception {

        String subFolder = getSubFolder(ossFolderForm);
        return ResponseUtil.success(OssUtil.getSignature(subFolder));
    }
}
