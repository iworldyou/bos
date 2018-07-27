package cn.itcast.bos.service.base.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.PromotionRepository;
import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.page.PageBean;
import cn.itcast.bos.service.base.IPromotionService;

@Service
@Transactional
public class PromotionServiceImpl implements IPromotionService {
	// 注入Jparepository
	@Autowired
	private PromotionRepository promotionRepository;

	@Override
	public void save(Promotion promotion) {
		promotionRepository.save(promotion);
	}

	// 分页数据查询
	@Override
	public Page<Promotion> findPageData(Pageable pageable) {
		Page<Promotion> page = promotionRepository.findAll(pageable);
		return page;
	}

	@Override
	public PageBean<Promotion> findAll(int page, int rows) {
		// 封装分页的参数
		Pageable pageable = new PageRequest(page - 1, rows);

		Page<Promotion> pagedata = promotionRepository.findAll(pageable);

		PageBean<Promotion> pageBean = new PageBean<Promotion>();

		pageBean.setTotalCount(pagedata.getTotalElements());
		pageBean.setPageData(pagedata.getContent());

		return pageBean;
	}
	
	//根据id查询促销信息
	@Override
	public Promotion findById(Integer id) {

		return promotionRepository.findOne(id);
	}


	@Override
	public void updateStatus(Date now) {
		promotionRepository.updateStatus(now);
	}

}
