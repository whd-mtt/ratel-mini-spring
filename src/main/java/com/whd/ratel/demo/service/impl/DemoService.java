package com.whd.ratel.demo.service.impl;


import com.whd.ratel.demo.service.IDemoService;
import com.whd.ratel.spring.annotation.Service;

@Service
public class DemoService implements IDemoService {

	@Override
	public String get(String name) {
		return "My name is " + name;
	}

}
