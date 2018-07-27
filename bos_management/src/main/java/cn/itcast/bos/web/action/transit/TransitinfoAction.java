package cn.itcast.bos.web.action.transit;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.transit.ITransitinfoService;
import cn.itcast.bos.web.action.common.BaseAction;


@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class TransitinfoAction extends BaseAction<TransitInfo> {
	//注入service
	@Autowired
	private ITransitinfoService transitinfoService;
	
	
	//属性驱动获得运单id
	private String wayBillIds;
	
	public void setWayBillIds(String wayBillIds) {
		this.wayBillIds = wayBillIds;
	}



	//运单中转配送
	@Action(value="transitinfo_create",results={@Result(name="success",type="json")})
	public String create(){
		Map<String,Object> result = new HashMap<String,Object>();
		
		try {
			transitinfoService.create(wayBillIds);
			result.put("success",true);
			result.put("msg","操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success",false);
			result.put("msg","操作失败");
			
		}
		ActionContext.getContext().getValueStack().push(result);
		
		return SUCCESS;
	}
	
		//运单中转配送
		@Action(value="transitinfo_pageQuery",results={@Result(name="success",type="json")})
		public String pageQuery(){
			//封装分页请求数据
			Pageable pageable = new PageRequest(page-1,rows);
			Page<TransitInfo> pageData = transitinfoService.findAll(pageable);
			//将数据返回
			pushToValueStack(pageData);
			
			return SUCCESS;
		}
	
	
	
	
}
