package cn.itcast.bos.web.action.transit;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.transit.SignInfo;
import cn.itcast.bos.service.transit.ISignService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class SignAction extends BaseAction<SignInfo>{
	
	@Autowired
	private ISignService signService;

	//属性驱动获得transitId
	private String transitId;
	//出入库的保存操作
	public void setTransitId(String transitId) {
		this.transitId = transitId;
	}

	
	//配送信息保存操作
	@Action(value="sign_save",results={@Result(name="success",type="redirect",location="/pages/transit/transitinfo.html")})
	public String sava(){
		signService.save(model,transitId);
		
		return SUCCESS;
		
	}

}
