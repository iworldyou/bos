package cn.itcast.bos.service.base;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Courier;

public interface ICourierService {

	public void save(Courier c);

	public Page<Courier> findPagedata(Pageable pageRequest, Specification<Courier> specification);

	//Page<Courier> findPagedata(Pageable pageRequest);

	public void delBatch(String[] idarray);

	public List<Courier> findnoassociation();

}
