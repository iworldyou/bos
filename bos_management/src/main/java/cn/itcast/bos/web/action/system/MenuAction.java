package cn.itcast.bos.web.action.system;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.service.system.IMenuService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class MenuAction extends BaseAction<Menu>{
	
	//注入service
	@Autowired
	private IMenuService menuService;
	
	//菜单查询
	@Action(value="menu_findAll",results={@Result(name="success",type="json")})
	public String find(){
		List<Menu> list = menuService.findAll();
		ActionContext.getContext().getValueStack().push(list);
		
		return SUCCESS;
	}
	
	//菜单添加
	@Action(value="menu_add",results={@Result(name="success",type="redirect",location="/pages/system/menu.html")})
	public String add(){
		menuService.add(model);
		
		return SUCCESS;
	}
	
	//权限菜单下显示
	@Action(value="menu_show",results={@Result(name="success",type="json")})
	public String show(){
		List<Menu> list = menuService.showMenu();
		ActionContext.getContext().getValueStack().push(list);
		
		return SUCCESS;
	}
	
	
}
