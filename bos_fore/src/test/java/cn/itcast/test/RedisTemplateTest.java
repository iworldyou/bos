package cn.itcast.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class RedisTemplateTest {
	
	@Autowired
	private RedisTemplate<String,String> redisTempalte;
	
	@Test
	public void test01(){
		redisTempalte.opsForValue().set("city", "北京", 60,TimeUnit.SECONDS);
		
		System.out.println(redisTempalte.opsForValue().get("city"));
		
	}
}
