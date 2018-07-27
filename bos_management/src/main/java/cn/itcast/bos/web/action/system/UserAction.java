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

import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.IUserService;
import cn.itcast.bos.web.action.common.BaseAction;



@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class UserAction extends BaseAction<User>{
	
	//注入service
	@Autowired
	private IUserService userService;
	
	//查看所有
	@Action(value="user_findAll",results={@Result(name="success",type="json")})
	public String findAll(){
		List<User> list = userService.findAll();
		ActionContext.getContext().getValueStack().push(list);
		
		return SUCCESS;
	}
	
	//属性驱动获取角色id,为id数组
	private Integer[] roleIds;
	//添加用户
	public void setRoleIds(Integer[] roleIds) {
		this.roleIds = roleIds;
	}



	@Action(value="user_add",results={@Result(name="success",type="redirect",location="/pages/system/userlist.html")})
	public String save(){
		userService.save(model,roleIds);
		
		return SUCCESS;
	}
	
	

}
