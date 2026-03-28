package com.graduation.inventory.common.annotation;

import com.graduation.inventory.common.enums.BusinessType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 *
 * @author graduation
 * @version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Log {

    /**
     * 模块标题
     *
     * @return 模块标题
     */
    String title() default "";

    /**
     * 业务类型
     *
     * @return 业务类型
     */
    BusinessType action() default BusinessType.OTHER;

    /**
     * 是否保存请求参数
     *
     * @return 是否保存请求参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应结果
     *
     * @return 是否保存响应结果
     */
    boolean isSaveResponseData() default true;
}
