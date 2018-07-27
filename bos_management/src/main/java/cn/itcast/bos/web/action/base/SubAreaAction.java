package cn.itcast.bos.web.action.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.base.IAreaService;
import cn.itcast.bos.service.base.ISubAreaService;
import cn.itcast.bos.web.action.common.BaseAction;

/**
 * 分区
 * 
 * @author zhu
 *
 */

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class SubAreaAction extends BaseAction<SubArea> {

	// 注入subAreaService
	@Autowired
	private ISubAreaService subAreaService;

	// 注入一个AreaService
	@Autowired
	private IAreaService areaService;

	// 上传的文件
	private File file;

	public void setFile(File file) {
		this.file = file;
	}

	@Action(value = "subArea_import", results = { @Result(name = "success", type = "redirect", location = "/bos_management/pages/base/sub_area.html") })
	public String subAreaImport() throws Exception {

		// 编码对上传文件进行解析
		// 加载文件
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));

		// 获得一个工作簿
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);

		// 定义一个集合封装数据
		List<SubArea> list = new ArrayList<SubArea>();

		// 解析工作簿
		for (Row row : sheet) {
			// 如果是第一行.则跳过
			if (row.getRowNum() == 0) {
				continue;
			}

			if (row.getCell(0) == null
					|| StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
				// 结束
				continue;
			}

			SubArea subArea = new SubArea();
			// 分区编号
			subArea.setId(row.getCell(0).getStringCellValue());

			// 通过省市区查询area
			Area area = areaService.findAreaByProvinceAndCityAndDistrict(row
					.getCell(1).getStringCellValue(), row.getCell(2)
					.getStringCellValue(), row.getCell(3).getStringCellValue());

			// 区域
			subArea.setArea(area);
			// 关键字
			subArea.setKeyWords(row.getCell(4).getStringCellValue());
			// 起始号
			subArea.setStartNum(row.getCell(5).getStringCellValue());
			// 终止号
			subArea.setEndNum(row.getCell(6).getStringCellValue());
			// 单双号
			subArea.setSingle(row.getCell(7).getStringCellValue().charAt(0));
			// 辅助光键字
			subArea.setAssistKeyWords(row.getCell(8).getStringCellValue());

			list.add(subArea);

		}

		subAreaService.save(list);

		System.out.println("导入成功");

		return SUCCESS;
	}

}
