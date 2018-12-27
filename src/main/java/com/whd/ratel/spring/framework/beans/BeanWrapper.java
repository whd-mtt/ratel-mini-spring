package com.whd.ratel.spring.framework.beans;

import com.whd.ratel.spring.framework.aop.AopConfig;
import com.whd.ratel.spring.framework.aop.AopProxy;
import com.whd.ratel.spring.framework.core.FactoryBean;
import lombok.Data;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/10 22:36
 * @apiNote Describe the function of this class in one sentence
 **/
@Data
public class BeanWrapper extends FactoryBean {

    private AopProxy aopProxy = new AopProxy();
    //还会用到 观察者模式
    //1.支持事件响应会有一个监听

    private BeanPostProcessor beanPostProcessor;

    private Object wrapperInstance;

    /***
     * 原始的通过反射new出来，要包装起来，存下来
     */
    private Object originalInstance;

    public BeanWrapper(Object instance){
        this.wrapperInstance = aopProxy.getProxy(instance);
        this.originalInstance = instance;
    }

    /***
     * 返回代理以后的class,可能会是$Proxy0
     * @return
     */
    public Class<?> getWrappedClass(){
        return this.wrapperInstance.getClass();
    }

    public void setAopConfig(AopConfig aopConfig){
        aopProxy.setConfig(aopConfig);
    }
}
