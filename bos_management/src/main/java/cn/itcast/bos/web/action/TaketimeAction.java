package cn.itcast.bos.web.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.base.TakeTime;
import cn.itcast.bos.service.base.ITaketimeService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class TaketimeAction extends BaseAction<TakeTime>{
	
	//注入service
	@Autowired
	private ITaketimeService taketimeService;
	
	
	@Action(value="taketime_find",results={@Result(name="success",type="json")})
	public String findAll(){
		List<TakeTime> list = taketimeService.findAll();
		
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
		
	}
	
	
	
}
