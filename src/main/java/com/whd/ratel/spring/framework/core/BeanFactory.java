package com.whd.ratel.spring.framework.core;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/10 22:15
 * @apiNote Describe the function of this class in one sentence
 **/
public interface BeanFactory {

    /***
     * 通过beanName从ioc容器中获取一个实例
     * @param name beanName
     * @return obj
     */
    Object getBean(String name);
}
