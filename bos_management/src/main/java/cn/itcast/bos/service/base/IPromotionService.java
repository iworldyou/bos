package cn.itcast.bos.service.base;


import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.page.PageBean;

public interface IPromotionService {

	public void save(Promotion promotion);

	public Page<Promotion> findPageData(Pageable pageable);
	
	//WebService
	//分页查询
	@GET
	@Path("/findAll")
	@Produces({ "application/xml", "application/json" })
	public PageBean<Promotion> findAll(
			@QueryParam("page") int page,
			@QueryParam("rows") int rows);
	
	//根据id查询促销信息
	@GET
	@Path("/promotion")
	@Produces({ "application/xml", "application/json" })
	public Promotion findById(
			@QueryParam("id") Integer id);

	public void updateStatus(Date date);
	
}
