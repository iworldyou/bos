package cn.itcast.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.SubAreaRepository;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.base.ISubAreaService;

@Service
@Transactional
public class SubAreaServiceImpl implements ISubAreaService {
	
	//注入dao
	@Autowired
	private SubAreaRepository subAreaRepository;
	

	@Override
	public void save(List<SubArea> list) {
		subAreaRepository.save(list);
	}


}
