package com.graduation.inventory.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防重复提交注解
 *
 * @author graduation
 * @version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RepeatSubmit {

    /**
     * 间隔时间（毫秒）
     * 默认5秒内不允许重复提交
     *
     * @return 间隔时间
     */
    int interval() default 5000;

    /**
     * 提示消息
     *
     * @return 提示消息
     */
    String message() default "请勿重复提交";
}
