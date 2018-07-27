package cn.itcast.bos.dao.system;

import java.util.List;

import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.User;

public interface IPermissionService {

	public List<Permission> findByUser(User user);

	public List<Permission> findAll();

	public void add(Permission model);

}
