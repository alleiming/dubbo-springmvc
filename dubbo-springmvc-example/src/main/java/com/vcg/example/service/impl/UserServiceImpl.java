package com.vcg.example.service.impl;

import java.util.Date;

import com.vcg.example.model.User;
import com.vcg.example.service.UserService;

/**
 * 
 * @author wuyu
 *
 */
public class UserServiceImpl implements UserService {

	
	@Override
	// http://localhost:8090/defaultGroup/1.0.0/json/userService/getById?id=1
	public User getById(Integer id) {
		return new User(id, "test1", "123456", new Date());
	}

	@Override
	// http://localhost:8090/defaultGroup/1.0.0/json/userService/deleteById?id=1
	public void deleteById(Integer id) {
		System.out.println("删除用户 :" + id);
	}

	@Override
	// http://localhost:8090/defaultGroup/1.0.0/json/userService/deleteById?id=1&username=wuyu&password=1
	public Integer insert(User user) {
		System.out.println("插入用户" + user.toString());
		user.setId(1);
		return user.getId();
	}

}
