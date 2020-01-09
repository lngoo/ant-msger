package com.ant.msger.base.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Type {

    int[] value();

    String desc() default "";

}