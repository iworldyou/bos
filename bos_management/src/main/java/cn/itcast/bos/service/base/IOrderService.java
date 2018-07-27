package cn.itcast.bos.service.base;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.crm.domain.Customer;

public interface IOrderService {
	
	@POST
	@Path("/save")
	@Consumes({ "application/xml", "application/json"})
	public void save(Order order);

	public Order findByOrderNum(String orderNum);
	
	

}
