package com.whd.ratel.spring.framework.webmvc;

import com.whd.ratel.spring.framework.webmvc.map.ModelAndView;

import java.io.File;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/14 0:17
 * @apiNote
 * 1.将一个静态文件变成一个动态文件
 * 2.根据用户的传参不同产生不同的结果
 * 最终输出字符串，交给Response输出
 **/
public class ViewResolver {

    private String viewName;

    private File template;

    public ViewResolver(String viewName, File template){
        this.viewName = viewName;
        this.template = template;
    }

    public String viewResolver(ModelAndView modelAndView){
        return null;
    }
}
