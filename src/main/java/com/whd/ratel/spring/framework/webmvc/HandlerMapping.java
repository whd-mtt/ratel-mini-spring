package com.whd.ratel.spring.framework.webmvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/14 0:14
 * @apiNote Describe the function of this class in one sentence
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "from")
public class HandlerMapping {

    private Object controller;

    private Method method;

    /***
     * url的封装
     */
    private Pattern pattern;
}
