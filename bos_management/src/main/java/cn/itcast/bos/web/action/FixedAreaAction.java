package cn.itcast.bos.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
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

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.base.IFixedAreaService;
import cn.itcast.bos.web.action.common.BaseAction;


@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class FixedAreaAction extends BaseAction<FixedArea>{
	
	//注入service
	@Autowired
	private IFixedAreaService fixedAreaService;
	
	
	@Action(value="fixedArea_save",results={@Result(name="success",type="redirect",location="./pages/base/fixed_area.html")})
	public String save(){
		
		fixedAreaService.save(model);
		
		return SUCCESS;
		
	}
	
	//定区的分页查询
	@Action(value="fixedArea_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		//封装分页请求参数
		Pageable pageRequest = new PageRequest(page-1,rows);
		
		Specification<FixedArea> specification = new Specification<FixedArea>(){

			@Override
			public Predicate toPredicate(Root root, CriteriaQuery query,
					CriteriaBuilder cb) {
				//定义一个list封装多个条件
				List<Predicate> list = new ArrayList<Predicate>();
				
				//构造条件
				//根据定区编码
				if (StringUtils.isNotBlank(model.getId())) {
					Predicate p1 = cb.equal(root.get("id").as(String.class),model.getId());
					list.add(p1);
				}
				
				//根据所属公司查询
				if (StringUtils.isNotBlank(model.getCompany())) {
					Predicate p2 = cb.like(root.get("company").as(String.class),"%"+model.getCompany()+"%");
					list.add(p2);
				}
				
				return cb.and(list.toArray(new Predicate[0]));
			}};
			
		Page<FixedArea> pagedata = fixedAreaService.findPagedata(pageRequest,specification);
		
		pushToValueStack(pagedata);
			
		
		
		return SUCCESS;
		
	}
	
	
	
	
	
	
	
	
	
	
}
