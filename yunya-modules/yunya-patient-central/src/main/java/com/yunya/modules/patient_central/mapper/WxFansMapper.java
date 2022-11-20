package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.models.patient_central.WxFans;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface WxFansMapper extends Mapper<WxFans> {

    List<WxFansVo> findList(WxFansQueryForm wxFansQueryForm);

    List<WxWechatFansVo> findWechatList(WxFansWechatQueryForm wxFansQueryForm);

    List<WxFansVo> findListByName(WxFanByNameForm wxFanByNameForm);

    List<WxWechatMapFansVo> findMapList(WxFansMapQueryForm wxFansMapQueryForm);

    WxWechatMapBindNumFansVo findNumBind(WxFansMapQueryForm wxFansMapQueryForm);

    List<WxFansDetailVO> findDetail(WxFansDetailForm wxFansDetailForm);

    /**
     * 根据unionid查询微信用户已关注公众号或小程序或企业微信列表
     * @param unionid
     * @return
     */
    List<WxFansVo> selectWxFansSubscibedList(@Param("unionid") String unionid);

    /**
     * 批量插入
     * @param list
     */
    void insertList(@Param("list") List<WxFans> list);

    /**
     * 批量更新
     * @param list
     */
    void updateList(@Param("list") List<WxFans> list);
}