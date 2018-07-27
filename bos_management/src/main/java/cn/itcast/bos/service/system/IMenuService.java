package cn.itcast.bos.service.system;

import java.util.List;

import cn.itcast.bos.domain.system.Menu;

public interface IMenuService {

	public List<Menu> findAll();

	public void add(Menu model);

	public List<Menu> showMenu();

}
