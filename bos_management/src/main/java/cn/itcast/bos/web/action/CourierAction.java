package cn.itcast.bos.web.action;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.ICourierService;


@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CourierAction extends ActionSupport implements ModelDriven<Courier>{
	
	private Courier c = new Courier(); 
	
	
	@Override
	public Courier getModel() {
		return c;
	}
	
	@Autowired
	private ICourierService courierService;
	
	
	
	
	//收派员的添加
	@Action(value="courier_save", results = { @Result(name = "success", type = "redirect", location = "./pages/base/courier.html")})
	public String save(){
		courierService.save(c);
		
		return SUCCESS;
		
	}
	
	//属性驱动获得页面传入的参数
	private int page;//页数
	
	private int rows;//每页条数
	
	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	
	//条件查询
	@Action(value="courier_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery(){
		//封装分页的参数
		Pageable pageRequest = new PageRequest(page-1, rows);
		
		//封装条件查询
		Specification<Courier> specification = new Specification<Courier>(){

			@Override
			public Predicate toPredicate(Root root, CriteriaQuery query,	
					CriteriaBuilder cb) {
				//query为简单条件查询,cb用于构造复杂条件查询
				//定义一个集合封装条件
				List<Predicate> list = new ArrayList<Predicate>();
				
				//简单单表
				if (StringUtils.isNotBlank(c.getCourierNum())) {
					//快递员编号 equal相当于=?
					Predicate p1 = cb.equal(root.get("courierNum").as(String.class),c.getCourierNum());
					list.add(p1);
				}
				
				if (StringUtils.isNotBlank(c.getCompany())) {
					//快递员公司like相当于like? 模糊查询
					Predicate p2 = cb.like(root.get("company").as(String.class),"%"+c.getCompany()+"%");
					list.add(p2);
					
				}
				
				if (StringUtils.isNotBlank(c.getType())) {
					//取派员类型
					Predicate p3 = cb.equal(root.get("type").as(String.class),c.getType());
					list.add(p3);
					
				}
				
				//多表查询--jointype.inner内连接
				
				Join<Courier,Standard> join = root.join("standard",JoinType.INNER);
				
				if (c.getStandard()!=null &&  StringUtils.isNotBlank(c.getStandard().getName())) {
					//收派标准名称输入不为空,收派员的的收派标准不为空
					Predicate p4 = cb.like(join.get("name").as(String.class),"%"+c.getStandard().getName()+"%");
					list.add(p4);
				}
				
				return cb.and(list.toArray(new Predicate[0]));
				
			}};
			
			
			//查询分页数据
			Page<Courier> pagedata = courierService.findPagedata(pageRequest,specification);
			
			//定义一个Map集合
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("total", pagedata.getTotalElements());
			map.put("rows", pagedata.getContent());
			
			//将数据存入valueStack
			ActionContext.getContext().getValueStack().push(map);
			return SUCCESS;
	}
	
	
	//属性驱动获得请求参数
	private String ids;
	
	//提供set方法
	public void setIds(String ids) {
		this.ids = ids;
	}

	//批量作废收派员
	@Action(value="courier_delbatch",results={@Result(name="success",type="redirect",location= "./pages/base/courier.html")})
	public String delBatch(){
		//获得收派员的id
		String[] idarray = ids.split(",");
		
		courierService.delBatch(idarray);
		
		
		return SUCCESS;
	}
	
	
	//查询未关联到定区的快递员
	@Action(value="courier_findnoassociation",results={@Result(name="success",type="json")})
	public String findnoassociation(){
		//System.out.println("aaaaaaaaaaaaaa");
		List<Courier> list = courierService.findnoassociation();
		
		ActionContext.getContext().getValueStack().push(list);
		
		return SUCCESS;
	}
	
	
	
	
	
	
	
	

}
