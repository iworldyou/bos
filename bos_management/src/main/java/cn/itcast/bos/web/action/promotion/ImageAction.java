package cn.itcast.bos.web.action.promotion;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.web.action.common.BaseAction;

//处理富文本文件上传

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class ImageAction extends BaseAction<Object>{
	
	private File imgFile;//上传的文件
	
	private String imgFileFileName;//文件名
	
	private String imgFileContentType;//上传的文件类型

	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}
	
	
	public void setImgFileFileName(String imgFileFileName) {
		this.imgFileFileName = imgFileFileName;
	}


	public void setImgFileContentType(String imgFileContentType) {
		this.imgFileContentType = imgFileContentType;
	}
	
	//上传文件
	@Action(value="image_upload", results={@Result(name = "success", type = "json")})
	public String upload() throws IOException{
		
		System.out.println("文件"+imgFile);
		System.out.println("文件名"+imgFileFileName);
		System.out.println("文件类型"+imgFileContentType);
		
		//绝对路径
		String savePath = ServletActionContext.getServletContext().getRealPath("/upload");
		
		//相对路径
		String urlPath = ServletActionContext.getServletContext().getContextPath()+"/upload";
		
		//生成随机图片名
		UUID uuid = UUID.randomUUID();
		String ext = imgFileFileName.substring(imgFileFileName.lastIndexOf("."));//获得文件后缀
		
		String randomFileName = uuid + ext;//生成的图片名
		
		//上传(绝对路径)
		File destFile = new File(savePath+"/"+randomFileName);
		FileUtils.copyFile(imgFile,destFile);
		
		//通知浏览器上传成功
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("error",0);
		result.put("url",urlPath+randomFileName);//相对路径+文件名
		
		ActionContext.getContext().getValueStack().push(result);
		System.out.println("上传成功");
		
		return SUCCESS;
		
		
	}
	
	
	//图片空间管理
	@Action(value="image_manage",results={@Result(name = "success", type = "json")})
	public String manage(){
		
		//获得图片的绝对路径
		String rootPath = ServletActionContext.getServletContext().getRealPath("/upload/");
		File currentpathFile = new File(rootPath);
		
		//获得相对路径
		String rootUrl = ServletActionContext.getRequest().getContextPath()+"/upload/";
		// 遍历目录取的文件信息
		List<Map<String, Object>> fileList = new ArrayList<>();
		if (currentpathFile.listFiles() != null) {
			for (File file : currentpathFile.listFiles()) {
				Map<String, Object> hash = new HashMap<>();
				String fileName = file.getName();
				if (file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else {
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo", true);
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
				fileList.add(hash);
			}
		}

		Map<String, Object> result = new HashMap<>();
		result.put("moveup_dir_path", "");
		result.put("current_dir_path", rootPath);
		result.put("current_url",rootUrl );
		result.put("total_count", fileList.size());
		result.put("file_list", fileList);

		ActionContext.getContext().getValueStack().push(result);

		return SUCCESS;
	}
	
	
	
}
