package com.whd.ratel.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/9 14:18
 * @apiNote Describe the function of this class in one sentence
 **/
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    String value() default "";
}
