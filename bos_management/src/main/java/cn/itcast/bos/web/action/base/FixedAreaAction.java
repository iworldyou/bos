package cn.itcast.bos.web.action.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
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

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.base.IFixedAreaService;
import cn.itcast.bos.web.action.common.BaseAction;
import cn.itcast.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class FixedAreaAction extends BaseAction<FixedArea> {

	// 注入service
	@Autowired
	private IFixedAreaService fixedAreaService;

	@Action(value = "fixedArea_save", results = { @Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String save() {

		fixedAreaService.save(model);

		return SUCCESS;

	}

	// 定区的分页查询
	@Action(value = "fixedArea_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		// 封装分页请求参数
		Pageable pageRequest = new PageRequest(page - 1, rows);

		Specification<FixedArea> specification = new Specification<FixedArea>() {

			@Override
			public Predicate toPredicate(Root root, CriteriaQuery query,
					CriteriaBuilder cb) {
				// 定义一个list封装多个条件
				List<Predicate> list = new ArrayList<Predicate>();
				// 构造条件
				// 根据定区编码
				if (StringUtils.isNotBlank(model.getId())) {
					Predicate p1 = cb.equal(root.get("id").as(String.class),
							model.getId());
					list.add(p1);
				}
				// 根据所属公司查询
				if (StringUtils.isNotBlank(model.getCompany())) {
					Predicate p2 = cb.like(
							root.get("company").as(String.class),
							"%" + model.getCompany() + "%");
					list.add(p2);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};
		Page<FixedArea> pagedata = fixedAreaService.findPagedata(pageRequest,
				specification);

		pushToValueStack(pagedata);

		return SUCCESS;

	}

	// 查询未关联定区的客户
	@Action(value = "fixedArea_findNoAssociationFixedAreaCustomer", results = { @Result(name = "success", type = "json") })
	public String findNoAssociationFixedAreaCustomer() {
		// 使用cxf cilent工具访问crm_management
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:9090/crm_management/services/customerService/noAssociationFixedAreaCustomer")
				.accept(MediaType.APPLICATION_JSON)
				.getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);

		return SUCCESS;
	}

	// 查询已关联定区的客户
	@Action(value = "fixedArea_findHasAssociationFixedAreaCustomer", results = { @Result(name = "success", type = "json") })
	public String findHasAssociationFixedAreaCustomer() {
		// 使用cxf cilent工具访问crm_management
		Collection<? extends Customer> collection = WebClient
				.create("http://localhost:9090/crm_management/services/customerService/hasAssociationFixedAreaCustomer/"
						+ model.getId()).accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);

		return SUCCESS;
	}

	// 将客户关联到定区中,设置客户的fixeAreaId
	// 通过模型驱动获得定区id值,通过属性驱动获得客户id值
	private String[] customerIds;

	

	public void setCustomerIds(String[] customerIds) {
		this.customerIds = customerIds;
	}

	@Action(value = "fixedArea_associationCustomerToFixedArea", results = { @Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String associationCustomerToFixedArea() {
		// 将ids数组拼接成字符串
		
		//System.out.println(customerIds[0]);
		//System.out.println(model.getId());
		String customerIdStr = StringUtils.join(customerIds,",");// 用,号拼接
		
		// 向crm服务器发送请求
		WebClient
				.create("http://localhost:9090/crm_management/services/customerService/associationCustomerToFixedarea?customerIdStr="+customerIdStr+"&fixedAreaId="+model.getId())
				.put(null);

		return SUCCESS;
	}
	
	
	
	
	//定区id可以通过模型驱动获得model.getId()
	//快递员编号和运派事件通过属性驱动获得
	private Integer courierId;//快递员id
	
	public void setCourierId(Integer courierId) {
		this.courierId = courierId;
	}
	
	private Integer taketimeId;

	public void setTaketimeId(Integer taketimeId) {
		this.taketimeId = taketimeId;
	}

	//关联快递员在定区上
	@Action(value="fixedArea_associationCourierToFixedArea",results={@Result(name="success",type = "redirect", location = "./pages/base/fixed_area.html")})
	public String associationCourierToFixedArea(){
		fixedAreaService.associationCourierToFixedArea(courierId,taketimeId,model.getId());
		
		
		return SUCCESS;
	}
	

}
