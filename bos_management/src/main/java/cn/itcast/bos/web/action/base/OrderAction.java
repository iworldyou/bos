package cn.itcast.bos.web.action.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.service.base.IOrderService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class OrderAction extends BaseAction<Order>{
	
	//注入service
	@Autowired
	private IOrderService orderService;
	
	//根据订单编号查询订单
	@Action(value="order_find",results={@Result(name="success",type="json")})
	public String find(){
		
		Order order = orderService.findByOrderNum(model.getOrderNum());
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		if (order!=null) {
			//订单存在
			map.put("success",true);
			map.put("orderData",order);
		}else {
			//订单不存在
			map.put("success",false);
			
		}
		
		ActionContext.getContext().getValueStack().push(map);
		return SUCCESS;
		
	}
	
	

}
