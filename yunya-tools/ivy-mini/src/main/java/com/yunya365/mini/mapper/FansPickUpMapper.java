package com.yunya365.mini.mapper;

import com.yunya.feign.ivy_mini.domain.vo.FansPickUpVO;
import com.yunya365.mini.entity.FansPickUp;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface FansPickUpMapper extends Mapper<FansPickUp> {

     List<FansPickUpVO> findList(Integer fansId);

     void updateDefaultStatus(Integer fansIds);
}