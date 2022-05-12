import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.patient_central.WxFans;
import com.yunya365.mini.IvyMiniApplication;
import com.yunya365.mini.service.IWxFansService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest(classes = {IvyMiniApplication.class})
class MiniTest {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private IWxFansService wxFansService;

    @Test
    void contextLoads() throws IOException {
        WxFans byId = wxFansService.getByOpenId("");
        System.out.println(byId);
    }


}
