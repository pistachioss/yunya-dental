package com.yunya365.aliyunoss.controller;

import com.yunya.feign.oss.domain.model.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.Base64Utils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.aliyunoss.enums.BucketFolderEnum;
import com.yunya365.aliyunoss.util.OssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.*;


@RestController
@Api(tags = "ossCategory说明,1:诊所,2:员工,3:患者,4:优惠活动,5:考勤,6:现金结存,7:商品价目,8:用户,9:小程序相关,0:临时文件夹.")
public class BaseController {

    private String getSuffixName(final String fileName) {
        if (fileName == null) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String getFileName(final String fullName) {

        return fullName.substring(fullName.lastIndexOf("/") + 1);
    }

    private String getSubFolder(final Integer compId, final Integer catId, final Integer objId) {

        final BucketFolderEnum bucketFolderEnum = BucketFolderEnum.values()[catId];
        return bucketFolderEnum.getBaseFolder(compId, objId);
    }

    private String getSubFolder(final OssFolderForm ossFolderForm) {

        return this.getSubFolder(ossFolderForm.getCompanyId(), ossFolderForm.getOssCategory(), ossFolderForm.getObjectId());
    }

    private String makeObjectFullName(final OssUploadForm ossUploadForm) {

        final String fileName = ossUploadForm.getFile().getOriginalFilename();
        final String suffixName = this.getSuffixName(fileName);
        if (ossUploadForm.getOssCategory() < 0 || ossUploadForm.getOssCategory() >= BucketFolderEnum.values().length) {
            ossUploadForm.setOssCategory(0);
        }
        return this.getSubFolder(ossUploadForm) + UUID.randomUUID() + suffixName;
    }

    private String makeObjectFullName(final OssUrlForm ossUrlForm) {

        return this.getSubFolder(ossUrlForm) + ossUrlForm.getOssFilename();
    }

    private String makeObjectFullName(final OssUrlForm ossUrlForm, final Boolean isNewFileName) {

        final String subFolder = this.getSubFolder(ossUrlForm);
        final String objectName;
        if (isNewFileName) {
            final String suffixName = this.getSuffixName(ossUrlForm.getOssFilename());
            objectName = subFolder + UUID.randomUUID() + suffixName;
        } else {
            objectName = subFolder + ossUrlForm.getOssFilename();
        }
        return objectName;
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ApiOperation("1.单资源：上传")
    public ResponseResult uploadfile(@RequestParam("file") final MultipartFile file, final OssUploadForm ossUploadForm) throws Exception {

        ossUploadForm.setFile(file);
        final String objectName = this.makeObjectFullName(ossUploadForm);
        OssUtil.putObject(objectName, ossUploadForm.getFile().getInputStream());
        return ResponseUtil.success(this.getFileName(objectName));
    }

    @RequestMapping(value = "uploadBase64Image", method = RequestMethod.POST)
    @ApiOperation("1.单资源：上传")
    public ResponseResult uploadBase64Image(@Validated @RequestBody Base64UploadForm form) throws Exception {
        String data = form.getData();
        byte[] buf = Base64Utils.decoderImage(data);
        InputStream imgIn = new ByteArrayInputStream(buf);
        String contentType = URLConnection.guessContentTypeFromStream(imgIn);
        String fileName = form.getFileName() + "." + contentType.substring(contentType.lastIndexOf("/")+1);
        form.setFile(new MockMultipartFile(fileName, fileName, contentType, buf));
        final String objectName = this.makeObjectFullName(form);
        OssUtil.putObject(objectName, form.getFile().getInputStream());
        return ResponseUtil.success(this.getFileName(objectName));
    }

    @RequestMapping(value = "upload/multi", method = RequestMethod.POST, consumes = "multipart/form-data")
    @ApiOperation("1.多资源：上传")
    public ResponseResult uploadfile(@RequestParam("files") final List<MultipartFile> files, final OssFolderForm ossFolerForm) throws Exception {

        final List<String> urls = new ArrayList<>();
        files.forEach(file -> {
            try{
                OssUploadForm ossUploadForm = new OssUploadForm();
                ossUploadForm.setFile(file);
                ossUploadForm.setCompanyId(ossFolerForm.getCompanyId());
                ossUploadForm.setOssCategory(ossFolerForm.getOssCategory());
                ossUploadForm.setObjectId(ossFolerForm.getObjectId());
                final String objectName = this.makeObjectFullName(ossUploadForm);
                OssUtil.putObject(objectName, ossUploadForm.getFile().getInputStream());
                urls.add(this.getFileName(objectName));
            }
            catch(Exception e){

            }
        });
        return ResponseUtil.success(urls);
    }

    @RequestMapping(value = "url", method = RequestMethod.POST)
    @ApiOperation("2.单资源：获取外网访问URL")
    public ResponseResult getUrl(@RequestBody final OssUrlForm ossUrlForm) throws Exception {

        final String objectName = this.makeObjectFullName(ossUrlForm);
        final String url = OssUtil.getSignatureUrl(objectName, ossUrlForm);
        return ResponseUtil.success(url);
    }

    @RequestMapping(value = "url/multi", method = RequestMethod.POST)
    @ApiOperation("2.多资源：获取外网访问URL列表")
    public ResponseResult getUrl(@RequestBody final List<OssUrlForm> ossUrlForms) throws Exception {

        final List<String> urls = new ArrayList<>();
        ossUrlForms.forEach(ossUrlForm -> {
            final String objectName = this.makeObjectFullName(ossUrlForm);
            final String url = OssUtil.getSignatureUrl(objectName, ossUrlForm);
            urls.add(url);
        });

        return ResponseUtil.success(urls);
    }


    @RequestMapping(value = "url/map", method = RequestMethod.POST)
    @ApiOperation("2.多资源：获取外网访问URL列表")
    public ResponseResult<Map<String, String>> getUrlMap(@RequestBody final List<OssUrlForm> ossUrlForms) {
        Map<String, String> result = new HashMap<>(16);
        ossUrlForms.forEach(ossUrlForm -> {
            final String objectName = this.makeObjectFullName(ossUrlForm);
            final String url = OssUtil.getSignatureUrl(objectName, ossUrlForm);
            result.put(ossUrlForm.getOssFilename(), url);
        });
        return ResponseUtil.success(result);
    }

    @RequestMapping(value = "copy", method = RequestMethod.POST)
    @ApiOperation("3.单资源：复制资源文件（可实现重命名）")
    public ResponseResult copy(@RequestBody final OssCopyForm ossCopyForm) throws Exception {

        if (!ossCopyForm.getDestForm().getCompanyId().equals(ossCopyForm.getSrcForm().getCompanyId())) {
            throw new Exception("不能复制到不同注册公司");
        }
        final String src = this.makeObjectFullName(ossCopyForm.getSrcForm());
        final String dest = this.makeObjectFullName(ossCopyForm.getDestForm(), ossCopyForm.getIsNewFileName());
        OssUtil.copyObject(src, dest);
        return ResponseUtil.success(this.getFileName(dest));
    }

    @RequestMapping(value = "signature", method = RequestMethod.POST)
    @ApiOperation("0.获取签名，以便上传或获取文件等（服务器签名后js直传）")
    public ResponseResult getSignedUrl(final OssFolderForm ossFolderForm) throws Exception {

        final String subFolder = this.getSubFolder(ossFolderForm);
        return ResponseUtil.success(OssUtil.getSignature(subFolder));
    }
}
