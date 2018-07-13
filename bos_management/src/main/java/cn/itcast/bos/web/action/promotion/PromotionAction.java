package cn.itcast.bos.web.action.promotion;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
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

import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.base.IPromotionService;
import cn.itcast.bos.web.action.common.BaseAction;

/*
 * 促销保存
 */

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PromotionAction extends BaseAction<Promotion> {
	
	//注入service
	@Autowired
	private IPromotionService promotionService;
	
	
	//上传的文件
	private File titleImgFile;
	
	//文件名
	private String titleImgFileFileName;
	

	public void setTitleImgFile(File titleImgFile) {
		this.titleImgFile = titleImgFile;
	}
	
	
	public void setTitleImgFileFileName(String titleImgFileFileName) {
		this.titleImgFileFileName = titleImgFileFileName;
	}

	
	//promotion的添加保存操作
	@Action(value="promotion_add", results={@Result(name = "success",type="redirect",location="./pages/take_delivery/promotion.html")})
	public String save() throws IOException{
		//上传文件的绝对路径
		String realPath = ServletActionContext.getServletContext().getRealPath("/upload/");
		//相对路径
		String urlPath = ServletActionContext.getRequest().getContextPath()+"/upload/";
		//获得上传图片的随机名字
		UUID uuid = UUID.randomUUID();
		//上传文件的文件名后缀
		String ext = titleImgFileFileName.substring(titleImgFileFileName.lastIndexOf("."));
		
		//上传后的文件名
		String randomFileName = uuid + ext;
		
		//上传
		FileUtils.copyFile(titleImgFile, new File(realPath,randomFileName));
		
		//保存相对路径到model中
		model.setTitleImg(urlPath+randomFileName);
		
		//保存到数据库的操作
		promotionService.save(model);
		return SUCCESS;
	}
	
	//分页查询
	@Action(value="promotion_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		//封装分页请求参数
		Pageable pageable = new PageRequest(page-1,rows);
		
		//分页数据查询
		Page<Promotion> pageData = promotionService.findPageData(pageable);
		
		//返回jso数据
		pushToValueStack(pageData);
		
		return SUCCESS;
		
	}
	
	
	

}
