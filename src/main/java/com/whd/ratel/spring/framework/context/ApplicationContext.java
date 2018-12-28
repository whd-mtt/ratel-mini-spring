package com.whd.ratel.spring.framework.context;

import com.whd.ratel.spring.framework.annotation.Autowired;
import com.whd.ratel.spring.framework.annotation.Controller;
import com.whd.ratel.spring.framework.annotation.Service;
import com.whd.ratel.spring.framework.aop.AopConfig;
import com.whd.ratel.spring.framework.beans.BeanDefinition;
import com.whd.ratel.spring.framework.beans.BeanPostProcessor;
import com.whd.ratel.spring.framework.beans.BeanWrapper;
import com.whd.ratel.spring.framework.context.support.BeanDefinitionReader;
import com.whd.ratel.spring.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/10 22:16
 * @apiNote Describe the function of this class in one sentence
 **/
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader reader;

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

    private void refresh() {

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
                //这里拿到的是通过aop代理的对象
                Object bean = getBean(beanName);
                System.out.println("bean = " + bean.getClass());
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
    private void populateBean(String beanName, Object instance){
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
                field.set(instance, this.beanWrapperMap.get(autowiredName).getOriginalInstance());
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

            Object instance = instantiationBean(beanDefinition );
            if (null == instance) {return null;}
            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanClassName);

            BeanWrapper beanWrapper = new BeanWrapper(instance);
            //设置代理配置
            beanWrapper.setAopConfig(instantiationAopConfig(beanDefinition));
            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName, beanWrapper);

            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanClassName);

//            populateBean(beanName, instance);

            //通过这样一调用，相当于给我们自己留下了可操作的的空间
            return this.beanWrapperMap.get(beanName).getWrapperInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /***
     *
     * @param beanDefinition
     * @return
     */
    private AopConfig instantiationAopConfig(BeanDefinition beanDefinition) throws Exception {
        AopConfig aopConfig = new AopConfig();
        String expression = reader.getConfig().getProperty("pointcut");
        String[] before = reader.getConfig().getProperty("aspectBefore").split("\\s");
        String[] after = reader.getConfig().getProperty("aspectAfter").split("\\s");

        String className = beanDefinition.getBeanClassName();
        Class<?> clazz = Class.forName(className);

        Pattern pattern = Pattern.compile(expression);

        Class<?> aspectClass = Class.forName(before[0]);
        //在这里得到的方法是一个原生的方法
        for (Method method : clazz.getMethods()) {
            //public .* com\.whd\.ratel\.demo\.service\..*Service\..*\(.*\)
            //public java.lang.String com.whd.ratel.demo.service.impl.ModifyService.add(java.lang.String, java.lang.String)
            Matcher matcher = pattern.matcher(method.toString());
            if (matcher.matches()){
                //能满足切面规则的类添加到aop配置中去
                aopConfig.put(method, aspectClass.newInstance(),
                        new Method[]{aspectClass.getMethod(before[1]), aspectClass.getMethod(after[1])});
            }
        }
        return aopConfig;
    }

    /***
     * 传入一个beanDefinition就返回一个bean
     * @param beanDefinition
     * @return
     */
    private Object instantiationBean(BeanDefinition beanDefinition) {

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

    public Properties getConfig(){
        return this.reader.getConfig();
    }


    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[0]);
    }

    /**
     * 刷新BeanFactory实现
     */
    @Override
    protected void refreshBeanFactory() {

    }
}
