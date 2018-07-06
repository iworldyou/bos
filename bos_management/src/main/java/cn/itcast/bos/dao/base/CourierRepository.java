package cn.itcast.bos.dao.base;

import java.util.List;

import javax.naming.ldap.ExtendedRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.bos.domain.base.Courier;

public interface CourierRepository extends JpaRepository<Courier, Integer>,JpaSpecificationExecutor<Courier>{
	
	//根据id删出收派员
	@Query("update Courier set deltag='1' where id=?")
	@Modifying
	public void updateDeltag(Integer id);
	
	

		
}
