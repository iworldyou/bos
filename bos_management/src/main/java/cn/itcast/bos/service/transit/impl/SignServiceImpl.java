package cn.itcast.bos.service.transit.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.transit.SignRepository;
import cn.itcast.bos.dao.transit.TransitinfoRepository;
import cn.itcast.bos.domain.transit.SignInfo;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.transit.ISignService;

@Service
@Transactional
public class SignServiceImpl implements ISignService {
	
	@Autowired
	private SignRepository signRepository;

	@Autowired
	private TransitinfoRepository transitRepository;
	
	//注入wayBillIndexRepository,当运单状态发生改变时,同步索引库
	@Autowired
	private WayBillIndexRepository wayBillIndexRepository;
	

	//签收信息保存操作
	@Override
	public void save(SignInfo signInfo, String transitId) {
		signRepository.save(signInfo);
		
		TransitInfo transitInfo = transitRepository.findOne(Integer.parseInt(transitId));
		transitInfo.setSignInfo(signInfo);
		
		if (signInfo.getSignType().equals("正常")) {
			transitInfo.setStatus("正常签收");
			//修改运单状态
			transitInfo.getWayBill().setSignStatus(3);
			wayBillIndexRepository.save(transitInfo.getWayBill());
			
		}else {
			transitInfo.setStatus("异常");
			transitInfo.getWayBill().setSignStatus(4);
			wayBillIndexRepository.save(transitInfo.getWayBill());
		}
		
		
		

	}

}
