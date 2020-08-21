package com.yunya.modules.discount.biz;


import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.discount.CouponFileInfo;

import com.yunya.models.discount.FileInfo;
import com.yunya.modules.discount.mapper.CouponFileInfoMapper;
import com.yunya.modules.discount.form.FileForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 杨柳絮
 * @className CouponFileInfoBiz
 * @description
 * @date 2020/8/18 14:20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CouponFileInfoBiz extends BaseBiz<CouponFileInfoMapper, CouponFileInfo> {

    /**
     * 修改卡券文件信息
     *
     * @param fileForm
     * @return
     */
    public Integer saveOrUpdateFile(FileForm fileForm) {
        List<CouponFileInfo> list = new ArrayList<>();
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        if (fileForm.getPaths() != null && fileForm.getPaths().size() > 0) {//图片信息
            couponFiledelete.setCouponId(fileForm.getId());
            couponFiledelete.setFileType(new Byte("0"));
            mapper.delete(couponFiledelete);//清除之前的图片
            for (FileInfo fileInfo : fileForm.getPaths()) {
                CouponFileInfo couponFileInfo = new CouponFileInfo();
                couponFileInfo.setCouponId(fileForm.getId());
                couponFileInfo.setInservice(true);
                couponFileInfo.setRemark(fileInfo.getMark());
                couponFileInfo.setFileType(new Byte("0"));
                couponFileInfo.setPath(fileInfo.getPath());
                couponFileInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                couponFileInfo.setCrtTime(new Date());
                list.add(couponFileInfo);
            }
        }
        if (fileForm.getDocs() != null && fileForm.getDocs().size() > 0) {//文档信息
            couponFiledelete.setCouponId(fileForm.getId());
            couponFiledelete.setFileType(new Byte("1"));
            mapper.delete(couponFiledelete);//清除之前的文档
            for (String doc : fileForm.getDocs()) {
                CouponFileInfo couponFileInfo = new CouponFileInfo();
                couponFileInfo.setCouponId(fileForm.getId());
                couponFileInfo.setInservice(true);
                couponFileInfo.setFileType(new Byte("1"));
                couponFileInfo.setPath(doc);
                couponFileInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                couponFileInfo.setCrtTime(new Date());
                list.add(couponFileInfo);
            }
        }
        if (list.size() > 0) {
            return mapper.insertAll(list);//插入文件信息
        }
        return 0;
    }

    /**
     * 获取文件信息
     *
     * @param fileForm
     * @return
     */
    public List<CouponFileInfo> findFile(FileForm fileForm) {
        CouponFileInfo couponFileInfo = new CouponFileInfo();
        couponFileInfo.setCouponId(fileForm.getId());
        return mapper.select(couponFileInfo);
    }

}
