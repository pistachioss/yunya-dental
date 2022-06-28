package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.TencentLocUtl;
import com.yunya.framework.common.utils.UUIDUtils;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.system.ClinicLiveCode;
import com.yunya.models.system.ClinicLiveCodeVisit;
import com.yunya.modules.system.domain.model.ClinicLiveCodeVisitModel;
import com.yunya.modules.system.domain.query.ClinicLiveCodeVisitQueryForm;
import com.yunya.modules.system.mapper.ClinicLiveCodeVisitMapper;
import com.yunya.modules.system.vo.ClinicLiveCodeVisitCountVO;
import com.yunya.modules.system.vo.ClinicLiveCodeVisitVO;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/6/27 16:16
 * @since: 1.0.0
 */
@Service
public class ClinicLiveCodeVisitBiz extends BaseBiz<ClinicLiveCodeVisitMapper, ClinicLiveCodeVisit> {

    @Autowired
    private RedisUtils redisUtils;

    private static final String LIVE_CODE_VISIT_KEY = "lock:liveCode:visit";

    /**
     * 长按识别二维码后在关闭页面时调用该接口
     *
     * @param model
     */
    public void click(ClinicLiveCodeVisitModel model) {
        ClinicLiveCodeVisit entity = new ClinicLiveCodeVisit();
        BeanUtils.copyProperties(model, entity);
        entity.setCrtTime(DateTime.now().toDate());
        Boolean isFirstVisit = true;
        String openId = entity.getOpenId();
        if (StringHelper.isNotEmpty(openId)) {
            try {
                while (redisUtils.setLock(LIVE_CODE_VISIT_KEY, openId, 10, TimeUnit.SECONDS)) {
                    List<ClinicLiveCodeVisit> record = mapper.selectClinicLiveCodeVisitByOpenId(openId);
                    if (StringHelper.isNotEmpty(record)) {
                        isFirstVisit = false;
                    }
                    entity.setIsFirstVisit(isFirstVisit);
                    mapper.insertSelective(entity);
                }
            } finally {
                redisUtils.unlock(LIVE_CODE_VISIT_KEY, openId);
            }
        } else {
            entity.setIsFirstVisit(isFirstVisit);
            mapper.insertSelective(entity);
        }
    }

    /**
     * 条件查询
     *
     * @param query
     * @return
     */
    public PageInfo<ClinicLiveCodeVisitVO> findList(ClinicLiveCodeVisitQueryForm query) {
        if (query.getWhetherPage()) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
        }
        List<ClinicLiveCodeVisitVO> result = mapper.selectClinicLiveCodeVisitList(query);
        echoProcess(result);
        return new PageInfo<>(result);
    }

    /**
     * 回显特殊处理
     *
     * @param result
     */
    private void echoProcess(List<ClinicLiveCodeVisitVO> result) {
        result.forEach(vo->{
            if (StringHelper.isEmpty(vo.getNickName())) {
                vo.setNickName("未知");
            }
            vo.setVisitType(vo.getIsFirstVisit()?"新访客":"老访客");
            vo.setProvinceCity(TencentLocUtl.getCityByLoc(vo.getLongitude(), vo.getLatitude()));
            Integer duration = Integer.parseInt(vo.getVisitDuration());
            vo.setVisitDuration(DateUtil.timestamp2DateStr(duration*1000L,"yyyy-MM-dd HH:mm:ss"));
        });
    }

    public ClinicLiveCodeVisitCountVO findCount() {
        ClinicLiveCodeVisitQueryForm query = new ClinicLiveCodeVisitQueryForm();
        String today = DateTime.now().toString("yyyy-MM-dd");
        query.setEndDate(today);
        List<ClinicLiveCodeVisitVO> result = mapper.selectClinicLiveCodeVisitList(query);
        return statisticsVisitNum(result, today);
    }

    /**
     * 统计访客人数
     *
     * @param result
     * @param today
     * @return
     */
    private ClinicLiveCodeVisitCountVO statisticsVisitNum(List<ClinicLiveCodeVisitVO> result, String today) {
        int todayClickCount = 0;
        Set<String> todayCustomerCount = new HashSet<>();
        Set<String> customerCount = new HashSet();
        for (ClinicLiveCodeVisitVO vo : result) {
            String openId = vo.getOpenId();
            if (StringHelper.isEmpty(openId)) {
                // 未知访客处理
                openId = UUIDUtils.generateShortUuid();
            }
            String visitTime = DateUtil.format(vo.getVisitTime());
            if (today.equals(visitTime)) {
                todayClickCount++;
                todayCustomerCount.add(openId);
            }
            customerCount.add(openId);
        }
        ClinicLiveCodeVisitCountVO count = new ClinicLiveCodeVisitCountVO();
        count.setTodayClickCount(todayClickCount);
        count.setTodayCustomerCount(todayCustomerCount.size());
        count.setClickCount(result.size());
        count.setCustomerCount(customerCount.size());
        return count;
    }
}
