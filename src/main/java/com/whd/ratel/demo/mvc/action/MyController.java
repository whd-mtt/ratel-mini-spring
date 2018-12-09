package com.whd.ratel.demo.mvc.action;

import com.whd.ratel.demo.service.IDemoService;
import com.whd.ratel.spring.annotation.Autowired;
import com.whd.ratel.spring.annotation.Controller;
import com.whd.ratel.spring.annotation.RequestMapping;

@Controller
public class MyController {

		@Autowired IDemoService demoService;
	
		@RequestMapping("/index.html")
		public void query(){

		}
	
}
