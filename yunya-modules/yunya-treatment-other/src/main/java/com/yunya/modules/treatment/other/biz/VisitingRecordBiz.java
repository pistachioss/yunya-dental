package com.yunya.modules.treatment.other.biz;

import com.yunya.feign.treatment_other.domain.form.VisitingRecordForm;
import com.yunya.feign.treatment_other.domain.model.VisitingRecordModel;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.modules.treatment.other.mapper.VisitingRecordMapper;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * @program: yunya-dental
 * @description: 随访记录业务层
 * @author: LHB
 * @create: 2020-08-21 17:52
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class VisitingRecordBiz extends BaseBiz<VisitingRecordMapper, VisitingRecord> {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 插入随访记录
     * @param model  随访记录表单
     * @return  返回插入成功的条数
     */
    public Integer insertVisitingRecord(VisitingRecordModel model){
        VisitingRecord build = EntityUtils.build(model, VisitingRecord.class);
        return mapper.insertSelective(build);
    }

    /**
     * 根据id删除随访记录
     * @param id  随访id
     */
    public ResponseResult deleteVisitingRecord(Integer id){
        VisitingRecord visitingRecord = mapper.selectByPrimaryKey(id);
        if (visitingRecord == null){
            return ResponseUtil.success("记录不存在！");
        }
        String lockStr = redisUtils.get(RedisConstants.LOCK_VISITING_RECORD);
        if (StringHelper.isEmpty(lockStr)) {
            redisUtils.setLock(RedisConstants.LOCK_VISITING_RECORD,String.valueOf(id),BusinessConstants.MEDICAL_APPLY_LOCK_SEC,TimeUnit.SECONDS);
            try {
                mapper.deleteByPrimaryKey(id);
            } finally {
                redisUtils.unlock(RedisConstants.LOCK_VISITING_RECORD,String.valueOf(id));
            }
            return ResponseUtil.success();
        }
        return ResponseUtil.success("该条记录正在编辑中，不能删除！");
    }

    /**
     * 修改随访记录
     * @param form  修改表单
     * @return 修改条数
     */
    public ResponseResult updateVisitingRecord(VisitingRecordForm form){
        String updateId = String.valueOf(form.getId());
        String lockIdStr = redisUtils.get(RedisConstants.LOCK_VISITING_RECORD);
        VisitingRecord visitingRecord = mapper.selectByPrimaryKey(Integer.valueOf(updateId));
        if (visitingRecord == null){
            return ResponseUtil.success("记录不存在！");
        }

        // 检测随访记录是否有其他人在修改
        if (StringHelper.isEmpty(lockIdStr)){
            // 排他加锁
            redisUtils.setLock(RedisConstants.LOCK_VISITING_RECORD,updateId, BusinessConstants.MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            try{
                VisitingRecord build = EntityUtils.build(form, VisitingRecord.class);
                mapper.updateByPrimaryKeySelective(build);
            } finally {
                // 释放锁
                redisUtils.unlock(RedisConstants.LOCK_VISITING_RECORD,updateId);
            }
        } else {
            return ResponseUtil.success("该条记录正在被修改中！");
        }

        return ResponseUtil.success();
    }

    /**
     * 根据随访id查询随访记录
     * @param id 随访id
     * @return ResponseResult
     */
    public ResponseResult findVisitingRecordById(Integer id){
        VisitingRecord visitingRecord = mapper.selectByPrimaryKey(id);
        if (visitingRecord == null){
            return ResponseUtil.success();
        }
        VisitingRecordVo build = EntityUtils.build(visitingRecord, VisitingRecordVo.class);
        return ResponseUtil.success(build);
    }


}
