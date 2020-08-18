package com.yunya.modules.discount.biz;


import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.discount.CouponFileInfo;

import com.yunya.modules.discount.mapper.CouponFileInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public int insetAll(List<CouponFileInfo> list){
    return mapper.insertAll(list);
  }

}
