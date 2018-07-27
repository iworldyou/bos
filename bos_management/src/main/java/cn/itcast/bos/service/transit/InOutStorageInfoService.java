package cn.itcast.bos.service.transit;

import cn.itcast.bos.domain.transit.InOutStorageInfo;

public interface InOutStorageInfoService {

	public void save(InOutStorageInfo model, String transitId);

}
