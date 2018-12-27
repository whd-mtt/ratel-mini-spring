package com.whd.ratel.spring.framework.context;

import com.whd.ratel.spring.framework.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/27 22:03
 * @apiNote Describe the function of this class in one sentence
 **/
public class DefaultListableBeanFactory extends AbstractApplicationContext {


    /***
     * beanDefinitionMap用来保存配置信息
     */
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(16);

    @Override
    protected void onRefresh() {
        super.onRefresh();
    }

    /**
     * 刷新BeanFactory实现
     */
    @Override
    protected void refreshBeanFactory() {

    }
}
