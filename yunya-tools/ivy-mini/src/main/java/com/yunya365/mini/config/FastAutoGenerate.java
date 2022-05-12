package com.yunya365.mini.config;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.sql.SQLException;
import java.util.*;

/**
 * @description:
 * @author: xy
 * @date 2021/11/2 16:51
 **/
public class FastAutoGenerate {
    /**
     * 执行 run
     */
    public static void main(String[] args) throws SQLException {
        String pkPath = System.getProperty("user.dir") + "/yunya-tools/ivy-mini/src/main/java";
        String resPath = System.getProperty("user.dir") + "/yunya-tools/ivy-mini/src/main/resources/mapper";
        FastAutoGenerator.create("jdbc:mysql://192.168.31.90:3306/dev_yunya_patient_central?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8"
                , "sa", "Admin001!")
                // 全局配置
                .globalConfig((scanner, builder) -> builder.outputDir(pkPath).author(scanner.apply("请输入作者名称？"))
                        .fileOverride().disableOpenDir())
                // 包配置
                .packageConfig((builder) -> builder.parent("com.yunya365.mini")
                        .entity("domain.entity").service("service").controller("controller").mapper("mapper")
                        .pathInfo(Collections.singletonMap(OutputFile.mapperXml, resPath)))
                // 策略配置
                .strategyConfig((scanner, builder) ->
                        builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                                .entityBuilder().enableLombok().enableTableFieldAnnotation().enableRemoveIsPrefix()
                                    .enableChainModel()
                                .controllerBuilder().enableRestStyle()
                                .mapperBuilder().enableMapperAnnotation().enableBaseResultMap()
                                .build())
                /*
                    模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
                   .templateEngine(new BeetlTemplateEngine())
                   .templateEngine(new FreemarkerTemplateEngine())
                 */
//                .templateConfig(builder -> builder.disable(TemplateType.SERVICE, TemplateType.CONTROLLER
//                        , TemplateType.SERVICEIMPL, TemplateType.XML).build())
                .execute();
    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}
