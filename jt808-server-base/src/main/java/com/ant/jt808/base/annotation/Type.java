package com.ant.jt808.base.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Type {

    int[] value();

    String desc() default "";

}