package cn.itcast.bos.service.transit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.transit.TransitInfo;

public interface ITransitinfoService {

	public void create(String wayBillIds);


	public Page<TransitInfo> findAll(Pageable pageable);

}
