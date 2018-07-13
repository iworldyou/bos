package cn.itcast.bos.web.action.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
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

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.IAreaService;
import cn.itcast.bos.utils.PinYin4jUtils;
import cn.itcast.bos.web.action.common.BaseAction;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class AreaAction extends BaseAction<Area>{
	
	//注入service
	@Autowired
	private IAreaService areaService; 
	
	
	
	//上传的文件
	private File file;

	//属性驱动
	public void setFile(File file) {
		this.file = file;
	}
	
	
	//解析上传的文件,并完成数据封装
	@Action(value="area_import",results={@Result(name="success",type="redirect",location="./pages/base/area.html")})
	public String areaImport() throws IOException{
		//编码对上传文件进行解析
		//.xsl文件
		//加载文集对象
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
		//解析文件的一个工作sheet
		//hssfWorkbook.getSheet(name)
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
		
		//定义一个集合,封装多行数据
		List<Area> list =  new ArrayList<Area>();
		
		//读取文件中的每一行
		for (Row row : sheet) {
			
			if (row.getRowNum()==0) {
				//跳过第一行
				continue;
			}
			
			if (row.getCell(0)==null || StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
				//结束
				continue;
			}
			
			//定义一个Area对每一行数据进行封装
			Area a= new Area();
			
			a.setId(row.getCell(0).getStringCellValue());//区域编号
			a.setProvince(row.getCell(1).getStringCellValue());//省份
			a.setCity(row.getCell(2).getStringCellValue());//城市
			a.setDistrict(row.getCell(3).getStringCellValue());//区域
			a.setPostcode(row.getCell(4).getStringCellValue());//邮编
			
			//使用pinyin4j生成城市编码和简码
			String province = a.getProvince();//省
			String city = a.getCity();//市
			String district = a.getDistrict();
			
			province = province.substring(0, province.length()-1);
			city = city.substring(0, city.length()-1);
			district = district.substring(0, district.length()-1);
			
			
			//生成简码
			String[] headArray = PinYin4jUtils.getHeadByString(province+city+district);
			
			//将数组转换成字符串
			StringBuilder sb = new StringBuilder();
			for (String s : headArray) {
				sb.append(s);
			}
			
			String shortcode = sb.toString();//简码
			a.setShortcode(shortcode);
			
			//生成城市编码
			String citycode = PinYin4jUtils.hanziToPinyin(city,"");
			a.setCitycode(citycode);
			
			list.add(a);
			
		}

		//调用service方法,进行保存操作
		areaService.save(list);
		
		return SUCCESS;
	}
	
	
		
		//条件查询
		@Action(value="area_pageQuery", results = { @Result(name = "success", type = "json") })
		public String pageQuery(){
			//封装分页的参数
			Pageable pageRequest = new PageRequest(page-1, rows);
			
			//封装条件查询
			Specification<Area> specification = new Specification<Area>(){

				@Override
				public Predicate toPredicate(Root root, CriteriaQuery query,	
						CriteriaBuilder cb) {
					//query为简单条件查询,cb用于构造复杂条件查询
					//定义一个集合封装多个条件
					List<Predicate> list = new ArrayList<Predicate>();
					
					//简单单表
					if (StringUtils.isNotBlank(model.getProvince())) {
						//省份 --相当于like?
						Predicate p1 = cb.like(root.get("province").as(String.class),"%"+model.getProvince()+"%");
						list.add(p1);
					}
					
					if (StringUtils.isNotBlank(model.getCity())) {
						//市--like? 模糊查询
						Predicate p2 = cb.like(root.get("city").as(String.class),"%"+model.getCity()+"%");
						list.add(p2);
						
					}
					
					if (StringUtils.isNotBlank(model.getDistrict())) {
						//市--like? 模糊查询
						Predicate p2 = cb.like(root.get("district").as(String.class),"%"+model.getDistrict()+"%");
						list.add(p2);
					}
					
					return cb.and(list.toArray(new Predicate[0]));
					
				}};
				

				//查询分页数据
				Page<Area> pagedata = areaService.findPagedata(pageRequest,specification);
				
				//调用父类方法
				pushToValueStack(pagedata);
				
				return SUCCESS;
		}
	
	
	public static void main(String[] args) {
		AreaAction action = new AreaAction();
		
	}
	
	
	
	

}
