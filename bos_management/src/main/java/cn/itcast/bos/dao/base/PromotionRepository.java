package cn.itcast.bos.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.itcast.bos.domain.take_delivery.Promotion;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

}
