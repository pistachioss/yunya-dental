package com.yunya365.aliyunoss.util;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

public class OssUtil {

    private static final String ENDPOINT = "aliyun.oss.endpoint";
    private static final String ACCESS_KEY_ID = "aliyun.oss.access-key-id";
    private static final String ACCESS_KEY_SECRET = "aliyun.oss.access-key-secret";
    private static final String BUCKET = "aliyun.oss.bucket";

    private static String endpoint;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static OSS ossClient;
    private static String bucket;

    private static void initOSSClient() {
        try {
            InputStream ins = OssUtil.class.getClassLoader().getResource("oss.properties").openStream();
            Properties prop = new Properties();
            prop.load(ins);
            endpoint = prop.getProperty(ENDPOINT);
            accessKeyId = prop.getProperty(ACCESS_KEY_ID);
            accessKeySecret = prop.getProperty(ACCESS_KEY_SECRET);
            bucket = prop.getProperty(BUCKET);
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            boolean exists = ossClient.doesBucketExist(bucket);
            if (!exists) {
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
                ossClient.createBucket(createBucketRequest);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PutObjectResult putObject(String fullPathName, InputStream ins) {
        if (ossClient == null) {
            initOSSClient();
        }

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
        return result;
    }

    public static URL getSignatureUrl(String fullPathName, Boolean isThumb) {
        if (ossClient == null) {
            initOSSClient();
        }
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, fullPathName, HttpMethod.GET);
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        req.setExpiration(expiration);
        if (isThumb) {
            String style = "image/resize,m_lfit,h_100,w_100";
            req.setProcess(style);
        }
        URL result = ossClient.generatePresignedUrl(req);
//        ossClient.shutdown();
        return result;
    }

    public static CopyObjectResult copyObject(String srcFullPathName, String dest_FullPathName) {
        if (ossClient == null) {
            initOSSClient();
        }

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
        return result;
    }
}
