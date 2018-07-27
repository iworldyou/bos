package cn.itcast.crm.service.base;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import cn.itcast.crm.domain.Customer;


public interface ICustomerService {
	
	//查询未关联到定区的客户
	@GET
	@Path("/noAssociationFixedAreaCustomer")
	@Produces({ "application/xml", "application/json" })
	public List<Customer> findNoAssociationFixedAreaCustomer();
	
	
	//查询已关联到定区的客户
	@GET
	@Path("/hasAssociationFixedAreaCustomer/{fixedAreaId}")
	@Produces({ "application/xml", "application/json" })
	public List<Customer> findHasAssociationFixedAreaCustomer(
			@PathParam("fixedAreaId") String fixedAreaId);//pathParam,要作为路径参数 
	
	
	//将客户关联到定区上 将客户id拼接成字符串 1,2,3
	@Path("/associationCustomerToFixedarea")
	@PUT
	public void associationCustomerToFixedArea(
			@QueryParam("customerIdStr") String customerIdStr,
			@QueryParam("fixedAreaId") String fixedAreaId);//修改参数
	
	
	//用户注册保存
	@Path("/save")
	@POST
	@Consumes({ "application/xml", "application/json"})
	public void saveCustomer(Customer customer);
	
	//根据手机号查询客户
	@Path("/findByTelphone/{telephone}")
	@GET
	@Produces({ "application/xml", "application/json" })
	public Customer findByTelphone(
			@PathParam("telephone") String telephone);
	
	
	//根据手机号修改激活状态
	@Path("/updatatype/{telephone}")
	@GET
	@Consumes({"application/xml", "application/json"})
	public void updataType(@PathParam("telephone") String telephone);
	
	//用户登陆
	@GET
	@Path("/customer")
	@Produces({ "application/xml", "application/json" })
	public Customer find(
			@QueryParam("telephone") String telephone,
			@QueryParam("password") String password);
	
	
	//根据客户address查询定区
	@GET
	@Path("/findFixedAreaIdByAddress")
	@Produces({ "application/xml", "application/json" })
	public String findFixedAreaIdByAddress(
			@QueryParam("address") String sendAddress);
	

}
