package cn.itcast.bos.service.base.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.TaketimeRepository;
import cn.itcast.bos.domain.base.TakeTime;
import cn.itcast.bos.service.base.ITaketimeService;

@Service
@Transactional
public class TaketimeServiceImpl implements ITaketimeService {
	//注入dao
	@Autowired
	private TaketimeRepository taketimeRepository;
		
	@Override
	public List<TakeTime> findAll() {
	
		return taketimeRepository.findAll();
	}

}
