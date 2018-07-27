package cn.itcast.bos.service.base.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.StandardRepository;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.IStandardService;


@Service
@Transactional
public class StandServiceImpl implements IStandardService {
	
	@Autowired
	private StandardRepository standardRepository;
	
	
	@Override
	@CacheEvict(value="standard",allEntries=true)//清除缓存区,用于增加,删除,修改
	public void save(Standard s) {
		standardRepository.save(s);
	}


	@Override
	public Page<Standard> findPageData(Pageable pageable) {
		
		return standardRepository.findAll(pageable);
	}


	@Override
	@Cacheable("standard")//cacheable应用缓存区,用于查询方法
	public List<Standard> findAll() {
		return standardRepository.findAll();
	}

}
