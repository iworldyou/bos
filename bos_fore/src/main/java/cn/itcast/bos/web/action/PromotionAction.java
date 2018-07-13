package cn.itcast.bos.web.action;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.page.PageBean;
import cn.itcast.bos.web.action.common.BaseAction;


@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PromotionAction extends BaseAction<Promotion>{
	
	//促销的分页查询
	@Action(value="promotion_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		//基于WebService,使用WebCilent获取bos_management中的promotion的数据
		PageBean<Promotion> pageBean = WebClient.create("http://localhost:8080/bos_management/services/promotionService/findAll?page="+page+"&rows="+rows)
		.accept(MediaType.APPLICATION_JSON).get(PageBean.class);
		
		ActionContext.getContext().getValueStack().push(pageBean);
		
		return SUCCESS;
	}
	
}
