package cn.itcast.crm.service.base.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.crm.dao.base.CustomerRepository;
import cn.itcast.crm.domain.Customer;
import cn.itcast.crm.service.base.ICustomerService;

@Service
@Transactional
public class CustomerServiceImpl implements ICustomerService {
	
	//注入dao
	@Autowired
	private CustomerRepository customerRepository;
	
	//查询未关联到定区的客户
	@Override
	public List<Customer> findNoAssociationFixedAreaCustomer() {
		
		return customerRepository.findByFixedAreaIdIsNull();
	}
	
	//查询已关联到定区的客户
	@Override
	public List<Customer> findHasAssociationFixedAreaCustomer(String fixedAreaId) {
		
		return customerRepository.findByFixedAreaId(fixedAreaId);
	}
	
	//将未关联到定区的客户关联到定区
	@Override
	public void associationCustomerToFixedArea(String customerIdStr,
			String fixedAreaId) {
		System.out.println(customerIdStr);
		//先将客户定区为fixedAreaId的值设为null,之后再进行添加
		customerRepository.clearCustomerFixedAreaId(fixedAreaId);
		if (StringUtils.isBlank(customerIdStr) || customerIdStr.equals("null")) {
			return;
		}
		//将客户id字符串分解
		String[] ids = customerIdStr.split(",");
		for (String idStr : ids) {
			//获得客户id
			Integer id = Integer.parseInt(idStr); 
			customerRepository.associationCustomerToFixedArea(fixedAreaId,id);
		}
		
		
	}


}
