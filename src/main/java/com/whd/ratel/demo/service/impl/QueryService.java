package com.whd.ratel.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;


import com.whd.ratel.demo.service.IQueryService;
import com.whd.ratel.spring.framework.annotation.Service;

/**
 * 查询业务
 * @author Tom
 *
 */
@Service
public class QueryService implements IQueryService {

	/**
	 * 查询
	 */
	@Override
	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		return json;
	}

}
