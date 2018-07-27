package cn.itcast.bos.web.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.page.PageBean;
import cn.itcast.bos.web.action.common.BaseAction;
import freemarker.core.Configurable;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PromotionAction extends BaseAction<Promotion> {

	// 促销的分页查询
	@Action(value = "promotion_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		// 基于WebService,使用WebCilent获取bos_management中的promotion的数据
		PageBean<Promotion> pageBean = WebClient
				.create("http://localhost:8080/bos_management/services/promotionService/findAll?page="
						+ page + "&rows=" + rows)
				.accept(MediaType.APPLICATION_JSON).get(PageBean.class);

		ActionContext.getContext().getValueStack().push(pageBean);

		return SUCCESS;
	}

	// 查询促销详情
	@Action(value = "promotion_showDetail")
	public String showDetail() throws IOException, Exception {
		// 根据id判断对应的html是否存在,如果存在,直接返回
		// 获得静态html的路径
		String htmlRealPath = ServletActionContext.getServletContext()
				.getRealPath("/freemarket");

		// 根据id查询对应的html
		File htmlFile = new File(htmlRealPath + "/" + model.getId() + ".html");
		// 判断
		if (!htmlFile.exists()) {
			// 不存在
			Configuration configuration = new Configuration(
					Configuration.VERSION_2_3_22);

			configuration.setDirectoryForTemplateLoading(new File(
					ServletActionContext.getServletContext().getRealPath(
							"/WEB-INF/freemarket_template")));
			// 获得模板对象
			Template template = configuration
					.getTemplate("promotion_detail.ftl");

			// 获取id动态数据
			Promotion promotion = WebClient
					.create("http://localhost:8080/bos_management/services/promotionService/promotion?id="
							+ model.getId()).accept(MediaType.APPLICATION_JSON)
					.get(Promotion.class);

			// 定义一个Map
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("promotion", promotion);

			// 合并输出
			template.process(map, new OutputStreamWriter(new FileOutputStream(
					htmlFile), "utf-8"));

		}

		// 存在,直接返回
		FileUtils.copyFile(htmlFile, ServletActionContext.getResponse()
				.getOutputStream());

		return NONE;
	}

}
