package com.whd.ratel.spring.framework.beans;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/11 22:36
 * @apiNote 用于事件监听
 **/
public class BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName){
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName){
        return bean;
    }

}
