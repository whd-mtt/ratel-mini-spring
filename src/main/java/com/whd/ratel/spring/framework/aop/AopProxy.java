package com.whd.ratel.spring.framework.aop;

import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/27 21:53
 * @apiNote spring默认代理方式jdk动态代理
 **/
public class AopProxy implements InvocationHandler {

    @Setter
    private AopConfig config;

    /***
     * 被代理对象
     */
    private Object target;

    /***
     * 把原生的对象传进来
     * @param instance
     * @return
     */
    public Object getProxy(Object instance){
        this.target = instance;
        Class<?> clazz = instance.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    /**
     * Processes a method invocation on a proxy instance and returns
     * the result.  This method will be invoked on an invocation handler
     * when a method is invoked on a proxy instance that it is
     * associated with.
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Method m = this.target.getClass().getMethod(method.getName(), method.getParameterTypes());
        //通过原生方法去找，通过代理方法是找不到的
        //在原始方法执行之前执行的增强代码
        if (this.config.contains(m)){
            AopConfig.Aspect aspect = config.get(method);
            aspect.getPoints()[0].invoke(aspect);
        }
        //反射调用原始方法
        Object object = method.invoke(this.target, args);
        //在原始方法调用以后执行增强的代码
        if (this.config.contains(m)){
            AopConfig.Aspect aspect = config.get(method);
            aspect.getPoints()[1].invoke(aspect);
        }
        //将最原始的返回值返回出去
        return object;
    }
}
