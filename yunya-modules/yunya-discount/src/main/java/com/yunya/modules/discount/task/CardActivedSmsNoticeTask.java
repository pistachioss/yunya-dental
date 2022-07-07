package com.yunya.modules.discount.task;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.discount.domain.vo.CardIyOr365VO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.utils.CronUtil;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.modules.discount.biz.CardActivedSmsBiz;
import com.yunya.modules.discount.biz.CardBiz;
import com.yunya.modules.discount.task.quartz.SimpleQuartz;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 简介：每天23点执行，扫描出明天将发送短信的卡券，为其创建定时任务
 *
 * @author: chenlin
 * @Description: 每天23点执行，扫描出明天将发送短信的卡券，为其创建定时任务
 * @Date: 2022/7/6 16:53
 * @since: 1.0.0
 */
@Slf4j
@Component
@EnableScheduling
public class CardActivedSmsNoticeTask {
    @Autowired
    private CardActivedSmsBiz cardActivedSmsBiz;
    @Autowired
    private CardBiz cardBiz;
    @Autowired
    private SimpleQuartz simpleQuartz;

    public static void main(String[] args) {
        String date = LocalDateTime.now().plusSeconds(3).toString();
        System.out.println(date);
    }

//    @Scheduled(cron = "0 0 23 * * ?")
    @Scheduled(cron = "0 15 16 * * ?")
    public void executeTask() {
        log.info(">>>>>>>>>>>>>>>>> CardActivedSmsNoticeTask start");
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        // 1、查询艾芽卡、365卡等已激活且未全部使用的卡券列表
        PageHelper.startPage(1, 20);
        List<CardIyOr365VO> cards = cardBiz.findIyOr365CardActivedList();
        if (StringHelper.isNotEmpty(cards)) {
            List<CardIyOr365VO> cardTask = new ArrayList<>();
            // 2、过滤掉失效的卡券（卡券的有效期）
            cards.forEach(vo->{
                LocalDateTime activationDeadline = vo.getActivationDeadline();
                LocalDateTime activeDate = vo.getActiveDate();
                Integer effectiveDays = vo.getEffectiveDays();
                if (ObjectUtils.isEmpty(activationDeadline)) {
                    if (effectiveDays != 0) {
                        activeDate = activeDate.plusDays(effectiveDays);
                    }
                } else {
                    if (effectiveDays != 0) {
                        activeDate = activeDate.plusDays(effectiveDays);
                        activationDeadline = activationDeadline.isAfter(activeDate)?activeDate:activationDeadline;
                    }
                }
                LocalDateTime lastSendDate = vo.getLastSendDate();
                // 截止时间晚于明天，且明天距上次发送短信日期为3个月
                if (!activationDeadline.isBefore(tomorrow)
                        && lastSendDate.plusMonths(3).toLocalDate().isEqual(tomorrow.toLocalDate())) {
                    if (activationDeadline.equals(tomorrow)) {
                        // 当明天为截止时间时，短信发送时间（激活时间）提前1小时
                        activeDate = activeDate.minusHours(1);
                    }
                    String date = String.join(" ",
                            DateUtil.format(tomorrow, "yyyy-MM-dd"),
                            DateUtil.format(activeDate, "HH:mm:ss"));
//                String date = "2022-07-07 16:16:00";
                    String cronExp = CronUtil.nextExecCron(date);
                    // 3、为每个卡券创建一个一次性的定时任务
                    if (StringHelper.isNotEmpty(cronExp)) {
                        Card card = new Card();
                        BeanUtils.copyProperties(vo, card);
                        card.setCrtId(1);
                        CouponCommonInfo coupon = new CouponCommonInfo();
                        BeanUtils.copyProperties(vo, coupon);
                        try {
                            simpleQuartz.cornTimmer(cronExp, ()->{
                                log.info(">>>>>>>>>>>>>>>>>>>开始执行");
                                cardActivedSmsBiz.sendAndrecordSms(card, coupon);
                                log.info("<<<<<<<<<<<<<<<<<<<执行完成");
                            });
                        } catch (SchedulerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        log.info("<<<<<<<<<<<<<<<<< CardActivedSmsNoticeTask end");
    }
}
