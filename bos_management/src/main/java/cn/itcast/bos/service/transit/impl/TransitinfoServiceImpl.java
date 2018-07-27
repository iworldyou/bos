package cn.itcast.bos.service.transit.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.WayBillRepository;
import cn.itcast.bos.dao.transit.TransitinfoRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.transit.ITransitinfoService;

@Service
@Transactional
public class TransitinfoServiceImpl implements ITransitinfoService{
	//注入dao
	@Autowired
	private TransitinfoRepository transitRepository;
	
	@Autowired
	private WayBillRepository wayBillRepository;
	
	@Override
	public void create(String wayBillIds) {
		//获得运单id
		if (wayBillIds!=null) {
			String[] ids = wayBillIds.split(",");
			for (String id : ids) {
				//查询运单
				WayBill wayBill = wayBillRepository.findOne(Integer.parseInt(id));
				
				if (wayBill.getSignStatus()==1) {
					//待发货
					//创建一个配运信息
					TransitInfo transitInfo = new TransitInfo();
					transitInfo.setWayBill(wayBill);
					transitInfo.setStatus("出入库中转");
					//保存
					transitRepository.save(transitInfo);
					//修改运单的状态
					wayBill.setSignStatus(2);
				}
				
			}
		}
	}

	@Override
	public Page<TransitInfo> findAll(Pageable pageable) {
		Page<TransitInfo> pageData = transitRepository.findAll(pageable);
		return pageData;
	}
	

}
