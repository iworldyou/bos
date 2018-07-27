package cn.itcast.bos.service.system;

import java.util.List;

import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;

public interface IRoleService {

	
	public List<Role> findByUser(User user);

	public List<Role> findAll();

	public void save(Role model, String[] permissionIds, String menuIds);

}
