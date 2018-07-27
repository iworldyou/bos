package cn.itcast.bos.service.transit;

import cn.itcast.bos.domain.transit.DeliveryInfo;

public interface IDeliveryService {

	public void save(DeliveryInfo model, String transitId);

}
