package com.yunya.modules.appointment.biz;

import com.yunya.feign.appointment.domain.model.AppointModifyRecordModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.models.appointment.AppointmentModifyRecord;
import com.yunya.modules.appointment.mapper.AppointmentModifyRecordMapper;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.Date;
import java.util.List;

/**
 * 预约修改记录服务
 *
 * @author yunya-lihuibin
 * @create 2020-08-06 20:13
 * @update yunya-lihuibin    2020-08-06    新建
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppointmentModifyRecordBiz extends BaseBiz<AppointmentModifyRecordMapper, AppointmentModifyRecord> {

    /**
     * 新增预约修改记录
     * @param record
     * @return
     */
    public Integer addAppointModifyRecord(AppointModifyRecordModel record){

        AppointmentModifyRecord build = EntityUtils.build(record, AppointmentModifyRecord.class);
        List<AppointmentModifyRecord> records = mapper.select(build);
        if (records != null && !records.isEmpty()){
            throw new ClientServiceException("已经存在相同的数据！", OperationCodeConstants.SAME_DATA_EXIST);
        }
        build.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
        build.setCrtName(BaseContextHandler.getName());
        build.setCrtTime(new Date(System.currentTimeMillis()));
        return mapper.insertSelective(build);
    }
}
