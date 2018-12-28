package com.whd.ratel.demo.service.impl;

import com.whd.ratel.demo.service.IModifyService;
import com.whd.ratel.spring.framework.annotation.Service;


/**
 * @author whd.java@gmail.com
 * @date 2018/12/27 22:35
 * @apiNote DemoController
 **/
@Service
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	@Override
	public String add(String name,String addr) {
		return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	@Override
	public String edit(Integer id,String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	@Override
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
