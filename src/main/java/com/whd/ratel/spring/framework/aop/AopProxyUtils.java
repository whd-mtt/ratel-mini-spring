package com.whd.ratel.spring.framework.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/28 20:33
 * @apiNote Describe the function of this class in one sentence
 **/
public class AopProxyUtils {

    public static Object getSingletonTarget(Object candidate) throws Exception {
        //先判断一下传进来的对象是不是一个被代理过的对象
        //如果不是一个代理对象，就直接返回
        if (!isAopProxy(candidate)){return candidate;}
        return getProxyTargetObject(candidate);
    }

    /***
     * 判断对象是不是一个被代理过的对象
     * @param object
     * @return
     */
    private static boolean isAopProxy(Object object){
        return Proxy.isProxyClass(object.getClass());
    }

    /***
     * 代理对象中都会包含一个h的对象，h对象包含了原来对象
     * @param proxy
     * @return
     * @throws Exception
     */
    private static Object getProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field target = aopProxy.getClass().getDeclaredField("target");
        target.setAccessible(true);
        return target.get(aopProxy);
    }
}
