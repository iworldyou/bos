package cn.itcast.bos.dao.base;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.bos.domain.base.SubArea;

public interface SubAreaRepository extends JpaRepository<SubArea,String>{

	
	
	@Query("from SubArea where c_area_id=?")
	public List<SubArea> findByAreaId(String id);



}
