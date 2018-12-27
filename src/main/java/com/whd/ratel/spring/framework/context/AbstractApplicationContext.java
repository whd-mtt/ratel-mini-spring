package com.whd.ratel.spring.framework.context;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/27 21:53
 * @apiNote Describe the function of this class in one sentence
 **/
public abstract class AbstractApplicationContext {


    /**
     * 刷新BeanFactory实现
     */
    protected abstract void refreshBeanFactory();

    /***
     * 提供给子类重写的
     */
    protected void onRefresh() {

    }
}
