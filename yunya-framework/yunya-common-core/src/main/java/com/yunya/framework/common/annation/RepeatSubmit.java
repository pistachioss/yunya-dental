package com.yunya.framework.common.annation;

import java.lang.annotation.*;

/**
 * 自定义注解防表单重复提交
 *
 * @author chow
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {}
