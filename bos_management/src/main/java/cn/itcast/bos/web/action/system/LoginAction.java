package cn.itcast.bos.web.action.system;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class LoginAction extends BaseAction<User> {

	// 用户登陆
	@Action(value = "user_login", results = {
			@Result(name = "success", type = "redirect", location = "/index.html"),
			@Result(name = "login", type = "redirect", location = "/login.html") })
	public String login() {
		System.out.println(model.getUsername());
		System.out.println(model.getPassword());
		// 用户名和密码封装在model中
		// 基于shiro实现登陆
		Subject subject = SecurityUtils.getSubject();// 相当于用户
		// 将用户名和密码封装在token中--->相当于令牌
		UsernamePasswordToken token = new UsernamePasswordToken(
				model.getUsername(), model.getPassword());

		try {
			subject.login(token);// subject会去找applicationContext中的sucurityManager;接着sucurityManager会去找realm

			// 登陆成功
			System.out.println("登陆成功");
			return SUCCESS;
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return LOGIN;
		}

	}
	
	
	//用户退出
	@Action(value="user_logout",results={@Result(name="success",type="redirect",location="/login.html")})
	public String logout(){
		//获得subject
		Subject subject = SecurityUtils.getSubject();
		//退出登录
		subject.logout();
		
		return SUCCESS;
		
	}
}
