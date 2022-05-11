package com.yunya.middletable.controller;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.middletable.controller.discount.BaseBenefitController;
import com.yunya.middletable.dao.report.BasePatientGroupRelationMapper;
import com.yunya.models.report.BasePatient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/3/31 14:57
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BaseBenefitControllerTest {
    @Autowired
    private BaseBenefitController baseBenefitController;

    @Test
    public void testPullBenefit() throws InterruptedException {
        String param = "{\"startDate\":\"2000-01-01\",\"endDate\":\"2021-11-31\"}";
        PullForm form = JSONObject.parseObject(param, PullForm.class);
        ResponseResult result = baseBenefitController.pullData(form);
        System.out.println(result);
    }

    @Autowired
    private BasePatientGroupRelationMapper basePatientGroupRelationMapper;

    /**
     * 查询挂号患者的手机号，并生成群发短信数据（阿里云短信群发手机模板格式）
     *
     * @throws Exception
     */
    @Test
    public void testBatchAliyunSms() throws Exception {
        List<String> uneedMobile = readText("C:\\Users\\E\\Desktop\\sms成功名单.txt");
        System.out.println("从txt文件中读取到手机号：" + uneedMobile.size());
        List<String> mobiles2 = readText("C:\\Users\\E\\Desktop\\sms成功名单2.txt");
        System.out.println("从txt文件中读取到手机号：" + mobiles2.size());
        // 需要排除的手机号
        uneedMobile.addAll(mobiles2);
        // 条件查询挂号患者的手机号
        List<BasePatient> patients = basePatientGroupRelationMapper.selectRegisterdPatientMobile("2015", "2022", 37, uneedMobile);
        System.out.println("从数据库读取到手机号有：" + patients.size());
        // 生成分批数据txt文件
        List<List<BasePatient>> partition = Lists.partition(patients, 1000);
        for (int i = 0; i < partition.size(); i++) {
            String writeFile = "D:/SMS_new"+(i+1)+".txt";
            writeTxt(writeFile, partition.get(i));
        }
        System.out.println("手机号写入txt文件完成！");
    }

    public static void writeTxt(String txtPath, List<BasePatient> patients){
        FileOutputStream fos = null;
        File file = new File(txtPath);
        try {
            if(file.exists()){
                //判断文件是否存在，如果不存在就新建一个txt
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            for (BasePatient patient : patients) {
                String mobile = patient.getMobile();
                fos.write(mobile.getBytes());
                fos.write("\n".getBytes());
            }
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> readText(String filePath) throws Exception {
        List<String> list = new ArrayList<>();
        try
        {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists())
            { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;

                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    list.add(lineTxt);
                }
                bufferedReader.close();
                read.close();
            }
            else
            {
                System.out.println("找不到指定的文件");
            }
        }
        catch (Exception e)
        {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return list;
    }
}
