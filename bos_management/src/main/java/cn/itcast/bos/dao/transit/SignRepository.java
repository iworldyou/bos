package cn.itcast.bos.dao.transit;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.itcast.bos.domain.transit.SignInfo;

public interface SignRepository extends JpaRepository<SignInfo,Integer>{

}
