package com.yunya365.aliyunoss.controller;

import com.aliyun.oss.model.PutObjectResult;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.aliyunoss.enums.BucketFolderEnum;
import com.yunya365.aliyunoss.util.OssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/tmp")
@Api(tags = "资源相关接口，测试用")
public class BaseTmpController {

    private String getSuffixName(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String getFileName(String fullName) {
        return fullName.substring(fullName.lastIndexOf("/") + 1);
    }

    private String makeObjectFullName(Integer oss_category, Integer id, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String suffixName = getSuffixName(fileName);
        if (oss_category < 0 || oss_category >= BucketFolderEnum.values().length) {
            oss_category = 0;
        }
        BucketFolderEnum bucketFolderEnum = BucketFolderEnum.values()[oss_category];
        String objectName = bucketFolderEnum.getBaseFolder(id) + UUID.randomUUID().toString() + suffixName;
        return objectName;
    }

    private String makeObjectFullName(Integer oss_category, Integer id, String filename) {
        BucketFolderEnum bucketFolderEnum = BucketFolderEnum.values()[oss_category];
        String objectName = bucketFolderEnum.getBaseFolder(id) + filename;
        return objectName;
    }

    @Deprecated
    @RequestMapping(value = "upload/{ossCategory}/{id}", method = RequestMethod.POST)
    @ApiOperation("1.单资源：上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ossCategory", value = "资源分类", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "id", value = "资源分类类别所对应对象ID", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "image_File", value = "上传的资源文件", required = true, paramType = "form", dataType = "__file")
    })
    public ResponseResult uploadClinicShow(@PathVariable("ossCategory") Integer oss_category,
                                           @PathVariable("id") Integer id,
                                           @RequestParam(value = "image_File") MultipartFile file) throws Exception {

        String objectName = makeObjectFullName(oss_category, id, file);
        PutObjectResult result = OssUtil.putObject(objectName, file.getInputStream());
        return ResponseUtil.success(getFileName(objectName));
    }

    @Deprecated
    @RequestMapping(value = "url/{ossCategory}/{id}", method = RequestMethod.GET)
    @ApiOperation("2.单资源：获取外网访问URL")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ossCategory", value = "资源分类", required = true, paramType = "path", dataType = "BucketFolderEnum"),
            @ApiImplicitParam(name = "id", value = "资源分类类别所对应对象ID", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "oss_filename", value = "资源文件存储路径", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "is_Thumb", value = "是否缩略图", paramType = "query", dataType = "Boolean")
    })
    public ResponseResult getClinicUrl(@PathVariable("ossCategory") Integer oss_category,
                                            @PathVariable("id") Integer id,
                                            @RequestParam(value = "oss_filename") String filename,
                                            @RequestParam(value = "is_Thumb") Boolean isThumb) throws Exception {

        String objectName = makeObjectFullName(oss_category, id, filename);
        URL url = OssUtil.getSignatureUrl(objectName, isThumb);
        return ResponseUtil.success(url);
    }
}
