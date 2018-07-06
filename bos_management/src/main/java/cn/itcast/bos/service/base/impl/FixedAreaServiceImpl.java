package cn.itcast.bos.service.base.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.CourierRepository;
import cn.itcast.bos.dao.base.FixedAreaRepository;
import cn.itcast.bos.dao.base.TaketimeRepository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.TakeTime;
import cn.itcast.bos.service.base.IFixedAreaService;

@Service
@Transactional
public class FixedAreaServiceImpl implements IFixedAreaService{

	//注入dao
	@Autowired
	private FixedAreaRepository fixedAreaRepository;
	
	@Override
	public void save(FixedArea fixedArea) {
		fixedAreaRepository.save(fixedArea);
		
	}
	
	//多条件分页查询-定区
	@Override
	public Page<FixedArea> findPagedata(Pageable pageRequest,Specification<FixedArea> specification) {
		Page<FixedArea> pageData = fixedAreaRepository.findAll(specification, pageRequest);
		return pageData;
	}

	
	//注入courierRepository和taketimeRepository
	@Autowired
	private CourierRepository courierRepository;
	
	@Autowired
	private TaketimeRepository taketimeRepository;
	
	
	//关联快递员到定区
	@Override
	public void associationCourierToFixedArea(Integer courierId,Integer taketimeId, String id) {
		//根据id查询
		Courier courier = courierRepository.findOne(courierId);
		TakeTime takeTime = taketimeRepository.findOne(taketimeId);
		FixedArea fixedArea = fixedAreaRepository.findOne(id);
		
		//定区关联快递员
		fixedArea.getCouriers().add(courier);
		
		//快递员关联送派时间
		courier.setTakeTime(takeTime);
	}



}
