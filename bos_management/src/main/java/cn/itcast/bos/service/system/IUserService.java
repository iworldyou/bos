package cn.itcast.bos.service.system;

import java.util.List;

import cn.itcast.bos.domain.system.User;

public interface IUserService {

	public User findByUsername(String username);

	public List<User> findAll();

	public void save(User user, Integer[] roleIds);

}
