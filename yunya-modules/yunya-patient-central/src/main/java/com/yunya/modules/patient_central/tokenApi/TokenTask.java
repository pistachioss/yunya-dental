package com.yunya.modules.patient_central.tokenApi;

import com.uniubi.sdk.auth.authToken.CustomTokenFetcher;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.modules.patient_central.tokenApi.TimingGetRedisToken;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/8/7 16:00
 * @description:
 * @since: 1.0.0
 */
@Component
public class TokenTask implements CustomTokenFetcher {

    @Autowired
    private TimingGetRedisToken timingGetRedisToken;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String getToken() {
        if(timingGetRedisToken.getToken() == null){
            try {
                return timingGetRedisToken.getRedisToken();
            } catch (IOException e){
                throw new ClientServiceException(
                        "访问Wo平台获取token失败！", OperationCodeConstants.DATA_ERROR);
            }
        }
        return timingGetRedisToken.getToken();
    }


}
