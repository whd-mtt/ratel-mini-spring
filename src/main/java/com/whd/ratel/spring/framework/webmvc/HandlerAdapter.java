package com.whd.ratel.spring.framework.webmvc;

import com.whd.ratel.spring.framework.webmvc.map.ModelAndView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/14 0:16
 * @apiNote Describe the function of this class in one sentence
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandlerAdapter {

    private Map<String, Integer> paramMappings;

    /***
     *
     * @param req
     * @param resp
     * @param handlerMapping 为什么要传handlerMapping参数，因为handlerMapping包含了controller/method/url
     * @return
     */
    public ModelAndView handler(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handlerMapping) throws Exception {
        //根据用户的请求的参数信息，跟method的参数信息进行动态匹配
        //resp 传进来的参数只有一个，就是为了将其复制给方法参数，仅此而已
        //只有当用户传过来的modelAndView为空的时候才会被初始化
        //1.准备好方法的形参列表，
        //方法重载：形参的决定因素，参数的个数/类型/顺序/方法名字
        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
        //2.拿到自定义的命名参数列表所在的位置
        //用户通过url传过来的参数列表
        Map<String, String[]> parameterMap = req.getParameterMap();
        //3.构造实参列表
        Object[] paramValues = new Object[parameterTypes.length];
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[\\]", "")
                    .replaceAll("\\s", "");
            if (!this.paramMappings.containsKey(entry.getKey())) {
                continue;
            }
            int index = this.paramMappings.get(entry.getKey());
            //页面上传过来的值都是string类型的，而方法中定义的类型可以是各种类型
            //所以要针对我们传过来的参数进行类型转换
            paramValues[index] = caseStringValue(value, parameterTypes[index]);
        }
        if (this.paramMappings.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = this.paramMappings.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if (this.paramMappings.containsKey(HttpServletResponse.class.getName())){
            int respIndex = this.paramMappings.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }
        //4.从handler中去除controller/method,然后利用反射机制调用
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (result == null) {return null;}
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == ModelAndView.class;
        if (isModelAndView) {
            return (ModelAndView) result;
        }
        return null;
    }

    /***
     * 将传过来的参数进行类型转换
     * @param value
     * @param clazz
     * @return
     */
    private Object caseStringValue(String value, Class<?> clazz) {
        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class || clazz == int.class) {
            return Integer.valueOf(value);
        } else {
            return null;
        }
    }
}
