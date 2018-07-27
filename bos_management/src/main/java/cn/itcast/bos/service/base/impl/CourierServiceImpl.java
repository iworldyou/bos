package cn.itcast.bos.service.base.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
	@RequiresPermissions("courier_add")//通过注解给添加快递员加入权限
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
	
	//查询未关联定区的快递员
	@Override
	public List<Courier> findnoassociation() {
		//构造条件
		Specification<Courier> specification = new Specification<Courier>(){
			
			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query,
					CriteriaBuilder cb){
				//构造快递员定区为空的条件
				Predicate p = cb.isEmpty(root.get("fixedAreas").as(Set.class));
				return p;
			}


		};
		
		return courierRepository.findAll(specification);
	}
	
	
	

}
