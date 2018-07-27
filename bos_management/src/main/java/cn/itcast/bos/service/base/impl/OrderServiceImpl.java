package cn.itcast.bos.service.base.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.constant.Constant;
import cn.itcast.bos.dao.base.AreaRepository;
import cn.itcast.bos.dao.base.FixedAreaRepository;
import cn.itcast.bos.dao.base.OrderRepository;
import cn.itcast.bos.dao.base.SubAreaRepository;
import cn.itcast.bos.dao.base.WorkBillRepository;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.domain.take_delivery.WorkBill;
import cn.itcast.bos.service.base.IOrderService;
import cn.itcast.bos.service.base.ISubAreaService;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService {
	// 注入一个OrderRepository
	@Autowired
	private OrderRepository orderRepository;

	// 注入fixedAreaRepository
	@Autowired
	private FixedAreaRepository fixedAreaRepository;

	// 注入AreaRepository
	@Autowired
	private AreaRepository areaRepository;

	// 注入subRepository
	@Autowired
	private SubAreaRepository subAreaRepository;

	// 注入WorkBillRepository
	@Autowired
	private WorkBillRepository workBillRepository;

	// 注入JMS--ActivateMQ模板
	@Autowired
	@Qualifier("jmsQueueTemplate")
	private JmsTemplate jmsTemplate;

	// 订单添加操作 1.自动分单 2.人工分单
	@Override
	public void save(Order order) {

		// 生成订单编号
		order.setOrderNum(UUID.randomUUID().toString());

		// 根据寄件地址和收件地址查询,将瞬时态的Area变成持久态
		// 寄件
		Area sendArea = areaRepository.findAreaByProvinceAndCityAndDistrict(
				order.getSendArea().getProvince(), order.getSendArea()
						.getCity(), order.getSendArea().getDistrict());

		// 收件
		Area recArea = areaRepository.findAreaByProvinceAndCityAndDistrict(
				order.getSendArea().getProvince(), order.getSendArea()
						.getCity(), order.getSendArea().getDistrict());

		// 订单绑定收件和寄件地区
		order.setSendArea(sendArea);
		order.setRecArea(recArea);

		// 第一种情况---> 判断订单中的地址与客户地址是否相同
		String fixedAreaId = WebClient
				.create(Constant.crm_management
						+ "/crm_management/services/customerService/findFixedAreaIdByAddress?address="
						+ order.getSendAddress())
				.accept(MediaType.APPLICATION_JSON).get(String.class);

		// 判断
		if (fixedAreaId != null) {
			// 可以实现自动分单
			// 通过定区id查询定区
			FixedArea fixedArea = fixedAreaRepository.findOne(fixedAreaId);

			// 通过定区查询快递员
			Courier courier = fixedArea.getCouriers().iterator().next();
			System.out.println(courier);

			if (courier != null) {
				// 生成订单
				saveOrder(order, courier);

				// 生成工单
				// 发送短信
				createWorkBill(order);
				return;
			}
		}

		/*
		 * 第二种情况,当寄件地址和客户注册时地址不同时,通过寄件的区域查询,找到所有的分区,分区关键字与寄件地址进行匹配,进而确定分区,
		 * 再通过分区找到对应的定区,定区再去找到关联的快递员
		 */

		// 通过寄件区域sendArea,获得所有分区
		List<SubArea> list = subAreaRepository.findByAreaId(sendArea.getId());

		// 遍历分区
		for (SubArea subArea : list) {
			// 判断
			if (order.getSendAddress().contains(subArea.getKeyWords())
					|| order.getSendAddress().contains(
							subArea.getAssistKeyWords())) {
				// 分区关键字或辅助关键字中包含寄件地址,则可自动分单
				// 通过分区找到定区
				FixedArea fixedArea = fixedAreaRepository.findOne(subArea
						.getFixedArea().getId());

				// 通过定区找到快递员
				Iterator<Courier> iterator = fixedArea.getCouriers().iterator();
				if (iterator.hasNext()) {
					Courier courier = iterator.next();
					if (courier != null) {
						saveOrder(order, courier);

						// 生成工单
						// 发送短信
						createWorkBill(order);

						return;
					}
				}
			}
		}

		/*
		 * 第三种情况,人工分单
		 */
		order.setOrderType("2");
		orderRepository.save(order);

	}

	// 保存订单
	public void saveOrder(Order order, Courier courier) {
		// 自动分单
		order.setCourier(courier);
		order.setOrderType("1");//分单类型
		order.setStatus("1");//订单状态

		orderRepository.save(order);

		System.out.println("自动分单成功");
	}

	// 生成工单
	public void createWorkBill(final Order order) {
		WorkBill workBill = new WorkBill();
		// 工单类型
		workBill.setType("新");
		// 取件状态
		workBill.setPickstate("新单");
		// 工单生成时间
		workBill.setBuildtime(new Date());
		// 备注
		workBill.setRemark(order.getRemark());
		// 短信序号
		String smsNumber = RandomStringUtils.randomNumeric(4);
		workBill.setSmsNumber(smsNumber);
		// 快递员
		workBill.setCourier(order.getCourier());
		// 订单
		workBill.setOrder(order);

		// 工单保存
		workBillRepository.save(workBill);
		System.out.println("工单生成------------");

		// 给快递员发送短信
		// 调用MQ发送短信
		final String msg = "短信序号:" + smsNumber + ",取件地址:"
				+ order.getSendAddress() + ",联系人:" + order.getSendName()
				+ ",手机号:" + order.getSendMobile() + ",快递员捎话:"
				+ order.getSendMobileMsg();
		
		jmsTemplate.send("bos_sms", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone", order.getCourier()
						.getTelephone());
				mapMessage.setString("msg", msg);
				return mapMessage;
			}
		});
		
		System.out.println("短信发送中---------");
		
		//修改取件状态为已通知
		workBill.setPickstate("已通知");

	}
	
	
	
	//根据订单编号查询订单================================================================
	@Override
	public Order findByOrderNum(String orderNum) {
		
		return orderRepository.findByOrderNum(orderNum);
	}

}
