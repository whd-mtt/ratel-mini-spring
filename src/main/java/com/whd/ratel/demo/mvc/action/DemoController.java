package com.whd.ratel.demo.mvc.action;

import com.whd.ratel.demo.service.IDemoService;
import com.whd.ratel.spring.annotation.Autowired;
import com.whd.ratel.spring.annotation.Controller;
import com.whd.ratel.spring.annotation.RequestMapping;
import com.whd.ratel.spring.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/demo")
public class DemoController {
	
	@Autowired
	private IDemoService demoService;
	
	@RequestMapping("/query")
	public void query(HttpServletRequest req,HttpServletResponse resp, @RequestParam("name") String name){
		String result = demoService.get(name);
		System.out.println(result);
//		try {
//			resp.getWriter().write(result);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	@RequestMapping("/edit.json")
	public void edit(HttpServletRequest req,HttpServletResponse resp,Integer id){
	}
	
}
