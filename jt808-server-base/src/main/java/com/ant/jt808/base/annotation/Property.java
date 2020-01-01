package com.ant.jt808.base.annotation;

import com.ant.jt808.base.enums.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

    int index() default -1;

    String[] indexOffsetName() default "";

    int length() default -1;

    String lengthName() default "";

    DataType type() default DataType.BYTE;

    String charset() default "GBK";

    byte pad() default 0;

    String desc() default "";

}