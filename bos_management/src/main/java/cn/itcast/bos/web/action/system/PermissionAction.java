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

import cn.itcast.bos.dao.system.IPermissionService;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PermissionAction extends BaseAction<Permission> {

	// 注入service
	@Autowired
	private IPermissionService permissionService;

	// 权限的查看
	@Action(value ="permission_findAll", results = { @Result(name = "success", type = "json") })
	public String find() {
		List<Permission> list = permissionService.findAll();
		ActionContext.getContext().getValueStack().push(list);

		return SUCCESS;
	}

	// 权限添加
	@Action(value = "permission_add", results = { @Result(name = "success", type = "redirect", location = "/pages/system/permission.html")})
	public String add() {
		permissionService.add(model);

		return SUCCESS;
	}

}
