package com.yunya.modules.emr;

import com.yunya.feign.emr.domain.form.SymptomConfigForm;
import com.yunya.feign.emr.domain.model.SymptomConfigModel;
import com.yunya.feign.emr.domain.query.SymptomConfigQuery;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.modules.emr.controller.SymptomConfigController;
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
public class SymptomConfigControllerTest {
    @Autowired
    private SymptomConfigController symptomConfigController;

    @Before
    public void before() {

    }

    @Test
    public void checkAdd() {
        BaseContextHandler.setUserID("635");
        SymptomConfigModel model = new SymptomConfigModel();
        model.setSymptomName("牙尖");
        model.setCheckId(2);
        model.setRemark("ok");
        symptomConfigController.add(model);
    }

    @Test
    public void checkUpdate() {
        BaseContextHandler.setUserID("635");
        SymptomConfigForm f = new SymptomConfigForm();
        f.setId(1);
        f.setSymptomName("齿白");
        f.setRemark("no ok");
        symptomConfigController.update(f);
    }

    @Test
    public void checkDelete() {
        BaseContextHandler.setUserID("635");
        symptomConfigController.delete(1);
    }

    @Test
    public void findOne() {
        System.out.println(symptomConfigController.findOneById(1));
    }

    @Test
    public void findList() {
        SymptomConfigQuery query = new SymptomConfigQuery();
        query.setCheckId(2);
        System.out.println(symptomConfigController.findList(query));
    }

}
