package com.vcg.example.service;

import com.vcg.example.model.User;

public interface UserService {

	public User getById(Integer id);

	public void deleteById(Integer id);

	public Integer insert(User user);

}
