package com.whd.ratel.spring.framework.context;

import com.whd.ratel.spring.framework.annotation.Autowired;
import com.whd.ratel.spring.framework.annotation.Controller;
import com.whd.ratel.spring.framework.annotation.Service;
import com.whd.ratel.spring.framework.beans.BeanDefinition;
import com.whd.ratel.spring.framework.beans.BeanPostProcessor;
import com.whd.ratel.spring.framework.beans.BeanWrapper;
import com.whd.ratel.spring.framework.context.support.BeanDefinitionReader;
import com.whd.ratel.spring.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/10 22:16
 * @apiNote Describe the function of this class in one sentence
 **/
public class ApplicationContext implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader reader;

    /***
     * beanDefinitionMap用来保存配置信息
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(16);

    /***
     * 用来保证注册式单例的容器
     */
    private Map<String, Object> beanCacheMap = new HashMap<>();

    /**
     * 用来存储所用的被代理过的对象
     */
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        refresh();
    }

    public void refresh() {

        //定位
        this.reader = new BeanDefinitionReader(configLocations);

        //加载
        List<String> beanDefinitions = reader.loadBeanDefinitions();

        //注册
        doRegistry(beanDefinitions);

        //依赖注入（lazy-init=false）要执行依赖注入
        //在这里自动调用getBean()方法
        doAutowired();
    }

    /**
     * 自动化的依赖注入
     */
    private void doAutowired() {

        this.beanDefinitionMap.forEach( (beanName, value) -> {
            if (!value.isLazyInit()){
                getBean(beanName);
            }
        });

        this.beanWrapperMap.forEach((key, value) ->{
            populateBean(key, value.getOriginalInstance());
        });
    }

    /***
     * 依赖注入的方法
     * @param beanName
     * @param instance
     */
    public void populateBean(String beanName, Object instance){
        Class<?> clazz = instance.getClass();
        //只有加了controller和service注解的才
        if (!(clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Service.class))){
            return;
        }
        Field[] declaredFields = clazz.getDeclaredFields();

        for (Field field : declaredFields) {
            if (!field.isAnnotationPresent(Autowired.class)){continue;}
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredName = autowired.value().trim();
            if ("".equals(autowiredName)){
                autowiredName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                field.set(instance, this.beanWrapperMap.get(autowiredName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /***
     * 真正地将beanDefinitions注册到IOC容器（beanDefinitionMsp）中
     * @param beanDefinitions
     */
    private void doRegistry(List<String> beanDefinitions) {
        if (beanDefinitions.isEmpty()) { return; }
        try {
            for (String clazzName : beanDefinitions) {

                Class<?> beanClass = Class.forName(clazzName);
                //如果是接口就不能实例化，用他的实现类实例化
                if (beanClass.isInterface()) {
                    continue;
                }

                BeanDefinition beanDefinition = reader.registerBean(clazzName);
                if (beanDefinition != null) {
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                }

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    //如果是多个实现类只能覆盖，因为spring没那么智能
                    //这时候可以自定义名字
                    this.beanDefinitionMap.put(anInterface.getName(), beanDefinition);
                }
                //到这里容器初始化完毕

                //beanName有三种情况
                //1.默认是类名首字母小写


                //2.自定义


                //3.接口注入

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /***
     * 通过读取BeanDefinition中的信息，然后通过反射机制创建一个实例并返回
     * spring的做法是，不会把最原始的对象放进去，这里会用一个beanWrapper进行一个包装
     * 装饰器模式的几个要素：
     *  1.要保留原来的OOP关系
     *  2.需要对他进行扩展和增强(为了以后的AOP打基础)
     * @param beanName beanName
     * @return obj
     */
    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        String beanClassName = beanDefinition.getBeanClassName();

        try {
            //生成一个通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

            Object instance = instantionBean(beanDefinition);
            if (null == instance) {
                return null;
            }
            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanClassName);

            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName, beanWrapper);

            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanClassName);

//            populateBean(beanName, instance);

            //通过这样一调用，相当于给我们自己留下了可操作的的空间
            return this.beanWrapperMap.get(beanName).getWrapperInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /***
     * 传入一个beanDefinition就返回一个bean
     * @param beanDefinition
     * @return
     */
    private Object instantionBean(BeanDefinition beanDefinition) {

        Object instance;
        String className = beanDefinition.getBeanClassName();
        try {
            //根据一个类才能确定一个类是否有实例
            if (this.beanCacheMap.containsKey(className)) {
                instance = beanCacheMap.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className, instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
