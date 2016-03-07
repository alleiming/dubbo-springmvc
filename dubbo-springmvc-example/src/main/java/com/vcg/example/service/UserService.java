package com.vcg.example.service;

import com.vcg.example.model.User;

/**
 * 
 * @author wuyu
 *
 */
public interface UserService {

	public User getById(Integer id);

	public void deleteById(Integer id);

	public Integer insert(User user);

	public void testException(Integer id);

	public void testErrorMsgException(Integer id);

}
