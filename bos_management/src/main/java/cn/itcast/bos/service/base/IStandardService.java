package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.base.Standard;

public interface IStandardService {

	public void save(Standard s);
	
	//分页查询
	public Page<Standard> findPageData(Pageable pageable);

	public List<Standard> findAll();
	

}
