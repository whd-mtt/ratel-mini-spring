package com.whd.ratel.demo.mvc.action;

import com.whd.ratel.demo.service.IQueryService;
import com.whd.ratel.spring.framework.annotation.Autowired;
import com.whd.ratel.spring.framework.annotation.Controller;
import com.whd.ratel.spring.framework.annotation.RequestMapping;
import com.whd.ratel.spring.framework.annotation.RequestParam;
import com.whd.ratel.spring.framework.webmvc.map.ModelAndView;

import java.util.HashMap;
import java.util.Map;


/**
 * 公布接口url
 * @author whd.java@gmail.com
 * @date 2018/12/27 22:35
 * @apiNote DemoController
 **/
@Controller
@RequestMapping("/")
public class PageController {

	@Autowired
	private IQueryService queryService;
	
	@RequestMapping("/first.html")
	public ModelAndView query(@RequestParam("teacher") String teacher){
		String result = queryService.query(teacher);
		Map<String,Object> model = new HashMap<>();
		model.put("teacher", teacher);
		model.put("data", result);
		model.put("token", "123456");
		return new ModelAndView("first.html",model);
	}
	
}
