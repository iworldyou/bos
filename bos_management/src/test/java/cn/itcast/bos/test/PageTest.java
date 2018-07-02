package cn.itcast.bos.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.bos.dao.base.StandardRepository;
import cn.itcast.bos.domain.base.Standard;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class PageTest {
	
	//注入
	@Autowired
	private StandardRepository standardRepository;
	
	//分页
	@Test
	public void test01(){
		//多态
		Pageable pageRequest = new PageRequest(1-1, 3);
		
		//获得分页数据
		Page<Standard> pageData = standardRepository.findAll(pageRequest);
		
		System.out.println("当前页数"+pageData.getNumber());
		System.out.println("总页数"+pageData.getTotalPages());
		System.out.println("当前页数据"+pageData.getContent());
		
		
		
	}
	
	
	

}
