package com.whd.ratel.demo.service;

/**
 * 增删改业
 * @author whd.java@gmail.com
 * @date 2018/12/27 22:35
 * @apiNote Describe the function of this class in one sentence
 **/
public interface IModifyService {

	/**
	 * 增加
	 */
	String add(String name, String addr);
	
	/**
	 * 修改
	 */
	String edit(Integer id, String name);
	
	/**
	 * 删除
	 */
	String remove(Integer id);
	
}
