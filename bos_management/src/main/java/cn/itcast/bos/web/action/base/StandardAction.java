package cn.itcast.bos.web.action.base;

import java.util.HashMap;
import java.util.List;
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
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.IStandardService;



@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class StandardAction extends ActionSupport implements ModelDriven<Standard>{
	
	private Standard s = new Standard();
	
	
	@Autowired
	private IStandardService standardService;

	@Override
	public Standard getModel() {
		return s;
	}
	
	//添加收派标准
	@Action(value="standard_save", results = { @Result(name = "success", type = "redirect", location = "./pages/base/standard.html")})
	public String save(){
		
		standardService.save(s);
		
		return SUCCESS;
		
	}
	
	// 属性驱动
	private int page;//页数
	private int rows;//每页条数
	
	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	//收派标准分页查询
	@Action(value = "standard_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery(){
		
		//传入page和rows
		Pageable pageable = new PageRequest(page - 1, rows);
		
		// 调用业务层 ，查询数据结果
		Page<Standard> pageData = standardService.findPageData(pageable);

		// 返回客户端数据 需要 total 和 rows
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pageData.getTotalElements());
		result.put("rows", pageData.getContent());

		// 将map转换为json数据返回 ，使用struts2-json-plugin 插件
		ActionContext.getContext().getValueStack().push(result);

		return SUCCESS;
	}
	
	//查询收派标准
		@Action(value = "standard_find", results = { @Result(name = "success", type = "json") })
		public String findAllStandard(){
			List<Standard> list = standardService.findAll();
			ActionContext.getContext().getValueStack().push(list);
			
			return SUCCESS;
		}
	
	
	
	

}
