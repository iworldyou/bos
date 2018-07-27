package cn.itcast.bos.service.transit.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.transit.DeliveryRepository;
import cn.itcast.bos.dao.transit.TransitinfoRepository;
import cn.itcast.bos.domain.transit.DeliveryInfo;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.transit.IDeliveryService;

@Service
@Transactional
public class DeliveryServiceImpl implements IDeliveryService{
	@Autowired
	private DeliveryRepository deliveryRepository;
	
	@Autowired
	private TransitinfoRepository transitRepository;
	
	@Override
	public void save(DeliveryInfo deliveryInfo, String transitId) {
		deliveryRepository.save(deliveryInfo);
		
		TransitInfo transitInfo = transitRepository.findOne(Integer.parseInt(transitId));
		transitInfo.setDeliveryInfo(deliveryInfo);
		transitInfo.setStatus("开始配送");
		
	}

}
