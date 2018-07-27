package cn.itcast.bos.service.base.impl;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.BooleanQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.base.WayBillRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.base.IWayBillService;

@Service
@Transactional
public class WayBillServiceImpl implements IWayBillService {

	// 注入dao
	@Autowired
	private WayBillRepository wayBillRepository;

	// 注入
	@Autowired
	private WayBillIndexRepository wayBillIndexRepository;

	@Override
	public void save(WayBill wayBill) {

		// 根据运单编号查询运单
		// 持久化的
		WayBill wb = wayBillRepository
				.findByWayBillNum(wayBill.getWayBillNum());
		if (wb == null) {
			// 不存在
			//将运单状态设置为1,待配送中
			wayBill.setSignStatus(1);
			wayBillRepository.save(wayBill);
			// 存到索引库
			wayBillIndexRepository.save(wayBill);
		} else {

			try {
				// 存在,获得运单id
				Integer id = wb.getId();
				
				//判断
				if (wayBill.getSignStatus()==1) {
					
					BeanUtils.copyProperties(wb, wayBill);
					wb.setId(id);// 持久化对象,自动更新数据库

					// 存到索引库
					wayBillIndexRepository.save(wb);
				}else {
					
					throw new RuntimeException("运单配送中,无法修改");
				}
			

			} catch (Exception e) {
				e.printStackTrace();

			}
		}

	}

	@Override
	public WayBill findByWayBillNum(String wayBillNum) {

		return wayBillRepository.findByWayBillNum(wayBillNum);
	}

	@Override
	public Page<WayBill> pageQuery(WayBill waybill, Pageable pageRequest) {
		// 判断,没有条件查询
		if (StringUtils.isBlank(waybill.getWayBillNum())
				&& StringUtils.isBlank(waybill.getSendAddress())
				&& StringUtils.isBlank(waybill.getRecAddress())
				&& StringUtils.isBlank(waybill.getSendProNum())
				&& (waybill.getSignStatus() == null || waybill.getSignStatus() == 0)) {
			// 查询所有
			Page<WayBill> pagedata = wayBillRepository.findAll(pageRequest);
			return pagedata;
		} else {
			// 有条件查询
			// 定义一个布尔查询,构造多条件组合查询
			BoolQueryBuilder query = new BoolQueryBuilder();
			// 向组合查询里面添加条件
			// 运单号
			if (StringUtils.isNotBlank(waybill.getWayBillNum())) {
				// termqueryBuild等值查询
				TermQueryBuilder builder = new TermQueryBuilder("wayBillNum",
						waybill.getWayBillNum());
				query.must(builder);
			}

			// 发件地址
			if (StringUtils.isNotBlank(waybill.getSendAddress())) {
				// 模糊查询
				// 第一种情况,输入北或北京--这些词在索影库有分词对应
				WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder(
						"sendAddress", "*" + waybill.getSendAddress() + "*");

				// 第二种情况,输入北京市朝阳区--在索引库中没有对应的分词,需要将搜索条件也进行分词
				QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(
						waybill.getSendAddress()).field("sendAddress")
						.defaultOperator(Operator.AND);

				// 将两种条件进行组合
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

				// should表示or
				boolQueryBuilder.should(wildcardQueryBuilder);
				boolQueryBuilder.should(queryStringQueryBuilder);

				// must表示&
				query.must(boolQueryBuilder);

			}

			// 发件地址
			if (StringUtils.isNotBlank(waybill.getRecAddress())) {

				// 第一种情况-模糊查询--查询条件不用分词
				WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder(
						"recAddress", "*" + waybill.getRecAddress() + "*");

				// 第二种情况--查询条件要进行分词
				QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(
						waybill.getRecAddress()).field("recAddress")
						.defaultOperator(Operator.AND);

				// 两种条件进行组合or
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
				boolQueryBuilder.should(wildcardQueryBuilder);
				boolQueryBuilder.should(queryStringQueryBuilder);

				// 再添加到多搜索条件中
				query.must(boolQueryBuilder);

			}

			// 产品快递类型
			if (StringUtils.isNotBlank(waybill.getSendProNum())) {
				// 等值查询
				// termqueryBuild等值查询
				TermQueryBuilder builder = new TermQueryBuilder("sendProNum",
						waybill.getSendProNum());
				query.must(builder);
			}

			if (waybill.getSignStatus() != null && waybill.getSignStatus() != 0) {
				// 等值查询
				TermQueryBuilder builder = new TermQueryBuilder("signStatus",
						waybill.getSignStatus());
				query.must(builder);
			}

			// 查询数据--参数为多条件查询组合
			NativeSearchQuery searchQuery = new NativeSearchQuery(query);

			// 分页效果
			searchQuery.setPageable(pageRequest);

			// 调用search方法
			Page<WayBill> pageData = wayBillIndexRepository.search(searchQuery);

			return pageData;

		}

	}
	
