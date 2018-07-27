package cn.itcast.bos.dao.transit;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.itcast.bos.domain.transit.InOutStorageInfo;

public interface InOutStorageRepository extends JpaRepository<InOutStorageInfo,Integer>{

}
