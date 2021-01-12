package com.yunya365.aliyunoss.util;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import com.yunya365.aliyunoss.property.AliyunOssProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class OssUtil {

    private static String endpoint;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String bucket;
    private static String prefixPath;

    private static OSS ossClient;

    @Autowired
    private static AliyunOssProperties aliyunOssProperties;

    private static void initOSSClient() {
        try {
            if(endpoint == null || endpoint.isEmpty()){
                endpoint = aliyunOssProperties.getEndpoint();
                accessKeyId = aliyunOssProperties.getAccessKeyId();
                accessKeySecret = aliyunOssProperties.getAccessKeySecret();
                bucket = aliyunOssProperties.getBucket();
                prefixPath = aliyunOssProperties.getPrefixPath();
                if(!prefixPath.isEmpty() && !prefixPath.endsWith("/")){
                    prefixPath = prefixPath + "/";
                }
            }
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            boolean exists = ossClient.doesBucketExist(bucket);
            if (!exists) {
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
                ossClient.createBucket(createBucketRequest);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PutObjectResult putObject(String fullPathName, InputStream ins) {
        if (ossClient == null) {
            initOSSClient();
        }
        fullPathName = prefixPath + fullPathName;

        ObjectMetadata metadata = new ObjectMetadata();
        // 指定上传文件操作时是否覆盖同名Object。
        // 不指定x-oss-forbid-overwrite时，默认覆盖同名Object。
        // 指定x-oss-forbid-overwrite为false时，表示允许覆盖同名Object。
        // 指定x-oss-forbid-overwrite为true时，表示禁止覆盖同名Object，如果同名Object已存在，程序将报错。
        metadata.setHeader("x-oss-forbid-overwrite", "true");

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fullPathName, ins);
        putObjectRequest.setMetadata(metadata);
        PutObjectResult result = ossClient.putObject(putObjectRequest);
//        ossClient.shutdown();
//        ossClient = null;
        return result;
    }

    public static String getSignatureUrl(String fullPathName, Boolean isThumb) {
        if (ossClient == null) {
            initOSSClient();
        }
        fullPathName = prefixPath + fullPathName;

        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, fullPathName, HttpMethod.GET);
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        req.setExpiration(expiration);
        if (isThumb) {
            String style = "image/resize,m_lfit,h_100,w_100";
            req.setProcess(style);
        }
        URL result = ossClient.generatePresignedUrl(req);
//        ossClient.shutdown();
//        ossClient = null;
        return replaceUrl(result);
    }

    public static CopyObjectResult copyObject(String srcFullPathName, String dest_FullPathName) {
        if (ossClient == null) {
            initOSSClient();
        }
        srcFullPathName = prefixPath + srcFullPathName;
        dest_FullPathName = prefixPath + dest_FullPathName;

        ObjectMetadata metadata = new ObjectMetadata();
        // 指定上传文件操作时是否覆盖同名Object。
        // 不指定x-oss-forbid-overwrite时，默认覆盖同名Object。
        // 指定x-oss-forbid-overwrite为false时，表示允许覆盖同名Object。
        // 指定x-oss-forbid-overwrite为true时，表示禁止覆盖同名Object，如果同名Object已存在，程序将报错。
        metadata.setHeader("x-oss-forbid-overwrite", "true");

        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, srcFullPathName, bucket, dest_FullPathName);
        copyObjectRequest.setNewObjectMetadata(metadata);
        CopyObjectResult result = ossClient.copyObject(copyObjectRequest);
//        ossClient.shutdown();
//        ossClient = null;
        return result;
    }

    public static Map<String, String> getSignature(String subFolder) {
        if (ossClient == null) {
            initOSSClient();
        }
        subFolder = prefixPath + subFolder;

        String endpoint_tmp = endpoint;
        if (endpoint.indexOf("://") >= 0) {
            endpoint_tmp = endpoint.substring(endpoint.indexOf("://") + 3);
        }
        String host = "https://" + bucket + "." + endpoint_tmp; // host的格式为 bucketname.endpoint
        String callbackUrl = "http://88.88.88.88:8888";// callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String dir = subFolder; // 用户上传文件时指定的前缀。
        try {
            long expireTime = 300;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("OSSAccessKeyId", accessKeyId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));

//            JSONObject jasonCallback = new JSONObject();
//            jasonCallback.put("callbackUrl", callbackUrl);
//            jasonCallback.put("callbackBody",
//                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
//            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
//            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
//            respMap.put("callback", base64CallbackBody);

            respMap.put("memo", "获取签名后直传参数详见：https://help.aliyun.com/document_detail/31988.html?spm=a2c4g.11186623.6.1511.35972e3cxPu9f6");
            return respMap;

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
//        ossClient.shutdown();
//        ossClient = null;
        }
        return null;
    }

    public static String replaceUrl(URL url){
        String urlStr = url.toString();
        urlStr = urlStr.replace(url.getProtocol() + "://" + url.getHost(), "img");
        return urlStr;
    }
}
