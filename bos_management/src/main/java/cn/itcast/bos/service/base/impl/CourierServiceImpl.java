package cn.itcast.bos.service.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.CourierRepository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.service.base.ICourierService;

@Service
@Transactional
public class CourierServiceImpl implements ICourierService {

	@Autowired
	private CourierRepository courierRepository;

	@Override
	public void save(Courier c) {
		courierRepository.save(c);
	}

	/*
	// 查询分页数据
	@Override
	public Page<Courier> findPagedata(Pageable pageRequest) {

		Page<Courier> pagedata = courierRepository.findAll(pageRequest);
		return pagedata;
	}

*/	
	//条件查询
	@Override
	public Page<Courier> findPagedata(Pageable pageRequest,Specification<Courier> specification) {
		Page<Courier> pageData = courierRepository.findAll(specification, pageRequest);	
		
		return pageData;
	}

	@Override
	public void delBatch(String[] idarray) {
		for (String s : idarray) {
			Integer id = Integer.parseInt(s);
			courierRepository.updateDeltag(id);
		}
		
		
		
	}
	
	
	

}