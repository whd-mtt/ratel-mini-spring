package com.whd.ratel.spring.annotation;

import java.lang.annotation.*;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/9 14:13
 * @apiNote Describe the function of this class in one sentence
 **/
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String value() default "";
}
