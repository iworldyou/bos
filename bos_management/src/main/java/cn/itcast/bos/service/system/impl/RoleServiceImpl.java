package cn.itcast.bos.service.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.system.MenuRepository;
import cn.itcast.bos.dao.system.PermissionRepository;
import cn.itcast.bos.dao.system.RoleRepository;
import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.IRoleService;

@Service
@Transactional
public class RoleServiceImpl implements IRoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private MenuRepository menuRepository;
	

	@Override
	public List<Role> findByUser(User user) {
		return roleRepository.findByUser(user.getId());
	}

	@Override
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	@Override
	public void save(Role role, String[] permissionIds, String menuIds){
		roleRepository.save(role);
		
		//持久态具有自动更新数据库的能力
		if (permissionIds !=null && permissionIds.length!=0) {
			for (String permissionId : permissionIds) {
				Permission permission = permissionRepository.findOne(Integer.parseInt(permissionId));
				role.getPermissions().add(permission);
			}
			
		}
		
		if (StringUtils.isNoneBlank(menuIds)) {
			String[] menuId = menuIds.split(",");
			for (String s : menuId) {
				Menu menu = menuRepository.findOne(Integer.parseInt(s));
				role.getMenus().add(menu);
			}
			
		}
		
		
	}

}
