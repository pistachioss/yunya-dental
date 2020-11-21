package com.yunya.modules.treatment.other.utils;

import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;

import java.util.Calendar;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 工具类
 * @author: LHB
 * @create: 2020-11-19 15:20
 **/
public class TreatmentOtherUtils {
    /**
     * 根据给定日期(yyyy-MM-dd)返回yyyy-MM-dd 23:59:59 距离格林尼治时间的毫秒
     * @param crtTime 创建时间
     * @return 毫秒数
     */
    public static long currentTimeInMillis(Date crtTime) {
        Calendar creatTime = Calendar.getInstance();
        creatTime.setTimeInMillis(creatTime.getTimeInMillis());
        int year = creatTime.get(Calendar.YEAR);
        int month = creatTime.get(Calendar.MONTH);
        int day = creatTime.get(Calendar.DAY_OF_MONTH);
        Calendar currentTime = Calendar.getInstance();
        creatTime.set(year,month,day,23,59,59);
        return currentTime.getTimeInMillis();
    }

    /**
     * 判断是否可以编辑图片
     * @param crtTime 创建时间
     */
    public static void enableEditImage(Date crtTime) {
        long deadLineInMillis = TreatmentOtherUtils.currentTimeInMillis(crtTime);
        long systemTimeInMillis = System.currentTimeMillis();
        if (systemTimeInMillis > deadLineInMillis) {
            throw new ClientServiceException("已经错过修改日期，不允许修改", OperationCodeConstants.OBJECT_EDIT_FAIL);
        }
    }
}
