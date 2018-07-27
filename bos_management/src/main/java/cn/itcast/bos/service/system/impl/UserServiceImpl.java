package cn.itcast.bos.service.system.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.itcast.bos.dao.system.RoleRepository;
import cn.itcast.bos.dao.system.UserRepository;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	// 注入dao
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public User findByUsername(String username) {
		User user = userRepository.findByUsername(username);
		return user;

	}

	@Override
	public List<User> findAll() {
		List<User> list = userRepository.findAll();
		return list;
	}

	@Override
	public void save(User user, Integer[] roleIds) {

		if (roleIds != null && roleIds.length != 0) {
			for (Integer roleId : roleIds) {
				Role role = roleRepository.findOne(roleId);
				user.getRoles().add(role);
			}
		}
		
		userRepository.save(user);

	}

}
