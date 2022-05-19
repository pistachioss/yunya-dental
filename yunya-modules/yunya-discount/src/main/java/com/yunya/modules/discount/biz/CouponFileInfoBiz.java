package com.yunya.modules.discount.biz;


import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.discount.CouponFileInfo;

import com.yunya.models.discount.FileInfo;
import com.yunya.modules.discount.mapper.CouponFileInfoMapper;
import com.yunya.modules.discount.form.FileForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

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
        //清除之前的图片
        CouponFileInfo couponFiledelete = new CouponFileInfo();
        couponFiledelete.setCouponId(fileForm.getId());
        couponFiledelete.setFileType(new Byte("0"));
        mapper.delete(couponFiledelete);
        //清除之前的文档
        couponFiledelete.setCouponId(fileForm.getId());
        couponFiledelete.setFileType(new Byte("1"));
        mapper.delete(couponFiledelete);
        //图片信息
        if (fileForm.getPaths() != null && fileForm.getPaths().size() > 0) {
            for (FileInfo fileInfo : fileForm.getPaths()) {
                CouponFileInfo couponFileInfo = new CouponFileInfo();
                couponFileInfo.setFileName(fileInfo.getFileName());
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
        //文档信息
        if (fileForm.getDocs() != null && fileForm.getDocs().size() > 0) {
            for (FileInfo fileInfo : fileForm.getDocs()) {
                CouponFileInfo couponFileInfo = new CouponFileInfo();
                couponFileInfo.setFileName(fileInfo.getFileName());
                couponFileInfo.setCouponId(fileForm.getId());
                couponFileInfo.setInservice(true);
                couponFileInfo.setFileType(new Byte("1"));
                couponFileInfo.setPath(fileInfo.getPath());
                couponFileInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
                couponFileInfo.setCrtTime(new Date());
                list.add(couponFileInfo);
            }
        }
        if (list.size() > 0) {
            //插入文件信息
            return mapper.insertAll(list);
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

    public List<CouponFileInfo> listByCouponIds(Collection<Integer> ids) {
        Example example = new Example(CouponFileInfo.class);
        example.createCriteria().andIn("couponId", ids)
                .andEqualTo("inservice", true)
                .andEqualTo("fileType", 0);
        return mapper.selectByExample(example);
    }

}
