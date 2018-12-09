package com.whd.ratel.spring.annotation;

import java.lang.annotation.*;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/9 14:20
 * @apiNote Describe the function of this class in one sentence
 **/
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value() default "";
}
