package cn.itcast.bos.web.action.base;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.service.base.IWayBillService;
import cn.itcast.bos.utils.FileUtils;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class WayBillAction<JasperReport> extends BaseAction<WayBill> {

	// 注入service
	@Autowired
	private IWayBillService wayBillService;

	private static final Logger logger = Logger.getLogger(WayBill.class);

	// 工单快速录入保存操作waybill_save
	@Action(value = "waybill_save", results = { @Result(name = "success", type = "json")})
	public String save() {
		
		if (model.getOrder()!=null && model.getOrder().getId()==null || model.getOrder().getId()==0) {
			model.setOrder(null);
		}
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			wayBillService.save(model);
			// 保存成功
			map.put("success", true);
			map.put("msg", "保存成功");
			logger.info("保存运单成功,运单编号:" + model.getWayBillNum());
		} catch (Exception e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "保存失败");
			logger.error("保存运单失败,运单编号:" + model.getWayBillNum());
		}

		ActionContext.getContext().getValueStack().push(map);

		return SUCCESS;
	}

	// 分页查询waybill_pageQuery
	@Action(value = "waybill_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		// 封装分页数据
		Pageable pageRequest = new PageRequest(page - 1, rows);
		
		//查询分页数据
		Page<WayBill> pagedata = wayBillService.pageQuery(model,pageRequest);
		
		pushToValueStack(pagedata);

		return SUCCESS;
	}
	
	
	//根据waybillNum查询
	@Action(value="waybill_find",results={@Result(name="success",type="json")})
	public String findByWaybillNum(){
		WayBill wayBill = wayBillService.findByWayBillNum(model.getWayBillNum());
		
	
		Map<String,Object> map = new HashMap<String,Object>();
		
		if (wayBill!=null) {
			//存在
			map.put("success",true);
			map.put("waybillData",wayBill);
			
		}else {
			//不存在
			map.put("success",false);
			map.put("msg","运单号不存在");
		}
		
		ActionContext.getContext().getValueStack().push(map);
		return SUCCESS;
	}
	
	
	//导出运单表格的操作
	@Action(value="export_excel")
	public String exportExcel() throws IOException{
		//获得运单数据
		List<WayBill> wayBills = wayBillService.findWayBill(model);
		
		//将运单数据封装到Excel对象中
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
		
		HSSFSheet sheet = hssfWorkbook.createSheet("运单数据");
		//设置表头
		HSSFRow headRow = sheet.createRow(0);
		headRow.createCell(0).setCellValue("运单编号");
		headRow.createCell(1).setCellValue("寄件人");
		headRow.createCell(2).setCellValue("寄件人电话");
		headRow.createCell(3).setCellValue("寄件人公司");
		headRow.createCell(4).setCellValue("寄件人地址");
		headRow.createCell(5).setCellValue("收件人");
		headRow.createCell(6).setCellValue("收件人电话");
		headRow.createCell(7).setCellValue("收件人公司");
		headRow.createCell(8).setCellValue("收件人地址");
		
		//设置表记录
		for (WayBill wayBill : wayBills) {
			HSSFRow row = sheet.createRow(sheet.getLastRowNum()+1);
			row.createCell(0).setCellValue(wayBill.getWayBillNum());
			row.createCell(1).setCellValue(wayBill.getSendName());
			row.createCell(2).setCellValue(wayBill.getSendMobile());
			row.createCell(3).setCellValue(wayBill.getSendCompany());
			row.createCell(4).setCellValue(wayBill.getSendAddress());
			row.createCell(5).setCellValue(wayBill.getRecName());
			row.createCell(6).setCellValue(wayBill.getRecMobile());
			row.createCell(7).setCellValue(wayBill.getRecCompany());
			row.createCell(8).setCellValue(wayBill.getRecAddress());
		}
		
		//下载导出
		ServletActionContext.getResponse().setContentType(
				"application/vnd.ms-excel");
		String filename = "运单数据.xls";
		String agent = ServletActionContext.getRequest()
				.getHeader("user-agent");
		filename = FileUtils.encodeDownloadFilename(filename, agent);
		ServletActionContext.getResponse().setHeader("Content-Disposition",
				"attachment;filename=" + filename);

		ServletOutputStream outputStream = ServletActionContext.getResponse()
				.getOutputStream();
		hssfWorkbook.write(outputStream);

		// 关闭
		hssfWorkbook.close();
		
		return NONE;
	}
	
	
	//导出pdf的操作
	@Action(value="export_pdf")
	public String exportPdf() throws IOException, DocumentException{
		//获得运单数据
		List<WayBill> wayBills = wayBillService.findWayBill(model);
		// 下载导出
				// 设置头信息
				ServletActionContext.getResponse().setContentType("application/pdf");
				String filename = "运单数据.pdf";
				String agent = ServletActionContext.getRequest()
						.getHeader("user-agent");
				filename = FileUtils.encodeDownloadFilename(filename, agent);
				ServletActionContext.getResponse().setHeader("Content-Disposition",
						"attachment;filename=" + filename);

				// 生成PDF文件
				Document document = new Document();
				PdfWriter.getInstance(document, ServletActionContext.getResponse()
						.getOutputStream());
				document.open();
				// 写PDF数据
				// 向document 生成pdf表格
				Table table = new Table(7);
				table.setWidth(80); // 宽度
				table.setBorder(1); // 边框
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); // 水平对齐方式
				table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP); // 垂直对齐方式

				// 设置表格字体
				BaseFont cn = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H",
						false);
				Font font = new Font(cn, 10, Font.NORMAL, Color.BLUE);

				// 写表头
				table.addCell(buildCell("运单号", font));
				table.addCell(buildCell("寄件人", font));
				table.addCell(buildCell("寄件人电话", font));
				table.addCell(buildCell("寄件人地址", font));
				table.addCell(buildCell("收件人", font));
				table.addCell(buildCell("收件人电话", font));
				table.addCell(buildCell("收件人地址", font));
				// 写数据
				for (WayBill wayBill : wayBills) {
					table.addCell(buildCell(wayBill.getWayBillNum(), font));
					table.addCell(buildCell(wayBill.getSendName(), font));
					table.addCell(buildCell(wayBill.getSendMobile(), font));
					table.addCell(buildCell(wayBill.getSendAddress(), font));
					table.addCell(buildCell(wayBill.getRecName(), font));
					table.addCell(buildCell(wayBill.getRecMobile(), font));
					table.addCell(buildCell(wayBill.getRecAddress(), font));
				}
				// 将表格加入文档
				document.add(table);

				document.close();
	
		
		
		
		return NONE;
	}
	
	
	private Cell buildCell(String content, Font font)
			throws BadElementException {
		Phrase phrase = new Phrase(content, font);
		return new Cell(phrase);
	}
	
	
	
	
	
	
	
	
	
	

}
