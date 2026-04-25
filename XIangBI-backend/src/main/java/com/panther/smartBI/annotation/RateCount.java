/**
 * XiangBI File: src/main/java/com/panther/smartBI/annotation/RateCount.java
 * Responsibility: Project source module.
 */
package com.panther.smartBI.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateCount {
    /**
     * 每秒限流次数
     *
     * @return
     */
    String count() default "5";
}

