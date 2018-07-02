package cn.itcast.bos.web.action.common;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public abstract class BaseAction<T>  extends ActionSupport implements ModelDriven<T>{
	//模型驱动
	protected T model;

	@Override
	public T getModel() {
		
		return model;
	}
	
	//构造器完成model的实例化
	public BaseAction() {
		//构造子类action,获取继承父类的泛型
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
		Class<T> modelClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
		
		try {
			model = modelClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} 
	}
	

	//属性驱动获得页面传入的参数
	protected int page;//页数
		
	protected int rows;//每页条数
		
		public void setPage(int page) {
			this.page = page;
		}

		public void setRows(int rows) {
			this.rows = rows;
		}
	
	
	public void pushToValueStack(Page<T> pagedata){
		//定义一个Map集合
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("total", pagedata.getNumberOfElements());
		map.put("rows", pagedata.getContent());
		
		//将数据存入valueStack
		ActionContext.getContext().getValueStack().push(map);
	}
	
	

}
