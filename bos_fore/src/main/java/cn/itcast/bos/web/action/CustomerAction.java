package cn.itcast.bos.web.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.management.RuntimeErrorException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.utils.MailUtils;
import cn.itcast.bos.web.action.common.BaseAction;
import cn.itcast.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CustomerAction extends BaseAction<Customer> {
	
	//注入jmsTemplate
	@Autowired
	private JmsTemplate jmsTemplate;
	
	// 发送短信验证码
	@Action(value = "customer_sendMessage")
	public String sendMessage() throws IOException {
		// 生成短信验证码
		String randomCode = RandomStringUtils.randomNumeric(4);
		//System.out.println(randomCode);
		// 将短信验证码保存到session中
		// 将电话号码作为键
		ServletActionContext.getRequest().getSession()
				.setAttribute(model.getTelephone(), randomCode);

		// 编辑短信内容
		final String msg = "尊敬的用户,您好,本次获取的验证码为:" + randomCode + ",服务电话:13297976051";

		// 给用户发送短信
		// String result = SmsUtils.sendSmsByHTTP(model.getTelephone(),msg);
		//调用mq服务发送一条消息
		jmsTemplate.send("bos_sms",new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone",model.getTelephone());
				mapMessage.setString("msg",msg);
				return mapMessage;
			}
		});
		return null;
		
	
	}

	// 属性驱动获得验证码
	private String checkCode;

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	// 注入redisTemplate
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// 用户注册
	@Action(value = "customer_regist", results = {
			@Result(name = "success", type = "redirect", location = "signup-success.html"),
			@Result(name = "input", type = "redirect", location = "signup.html") })
	public String regist() {
		// 先获得输入的验证码
		// 获得在session中的验证码
		String checkCodeSession = (String) ServletActionContext.getRequest()
				.getSession().getAttribute(model.getTelephone());

		// 校验验证码
		if (checkCodeSession == null || !checkCodeSession.equals(checkCode)) {
			// 校验失败,
			System.out.println("验证码错误");
			return INPUT;
		}

		// 校验成功
		// 调用webService,连接crm_management
		WebClient
				.create("http://localhost:9090/crm_management/services/customerService/save")
				.type(MediaType.APPLICATION_JSON).post(model);

		System.out.println("注册成功");

		// 发送一封激活邮件
		// 生成激活码
		String activeCode = RandomStringUtils.randomNumeric(32);
		// 将激活码保存在redis中,设置为24小时有效
		redisTemplate.opsForValue().set(model.getTelephone(), activeCode, 24,
				TimeUnit.HOURS);

		// 发送邮件
		// 邮件内容
		String content = "尊敬的客户您好,请于24小时内完成邮箱的激活,点击下面地址完成激活:<br/><a href='"+MailUtils.activeUrl+"?telephone="+model.getTelephone()+"&activecode="+activeCode+"'>激活邮箱</a>";

		// 使用工具发送邮件
		MailUtils.sendMail("速运快递激活邮件", content, model.getEmail());
		return SUCCESS;
	}

	// 邮箱激活功能的完成--当客户收到邮件后,点击激活链接,就会发送请求--get请求
	// 通过属性驱动获得请求参数
	private String activecode;

	public void setActivecode(String activecode) {
		this.activecode = activecode;
	}
	
	
	//邮箱激活
	@Action(value = "customer_activeMail")
	public String activeMail() throws IOException {
		//解决中文乱码
		ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
		
		// 从redis中获得activecode
		String redisActiveCode = redisTemplate.opsForValue().get(
				model.getTelephone());

		if (redisActiveCode == null || !activecode.equals(redisActiveCode)) {
			// 激活码无效
			ServletActionContext.getResponse().getWriter()
					.write("激活码无效,请重新绑定邮箱");
		} else {
			// 激活码有效
			// 将用户邮箱状态设置为1
			// 根据手机号获得客户
			Customer customer = WebClient
					.create("http://localhost:9090/crm_management/services/customerService/findByTelphone/"+model.getTelephone())
					.accept(MediaType.APPLICATION_JSON).get(Customer.class);
			
			//判断
			if (customer.getType()==null || customer.getType()!=1) {
				//根据手机号将状态码修改为1--激活状态
				WebClient.create(
						"http://localhost:9090/crm_management/services/customerService/updatatype/"
								+ model.getTelephone()).get();
				ServletActionContext.getResponse().getWriter()
				.write("邮箱已激活");
				
			}else {
				ServletActionContext.getResponse().getWriter()
				.write("邮箱为激活状态,无需重新激活");
				
			}
			
			

		}
		
		//清除redis中的激活码
		redisTemplate.delete(model.getTelephone());
		
		return NONE;

	}

}