	//将索引库和数据库同步
	public void getNew(){
		wayBillIndexRepository.deleteAll();
		List<WayBill> waybills = wayBillRepository.findAll();
		wayBillIndexRepository.save(waybills);
		
	}

	@Override
	public List<WayBill> findWayBill(WayBill waybill) {
		// 判断,没有条件查询
				if (StringUtils.isBlank(waybill.getWayBillNum())
						&& StringUtils.isBlank(waybill.getSendAddress())
						&& StringUtils.isBlank(waybill.getRecAddress())
						&& StringUtils.isBlank(waybill.getSendProNum())
						&& (waybill.getSignStatus() == null || waybill.getSignStatus() == 0)) {
					// 查询所有
				 List<WayBill> wayBills = wayBillRepository.findAll();
					return wayBills;
				} else {
					// 有条件查询
					// 定义一个布尔查询,构造多条件组合查询
					BoolQueryBuilder query = new BoolQueryBuilder();
					// 向组合查询里面添加条件
					// 运单号
					if (StringUtils.isNotBlank(waybill.getWayBillNum())) {
						// termqueryBuild等值查询
						TermQueryBuilder builder = new TermQueryBuilder("wayBillNum",
								waybill.getWayBillNum());
						query.must(builder);
					}

					// 发件地址
					if (StringUtils.isNotBlank(waybill.getSendAddress())) {
						// 模糊查询
						// 第一种情况,输入北或北京--这些词在索影库有分词对应
						WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder(
								"sendAddress", "*" + waybill.getSendAddress() + "*");

						// 第二种情况,输入北京市朝阳区--在索引库中没有对应的分词,需要将搜索条件也进行分词
						QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(
								waybill.getSendAddress()).field("sendAddress")
								.defaultOperator(Operator.AND);

						// 将两种条件进行组合
						BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

						// should表示or
						boolQueryBuilder.should(wildcardQueryBuilder);
						boolQueryBuilder.should(queryStringQueryBuilder);

						// must表示&
						query.must(boolQueryBuilder);

					}

					// 发件地址
					if (StringUtils.isNotBlank(waybill.getRecAddress())) {

						// 第一种情况-模糊查询--查询条件不用分词
						WildcardQueryBuilder wildcardQueryBuilder = new WildcardQueryBuilder(
								"recAddress", "*" + waybill.getRecAddress() + "*");

						// 第二种情况--查询条件要进行分词
						QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(
								waybill.getRecAddress()).field("recAddress")
								.defaultOperator(Operator.AND);

						// 两种条件进行组合or
						BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
						boolQueryBuilder.should(wildcardQueryBuilder);
						boolQueryBuilder.should(queryStringQueryBuilder);

						// 再添加到多搜索条件中
						query.must(boolQueryBuilder);

					}

					// 产品快递类型
					if (StringUtils.isNotBlank(waybill.getSendProNum())) {
						// 等值查询
						// termqueryBuild等值查询
						TermQueryBuilder builder = new TermQueryBuilder("sendProNum",
								waybill.getSendProNum());
						query.must(builder);
					}

					if (waybill.getSignStatus() != null && waybill.getSignStatus() != 0) {
						// 等值查询
						TermQueryBuilder builder = new TermQueryBuilder("signStatus",
								waybill.getSignStatus());
						query.must(builder);
					}

					// 查询数据--参数为多条件查询组合
					NativeSearchQuery searchQuery = new NativeSearchQuery(query);

					// searchQuery有默认查询条数,如果要查询所有,则需要进行设置
					Pageable pageable = new PageRequest(0,10000); 
					
					searchQuery.setPageable(pageable);

					// 调用search方法
					Page<WayBill> pageData = wayBillIndexRepository.search(searchQuery);

					return pageData.getContent();

				}
		
	}
	
	

}
