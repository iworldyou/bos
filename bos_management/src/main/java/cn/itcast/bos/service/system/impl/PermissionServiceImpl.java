package cn.itcast.bos.service.system.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.system.IPermissionService;
import cn.itcast.bos.dao.system.PermissionRepository;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.User;

@Service
@Transactional
public class PermissionServiceImpl implements IPermissionService {
	
	@Autowired
	private PermissionRepository permissionRepository;

	@Override
	public List<Permission> findByUser(User user) {
		List<Permission> primissions = permissionRepository.findByUser(user.getId());
		return primissions;
	}

	@Override
	public List<Permission> findAll() {
		return permissionRepository.findAll();
	}

	@Override
	public void add(Permission permission) {
		permissionRepository.save(permission);
	}

}
