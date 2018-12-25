package com.whd.ratel.spring.framework.webmvc;

import com.whd.ratel.spring.framework.webmvc.map.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/14 0:16
 * @apiNote Describe the function of this class in one sentence
 **/
public class HandlerAdapter {

    private Map<String, Integer> paramMappings;

    public HandlerAdapter(Map<String, Integer> paramMappings) {
        this.paramMappings = paramMappings;
    }

    /***
     *
     * @param req
     * @param resp
     * @param handlerMapping 为什么要传handlerMapping参数，因为handlerMapping包含了controller/method/url
     * @return
     */
    public ModelAndView handler(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handlerMapping) {
        //根据用户的请求的参数信息，跟method的参数信息进行动态匹配
        //resp 传进来的参数只有一个，就是为了将其复制给方法参数，仅此而已
        //只有当用户传过来的modelAndView为空的时候才会被初始化
        return null;
    }
}
