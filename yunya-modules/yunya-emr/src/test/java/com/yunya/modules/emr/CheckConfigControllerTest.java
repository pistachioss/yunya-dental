package com.yunya.modules.emr;

import com.yunya.feign.emr.domain.form.CheckConfigForm;
import com.yunya.feign.emr.domain.model.CheckConfigModel;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.model.PageQuery;
import com.yunya.modules.emr.controller.CheckConfigController;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/1/9 14:08
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CheckConfigControllerTest {
    @Autowired
    private CheckConfigController checkConfigController;

    @Before
    public void before() {

    }

    @Test
    public void checkAdd() {
        BaseContextHandler.setUserID("635");
        CheckConfigModel model = new CheckConfigModel();
        model.setCheckName("龋齿");
        checkConfigController.add(model);
    }

    @Test
    public void checkUpdate() {
        BaseContextHandler.setUserID("635");
        CheckConfigForm f = new CheckConfigForm();
        f.setId(2);
        f.setCheckName("犬齿");
        checkConfigController.update(f);
    }

    @Test
    public void checkDelete() {
        BaseContextHandler.setUserID("635");
        checkConfigController.delete(1);
    }

    @Test
    public void findOne() {
        System.out.println(checkConfigController.findOneById(1));
    }

    @Test
    public void findList() {
        System.out.println(checkConfigController.findList(new PageQuery()));
    }

}
