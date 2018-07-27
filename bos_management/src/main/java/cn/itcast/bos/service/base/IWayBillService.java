package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.take_delivery.WayBill;

public interface IWayBillService {

	public void save(WayBill wayBill);

	public Page<WayBill> pageQuery(WayBill model, Pageable pageRequest);

	public WayBill findByWayBillNum(String wayBillNum);
	
	//跟新索引库
	public void getNew();

	public List<WayBill> findWayBill(WayBill model);

}
