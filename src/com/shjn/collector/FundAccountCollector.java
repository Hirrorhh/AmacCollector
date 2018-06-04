package com.shjn.collector;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.shjn.model.FundAccount;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FundAccountCollector extends JsonCollector {

	private String url = "http://gs.amac.org.cn/amac-infodisc/api/fund/account";

	// 基金专户产品数据
	public void run() {
		// 取得数据
		int count = this.getData();
		logger.info("FundAccountCollector总共插入" + count + "条数据");
	}

	// 获得数据
	private int getData() {
		boolean last = false;
		int count = 0;

		Configuration cfg = new Configuration();
		SessionFactory sf = cfg.configure().buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		do {
			// 得到页面的JSON信息
			String result = this.getResponse(this.url);

			if (result != null) {
				// 解析数据
				JSONObject jsonObject = null;
				JSONArray jsonArray = null;
				try {
					jsonObject = JSONObject.fromObject(result);
					last = jsonObject.getBoolean("last");
					jsonArray = jsonObject.getJSONArray("content");
				} catch (Exception e) {
					logger.error(result);
					logger.error(e.getMessage(), e);
				}

				// 真实page参数从0开始
				int currentPage = this.page - 1;
				int totalPages = jsonObject.getInt("totalPages");
				System.out.println("FundAccountCollector:" + currentPage + "/" + totalPages);

				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject data = jsonArray.getJSONObject(i);

					try {
						// 基金专户产品
						FundAccount fa = this.getData(data);

						session.save(fa);
						count += 1;
					} catch (Exception e) {
						logger.error("FundAccountId:" + data.getLong("id"));
						logger.error(e.getMessage(), e);
					}
				}

				session.flush();
				session.clear();
			}
		} while (!last);

		tx.commit();
		session.close();
		sf.close();

		return count;
	}

	private FundAccount getData(JSONObject data) {
		// 基金专户产品
		FundAccount fa = new FundAccount();

		fa.setFundAccountId(data.getString("id"));
		fa.setName(data.getString("name"));
		fa.setManager(data.getString("manager"));
		fa.setType(data.getString("type"));
		fa.setRegisterCode(data.getString("registerCode"));
		try {
			fa.setRegisterDate(new Date(data.getLong("registerDate")));
		} catch (Exception e) {
		}
		String type = fa.getType();
		if (type != null && type.equals("一对多")) {
			fa.setUrl(fa.getFundAccountId() + ".html");
		}

		return fa;
	}

	@Override
	protected String createUrl(String baseUrl) {
		// 真实page参数从0开始
		int currentPage = this.page - 1;
		return baseUrl + "?rand=" + Math.random() + "&page=" + currentPage + "&size=" + this.size;
	}
}
