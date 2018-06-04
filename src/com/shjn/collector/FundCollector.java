package com.shjn.collector;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.shjn.model.Fund;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class FundCollector extends JsonCollector {

	private String url = "http://gs.amac.org.cn/amac-infodisc/api/pof/fund";

	// 私募基金数据
	public void run() {
		// 取得数据
		int count = this.getData();
		logger.info("FundCollector总共插入" + count + "条数据");
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
				System.out.println("FundCollector:" + currentPage + "/" + totalPages);
				count += jsonArray.size();

				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject data = jsonArray.getJSONObject(i);

					try {
						// 私募基金数据
						Fund f = this.getData(data);

						session.save(f);
					} catch (Exception e) {
						logger.error("FundId:" + data.getLong("id"));
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

	private Fund getData(JSONObject data) {
		// 私募基金数据
		Fund f = new Fund();

		f.setFundId(data.getLong("id"));
		f.setFundName(data.getString("fundName"));
		f.setManagerName(data.getString("managerName"));
		try {
			f.setEstablishDate(new Date(data.getLong("establishDate")));
		} catch (Exception e) {
		}
		try {
			f.setPutOnRecordDate(new Date(data.getLong("putOnRecordDate")));
		} catch (Exception e) {
		}
		f.setUrl(data.getString("url"));
		f.setLastQuarterUpdate(data.getBoolean("lastQuarterUpdate"));
		f.setContainClassification(f.getFundName().contains("分级"));
		f.setContainStructured(f.getFundName().contains("结构化"));

		return f;
	}

	@Override
	protected String createUrl(String baseUrl) {
		// 真实page参数从0开始
		int currentPage = this.page - 1;
		return baseUrl + "?rand=" + Math.random() + "&page=" + currentPage + "&size=" + this.size;
	}
}
