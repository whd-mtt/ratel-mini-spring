package com.whd.ratel.spring.framework.aop;

import lombok.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/27 22:40
 * @apiNote 这里这是对application的expression的封装。
 * 目标代理对象的一个方法要增强，需要有用户自己实现的逻辑去增强
 * 配置文件的目的就是：告诉spring，哪些类的哪些方法需要增强，增强的内容是什么
 * 对配置文件所体现的内容进行封装，
 **/
public class AopConfig {

    /***
     * 以目标对象需要增强的Method作为key,需要的代码内容作为value
     */
    @Getter
    private Map<Method, Aspect> points = new HashMap<>();

    public void put(Method target, Object aspect, Method[] methods){
        this.points.put(target, Aspect.from(aspect, methods));
    }

    public Aspect get(Method method){
        return this.points.get(method);
    }

    public boolean contains(Method method){
        return this.points.containsKey(method);
    }

    /***
     * 对增强的代码进行封装
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "from")
    class Aspect {
        /***
         * 待会将LogAspect这个对象赋值给它
         */
        private Object aspects;
        /***
         * 会将LogAspect的before方法和after方法赋值进来
         */
        private Method[] points;
    }
}
