package com.yunya.modules.treatment.other.biz;

import com.yunya.framework.common.model.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: yunya-dental
 * @description: 随访记录测试类
 * @author: LHB
 * @create: 2020-08-24 10:34
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class VisitingRecordTest {
    @Autowired
    private VisitingRecordBiz visitingRecordBiz;

    /**
     * 根据随访id查询随访记录
     */
    @Test
    public void findVisitingRecordByIdTest(){
        ResponseResult responseResult = visitingRecordBiz.findVisitingRecordById(3);
        System.out.println(responseResult);
    }

}
