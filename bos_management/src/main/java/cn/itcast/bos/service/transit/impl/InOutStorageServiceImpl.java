package cn.itcast.bos.service.transit.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.WayBillRepository;
import cn.itcast.bos.dao.transit.InOutStorageRepository;
import cn.itcast.bos.dao.transit.TransitinfoRepository;
import cn.itcast.bos.domain.transit.InOutStorageInfo;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.transit.ITransitinfoService;
import cn.itcast.bos.service.transit.InOutStorageInfoService;

@Service
@Transactional
public class InOutStorageServiceImpl implements InOutStorageInfoService {

	//注入transitinfo
	@Autowired
	private TransitinfoRepository transitRepository;
	
	@Autowired
	private WayBillRepository wayBillRepository;
	
	@Autowired
	private InOutStorageRepository inOutRepository;
	
	@Override
	public void save(InOutStorageInfo inOutStorageInfo, String transitId) {
		inOutRepository.save(inOutStorageInfo);
		
		TransitInfo transitInfo = transitRepository.findOne(Integer.parseInt(transitId));
		transitInfo.getInOutStorageInfos().add(inOutStorageInfo);
		
		//修改配运状态
		if (inOutStorageInfo.getOperation().equals("到达网点")) {
			transitInfo.setStatus("到达网点");
			//跟新网点地址
			transitInfo.setOutletAddress(inOutStorageInfo.getAddress());
		}
		
	

	}

}
