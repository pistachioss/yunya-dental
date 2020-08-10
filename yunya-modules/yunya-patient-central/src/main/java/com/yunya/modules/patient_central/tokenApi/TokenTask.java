package com.yunya.modules.patient_central.tokenApi;

import com.uniubi.sdk.auth.authToken.CustomTokenFetcher;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @SneakyThrows
    @Override
    public String getToken() {
        if(timingGetRedisToken.getToken() == null){
            try {
                return timingGetRedisToken.getRedisToken();
            } catch (IOException e){

            }

        }
        return timingGetRedisToken.getToken();
    }


}
