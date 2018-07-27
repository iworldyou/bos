package cn.itcast.crm.dao.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.crm.domain.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Integer>{
	
	//查询未关联到定区的客户
	public List<Customer> findByFixedAreaIdIsNull();
	
	//查询已关联到定区的客户
	public List<Customer> findByFixedAreaId(String fixedAreaId);
	
	//将客户关联到定区上
	@Query("update Customer set fixedAreaId = ? where id= ?")
	@Modifying
	public void associationCustomerToFixedArea(String fixedAreaId, Integer id);
	
	@Query("update Customer set fixedAreaId = null where fixedAreaId = ?")
	@Modifying
	public void clearCustomerFixedAreaId(String fixedAreaId);
	
	
	@Query("from Customer where telephone = ?")
	public Customer findByTelphone(String telephone);
	
	@Query("update Customer set type = 1 where telephone = ?")
	@Modifying
	public void updateType(String telephone);

	public Customer findByTelephoneAndPassword(String telephone, String password);

	
	//根据寄件地址--对应客户地址,查询定区fixedAreaId
	@Query("select fixedAreaId from Customer where address = ?")
	public String findFixedAreaIdByAddress(String sendAddress);

	
	

	
}
