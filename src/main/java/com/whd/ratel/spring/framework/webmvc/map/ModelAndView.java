package com.whd.ratel.spring.framework.webmvc.map;

import java.util.Map;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/13 23:12
 * @apiNote Describe the function of this class in one sentence
 **/
public class ModelAndView {

    private String viewName;
    private Map<String,?> model;

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
