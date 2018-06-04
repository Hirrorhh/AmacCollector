package com.shjn.collector;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ManagerDetailFundScaleCollector extends JsonCollector {

	private String url = "http://gs.amac.org.cn/amac-infodisc/res/pof/manager/index.html";

	// 私募基金管理人
	public void run() {
		int count = 0;

		// 定义各种分类规模
		String[] fundType = { "私募证券自主发行", "私募证券顾问管理", "私募股权", "创业投资", "其它私募" };
		String[][] fundScale = { //
				{ "", "500000", "200000", "100000", "10000", "0" }, // 私募证券自主发行
				{ "", "500000", "200000", "100000", "10000", "0" }, // 私募证券顾问管理
				{ "", "1000000", "500000", "200000", "0" }, // 私募股权
				{ "", "100000", "50000", "20000", "0" }, // 创业投资
				{ "", "100000", "50000", "20000", "0" } // 其它私募
		};

		// 循环取得分类
		for (int i = 0; i < fundType.length; i++) {
			// 循环取得规模
			for (int j = 1; j < fundScale.length; j++) {
				this.page = 1;
				// 取得数据
				count += this.getData(fundType[i], fundScale[i][j - 1], fundScale[i][j], i + 1, j);
			}
		}

		logger.info("ManagerDetailFundScaleCollector更新" + count + "条数据");
	}

	// 获得数据
	private int getData(String fundType, String to, String from, int number, int scale) {
		if (!to.equals("")) {
			to = "\"to\":\"" + to + "\",";
		}
		this.csq = "{\"fundScale\":{" + to + "\"from\":\"" + from + "\"},\"fundType\":\"" + fundType + "\"}";

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
				System.out.println("ManagerDetailFundScaleCollector:" + currentPage + "/" + totalPages);

				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject data = jsonArray.getJSONObject(i);

					try {
						String hql = "update ManagerDetail set fundScale" + number + " = " + scale
								+ " where managerId = " + data.getLong("id");
						session.createQuery(hql).executeUpdate();

						count += 1;
					} catch (Exception e) {
						logger.error("ManagerDetailId:" + data.getLong("id"));
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

	@Override
	protected String createUrl(String baseUrl) {
		// 真实page参数从0开始
		int currentPage = this.page - 1;
		return baseUrl + "?rand=" + Math.random() + "&page=" + currentPage + "&size=" + this.size;
	}
}
