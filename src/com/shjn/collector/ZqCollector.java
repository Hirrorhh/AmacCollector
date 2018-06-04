package com.shjn.collector;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.shjn.model.Zq;
import com.shjn.utils.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ZqCollector extends JsonCollector {

	private String url = "http://ba.amac.org.cn/pages/amacWeb/user!list.action";

	// 证券公司私募产品数据
	public void run() {
		// 取得数据
		int count = this.getData();
		logger.info("ZqCollector总共插入" + count + "条数据");
	}

	// 获得数据
	private int getData() {
		boolean hasNext = false;
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
					hasNext = jsonObject.getBoolean("hasNext");
					jsonArray = jsonObject.getJSONArray("result");
				} catch (Exception e) {
					logger.error(result);
					logger.error(e.getMessage(), e);
				}

				int totalPages = jsonObject.getInt("totalPages");
				System.out.println("ZqCollector:" + (this.page - 1) + "/" + totalPages);
				count += jsonArray.size();

				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject data = jsonArray.getJSONObject(i);

					try {
						// 证券公司私募产品
						Zq zq = this.getData(data);

						session.save(zq);
					} catch (Exception e) {
						logger.error("Zq MpiId:" + data.getLong("MPI_ID"));
						logger.error(e.getMessage(), e);
					}
				}

				session.flush();
				session.clear();
			}
		} while (hasNext);

		tx.commit();
		session.close();
		sf.close();

		return count;
	}

	// 获得数据
	private Zq getData(JSONObject data) throws Exception {
		// 证券公司私募产品
		Zq zq = new Zq();

		zq.setMpiId(data.getLong("MPI_ID"));
		zq.setProductCode(data.getString("CPBM"));
		zq.setProductName(data.getString("CPMC"));
		zq.setManagerName(data.getString("GLJG"));
		zq.setCreateDate(Utils.String2Date(data.getString("SLRQ")));
		zq.setContainClassification(zq.getProductName().contains("分级"));
		zq.setContainStructured(zq.getProductName().contains("结构化"));

		return zq;
	}

	@Override
	protected String createUrl(String baseUrl) {
		return baseUrl + "?page.pageNo=" + this.page + "&page.pageSize=" + this.size
				+ "&filter_LIKES_CPMC=&filter_LIKES_GLJG=&filter_LIKES_CPBM=&filter_GES_SLRQ=&filter_LES_SLRQ=&page.searchFileName=publicity_web&page.sqlKey=PAGE_PUBLICITY_WEB&page.sqlCKey=SIZE_PUBLICITY_WEB&_search=false&nd=&page.orderBy=SLRQ&page.order=desc";
	}
}
