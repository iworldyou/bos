package cn.itcast.bos.web.action;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.web.action.common.BaseAction;
import cn.itcast.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class OrderAction extends BaseAction<Order>  {
	
	//属性驱动
	private String sendAreaInfo; // 寄件人省市区信息
	
	private String recAreaInfo; // 收件人省市区信息

	public void setSendAreaInfo(String sendAreaInfo) {
		this.sendAreaInfo = sendAreaInfo;
	}

	public void setRecAreaInfo(String recAreaInfo) {
		this.recAreaInfo = recAreaInfo;
	}
	
	@Action(value="order_add",results={@Result(name="success",type="redirect",location="index.html#/express_manage")})
	public String add(){
		//定义寄件area
		Area sendArea = new Area();
		String[] s = sendAreaInfo.split("/");
		sendArea.setProvince(s[0]);
		sendArea.setCity(s[1]);
		sendArea.setDistrict(s[2]);
		
		//定义收件area
		Area recArea = new Area();
		String[] s2 = recAreaInfo.split("/");
		recArea.setProvince(s2[0]);
		recArea.setCity(s2[1]);
		recArea.setDistrict(s2[2]);
		
		model.setSendArea(sendArea);
		model.setRecArea(recArea);
		
		//在session查询并关联客户
		Customer customer = (Customer) ServletActionContext.getRequest().getSession().getAttribute("customer");
		
		model.setCustomer_id(customer.getId());
		
		//调用webService将order传给bos_management
		WebClient.create("http://localhost:8080/bos_management/services/orderService/save")
		.type(MediaType.APPLICATION_JSON)
		.post(model);
		
		return SUCCESS;
	}
	
	
	
	
	
	
	
	
	
}
