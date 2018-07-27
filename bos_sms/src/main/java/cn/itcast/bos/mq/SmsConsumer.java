package cn.itcast.bos.mq;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.stereotype.Service;

@Service("smsConsumer")
public class SmsConsumer implements MessageListener{

	@Override
	public void onMessage(Message message) {
		MapMessage mapMessage = (MapMessage) message;
		
		try {
			// 给用户发送短信
			// String result = SmsUtils.sendSmsByHTTP(mapMessage.getString("telephone"),mapMessage.getString("msg"));
			
			String result = "000xxx";
			
			if (result.startsWith("000")) {
				//短信发送成功
			System.out.println("发送短信成功,手机号为:"+mapMessage.getString("telephone")+"信息为:"+mapMessage.getString("msg"));
				
			}else{
				throw new RuntimeException("短信发送失败:信息码为:"+result);
				
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}


		
	}

}
