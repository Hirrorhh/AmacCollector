package com.shjn.collector;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.shjn.model.Manager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ManagerCollector extends JsonCollector {

	private String url = "http://gs.amac.org.cn/amac-infodisc/api/pof/manager";

	// 私募基金管理人
	public void run() {
		// 取得数据
		int count = this.getData();
		logger.info("ManagerCollector总共插入" + count + "条数据");
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
				System.out.println("ManagerCollector:" + currentPage + "/" + totalPages);

				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject data = jsonArray.getJSONObject(i);

					try {
						// 基金管理人数据
						Manager m = this.getData(data);

						session.save(m);
						count += 1;
					} catch (Exception e) {
						logger.error("ManagerId:" + data.getLong("id"));
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

	private Manager getData(JSONObject data) {
		// 基金管理人数据
		Manager m = new Manager();

		m.setManagerId(data.getLong("id"));
		m.setManagerNameC(data.getString("managerName"));
		m.setArtificialPersonName(data.getString("artificialPersonName"));
		m.setPrimaryInvestType(data.getString("primaryInvestType"));
		m.setRegisterProvince(data.getString("registerProvince"));
		m.setRegisterNo(data.getString("registerNo"));
		try {
			m.setEstablishDate(new Date(data.getLong("establishDate")));
		} catch (Exception e) {
		}
		try {
			m.setRegisterDate(new Date(data.getLong("registerDate")));
		} catch (Exception e) {
		}
		m.setUrl(data.getString("url"));
		m.setFundScale(data.getDouble("fundScale"));
		m.setFundCount(data.getInt("fundCount"));

		return m;
	}

	@Override
	protected String createUrl(String baseUrl) {
		// 真实page参数从0开始
		int currentPage = this.page - 1;
		return baseUrl + "?rand=" + Math.random() + "&page=" + currentPage + "&size=" + this.size;
	}
}
