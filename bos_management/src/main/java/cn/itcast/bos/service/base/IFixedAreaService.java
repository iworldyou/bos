package cn.itcast.bos.service.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.FixedArea;

public interface IFixedAreaService {

	public void save(FixedArea model);

	public Page<FixedArea> findPagedata(Pageable pageRequest,Specification<FixedArea> specification);

}
