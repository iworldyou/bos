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

import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.service.system.IRoleService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class RoleAction extends BaseAction<Role>{

	//注入service
	@Autowired
	private IRoleService roleService;
	
	//角色查看
	@Action(value="role_findAll",results={@Result(name="success",type="json")})
	public String find(){
		List<Role> list = roleService.findAll();
		
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}
	
	//属性驱动获取
	private String [] permissionIds;
	
	private String menuIds;
	
	public void setPermissionIds(String[] permissionIds) {
		this.permissionIds = permissionIds;
	}

	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}



	//保存操作
	@Action(value="role_save",results={@Result(name="success",type="redirect",location="/pages/system/role.html")})
	public String save(){
		System.out.println("====================================");
		roleService.save(model,permissionIds,menuIds);
		return SUCCESS;
	}
	
	
	
	
	
}
