package cn.itcast.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.AreaRepository;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.base.IAreaService;

@Service
@Transactional
public class AreaServiceImpl implements IAreaService {
	
	//注入dao
	@Autowired
	private AreaRepository areaRepository;

	@Override
	public void save(List<Area> list) {
		//一键导入,对区域数据进行保存
		areaRepository.save(list);
		
		
	}
	
	//多条件分页查询
	@Override
	public Page<Area> findPagedata(Pageable pageRequest,Specification<Area> specification) {
		Page<Area> pageData = areaRepository.findAll(specification, pageRequest);
		return pageData;
	}

	
}
