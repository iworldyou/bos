package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Area;

public interface IAreaService {

	public void save(List<Area> list);

	public Page<Area> findPagedata(Pageable pageRequest,Specification<Area> specification);

	public Area findAreaByProvinceAndCityAndDistrict(String province,
			String city, String district);

}
