package com.yunya365.mini.mapper;

import com.yunya.feign.ivy_mini.domain.vo.BannerVO;
import com.yunya365.mini.entity.Banner;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BannerMapper extends Mapper<Banner> {

    List<BannerVO> findBannerList();
}