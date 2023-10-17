package com.yunya.framework.common.utils;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author: chenlin
 * @date: 2023/10/17 10:13
 * @description: 二维码生成工具类
 * @since: 1.0.0
 */
public class QRCodeUtl {

    private static QrConfig qrConfig = new QrConfig();

    /**
     * 生成二维码并保存到指定文件
     *
     * @param content
     * @param file
     */
    public static void generateFile(String content, File file){
        //生成到本地文件
        QrCodeUtil.generate(content, qrConfig, file);
    }

    /**
     * 生成二维码并输出到响应流
     *
     * @param content
     * @param response
     * @throws IOException
     */
    public static void generateAsStream(String content, HttpServletResponse response) throws IOException {
        QrCodeUtil.generate(content, qrConfig,"png", response.getOutputStream());
    }
}
