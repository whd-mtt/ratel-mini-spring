package com.whd.ratel.spring.framework.webmvc.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/13 23:12
 * @apiNote Describe the function of this class in one sentence
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelAndView {

    private String viewName;

    private Map<String, ?> model;

}
