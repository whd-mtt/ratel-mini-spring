package com.whd.ratel.spring.framework.beans;

import com.whd.ratel.spring.framework.core.FactoryBean;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/10 22:36
 * @apiNote Describe the function of this class in one sentence
 **/
public class BeanWrapper extends FactoryBean {

    //还会用到 观察者模式
    //1.支持事件响应会有一个监听

    private BeanPostProcessor beanPostProcessor;

    private Object wrapperInstance;

    /***
     * 原始的通过反射new出来，要包装起来，存下来
     */
    private Object originalInstance;

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    public void setOriginalInstance(Object originalInstance) {
        this.originalInstance = originalInstance;
    }

    public BeanWrapper(Object instance){
        this.wrapperInstance = instance;
        this.originalInstance = instance;
    }

    public Object getWrapperInstance(){
        return this.wrapperInstance;
    }


    /***
     * 返回代理以后的class,可能会是$Proxy0
     * @return
     */
    public Class<?> getWrappedClass(){
        return this.wrapperInstance.getClass();
    }
}
