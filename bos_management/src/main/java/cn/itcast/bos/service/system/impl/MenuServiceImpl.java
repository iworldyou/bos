package cn.itcast.bos.service.system.impl;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.system.MenuRepository;
import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.IMenuService;

@Service
@Transactional
public class MenuServiceImpl implements IMenuService {

	//注入dao
	@Autowired
	private MenuRepository menuRepository;
	@Override
	public List<Menu> findAll() {
		
		List<Menu> list = menuRepository.findAll();
		System.out.println(list.size());
		return list;
	}
	@Override
	public void add(Menu menu) {
		//防止用户没有选择复菜单
		if (menu.getParentMenu()!=null && menu.getParentMenu().getId()==0) {
			menu.setParentMenu(null);
		}
		
		menuRepository.save(menu);
		
	}
	@Override
	public List<Menu> showMenu() {
		//获得当前登录用户
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		
		if (user.getUsername().equals("admin")) {
			List<Menu> list = menuRepository.findAll();
			return list;
			
		}else {

			//根据用户查询菜单
			List<Menu> list = menuRepository.findByUser(user.getId());
			return list;
		}
		
	}

}
