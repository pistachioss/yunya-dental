package com.yunya.modules.clinic.base;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@SpringBootTest
class ClinicBaseApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        int a=500;
        int b=777;
        DecimalFormat df=new DecimalFormat("0.0000");

        System.out.println(df.format((float)a/b));
    }
}
