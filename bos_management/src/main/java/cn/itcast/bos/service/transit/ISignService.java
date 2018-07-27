package cn.itcast.bos.service.transit;

import cn.itcast.bos.domain.transit.SignInfo;

public interface ISignService {

	public void save(SignInfo model, String transitId);

}
