package com.whd.ratel.spring.framework.webmvc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/14 0:14
 * @apiNote Describe the function of this class in one sentence
 **/

@Data
@AllArgsConstructor
public class HandlerMapping {

    private Object controller;

    private Method method;

    private String url;
}