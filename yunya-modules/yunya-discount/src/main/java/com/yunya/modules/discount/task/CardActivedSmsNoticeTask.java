package com.yunya.modules.discount.task;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.discount.domain.query.CardIyOr365ActivedQuery;
import com.yunya.feign.discount.domain.vo.CardIyOr365VO;
import com.yunya.framework.common.utils.CronUtil;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.discount.Card;
import com.yunya.models.discount.CouponCommonInfo;
import com.yunya.modules.discount.biz.CardActivedSmsBiz;
import com.yunya.modules.discount.biz.CardBiz;
import com.yunya.modules.discount.task.quartz.ScheduledQuartz;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
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
    private ScheduledQuartz scheduledQuartz;
    /** 间隔月份 */
    private static final Integer INTERVAL = 3;

    @Scheduled(cron = "0 0 23 * * ?")
    public void executeTask() {
        CardIyOr365ActivedQuery query = new CardIyOr365ActivedQuery();
        // 明天
        LocalDateTime execDate = LocalDateTime.now().plusDays(1);
//        log.info(">>>>>>>>>>>>>>>>>>>CardActivedSmsNoticeTask start");
        System.out.println(">>>>>>>>>>>>>>>>>>>CardActivedSmsNoticeTask start");
        // 1、查询艾芽卡、365卡等已激活且未全部使用的卡券列表
        List<CardIyOr365VO> cards = cardBiz.findIyOr365CardActivedList(query);
        List<CardIyOr365VO> list = new ArrayList<>();
        if (StringHelper.isNotEmpty(cards)) {
            // 2、过滤掉失效的卡券（卡券的有效期）
            cards.forEach(card->{
                LocalDateTime activationDeadline = getActivationDeadline(card);
                LocalDateTime activeDate = card.getActiveDate();
                // 截止时间晚于明天，且明天距上次发送短信日期为3个月
                if (isTomorrowTask(card.getLastSendDate(), activationDeadline, execDate)) {
                    if (activationDeadline.toLocalDate().equals(execDate.toLocalDate())) {
                        // 当明天为截止时间时，短信发送时间（激活时间）提前1小时
                        activeDate = activeDate.minusHours(1);
                    }
                    String date = String.join(" ",
                            DateUtil.format(execDate, "yyyy-MM-dd"),
                            DateUtil.format(activeDate, "HH:mm:ss"));
                    String cronExp = CronUtil.nextExecCron(date);
                    card.setCron(cronExp);
                    card.setActivationDeadline(activationDeadline);
                    list.add(card);
                    // 3、为卡券创建一个一次性的定时任务
                    addCronTask(card, cronExp);
                }
            });
            System.out.println("Eligible tasks are as follows: ");
            System.out.println("task size: " + list.size() + ", data: " + JSONObject.toJSON(list));
        }
//        log.info("<<<<<<<<<<<<<<<<<<<CardActivedSmsNoticeTask end");
        System.out.println("<<<<<<<<<<<<<<<<<<<CardActivedSmsNoticeTask end");
    }

    /**
     * 获取卡券的有效期
     * @param vo
     * @return
     */
    private LocalDateTime getActivationDeadline(CardIyOr365VO vo) {
        LocalDateTime activationDeadline = vo.getActivationDeadline();
        LocalDateTime activeDate = vo.getActiveDate();
        Integer effectiveDays = vo.getEffectiveDays();
        if (ObjectUtils.isEmpty(activationDeadline)) {
            if (effectiveDays != 0) {
                activationDeadline = activeDate.plusDays(effectiveDays);
            }
        } else {
            if (effectiveDays != 0) {
                activeDate = activeDate.plusDays(effectiveDays);
                activationDeadline = activationDeadline.isAfter(activeDate)?activeDate:activationDeadline;
            }
        }
        return activationDeadline;
    }

    /**
     * 是否是明天要执行的任务
     *
     * @param lastSendDate
     * @param activationDeadline
     * @param tomorrow
     * @return
     */
    private boolean isTomorrowTask(String lastSendDate, LocalDateTime activationDeadline, LocalDateTime tomorrow) {
        Double diff = DateUtil.dateDiff2Double(LocalDate.parse(lastSendDate), tomorrow, 1);
        if (!activationDeadline.toLocalDate().isBefore(tomorrow.toLocalDate())
                && diff % INTERVAL == 0) {
            return true;
        }
        return false;
    }

    /**
     * 添加一个定时任务
     *
     * @param vo
     * @param cronExp
     */
    private void addCronTask(CardIyOr365VO vo, String cronExp) {
        if (StringHelper.isNotEmpty(cronExp)) {
            Card card = new Card();
            BeanUtils.copyProperties(vo, card);
            card.setCrtId(-999);
            CouponCommonInfo coupon = new CouponCommonInfo();
            BeanUtils.copyProperties(vo, coupon);
            try {
                scheduledQuartz.cronTimmer(cronExp, () -> {
                    log.info(">>>>>>>>>>>>>>>>>>>开始执行");
                    cardActivedSmsBiz.sendAndrecordSms(card, coupon);
                    log.info("<<<<<<<<<<<<<<<<<<<执行完成");
                });
            } catch (SchedulerException e) {
                log.error("CardActivedSmsNoticeTask add a cron task error: {}", e);
            }
        }
    }
}
