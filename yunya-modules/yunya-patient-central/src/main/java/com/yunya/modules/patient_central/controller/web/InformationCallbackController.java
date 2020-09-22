package com.yunya.modules.patient_central.controller.web;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.yunya.feign.patient_central.domain.model.CallbackModel;
import com.yunya.feign.patient_central.domain.model.TaskModel;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.redis.util.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 简介:
 *
 * @author: YK
 * @date: 2020/9/22 09:52
 * @description: 回调中心控制层
 * @since: 1.0.0
 */

@Api(value = "回调中心", description = "回调中心控制层")
@RestController
@RequestMapping("callback")
public class InformationCallbackController {

    /** 注入redis */
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 心跳回调
     * @param callbackModel
     * @return Boolean
     */
    @ApiOperation(value = "心跳回调")
    @IgnoreUserToken
    @RequestMapping(value = "/heartbeatCallback",method = {RequestMethod.POST})
    public Map<String,Object> getCallback(@RequestBody CallbackModel callbackModel){
        String SN = "84E0F4246B261501";
        Map<String,Object> map = new HashMap<>(16);
        if (SN.equals(callbackModel.getDeviceKey())){
            map.put("result",true);
            return map;
        }
        map.put("result",false);
        return map;
    }


    @ApiOperation(value = "获取任务")
    @IgnoreUserToken
    @RequestMapping(value = "/getTask",method = {RequestMethod.POST})
    public JSONObject getTask(@RequestBody TaskModel taskModel){
        JSONObject jsonObject;
        String SN = "84E0F4246B261501";
        if (SN.equals(taskModel.getDeviceKey())){
            String object = redisUtils.get("object");
            jsonObject =JSONObject.parseObject(object);
            return jsonObject;
        }
        jsonObject = new JSONObject();
        jsonObject.put("result",false);
        return jsonObject;
    }


}