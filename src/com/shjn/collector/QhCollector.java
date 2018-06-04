package com.shjn.collector;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.shjn.model.Qh;
import com.shjn.utils.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QhCollector extends JsonCollector {

	private String url = "http://ba.amac.org.cn/pages/amacWeb/user!list.action";

	// 期货资管产品
	public void run() {
		// 取得数据
		int count = this.getData();
		logger.info("QhCollector总共插入" + count + "条数据");
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
				System.out.println("QhCollector:" + (this.page - 1) + "/" + totalPages);
				count += jsonArray.size();

				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject data = jsonArray.getJSONObject(i);

					try {
						// 期货资管产品
						Qh qh = this.getData(data);

						session.save(qh);
					} catch (Exception e) {
						logger.error("Qh MpiId:" + data.getLong("MPI_ID"));
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
	private Qh getData(JSONObject data) throws Exception {
		// 期货资管产品
		Qh qh = new Qh();

		qh.setMpiId(data.getLong("MPI_ID"));
		qh.setMpiProductCode(data.getString("MPI_PRODUCT_CODE"));
		qh.setMpiName(data.getString("MPI_NAME"));
		qh.setAoiName(data.getString("AOI_NAME"));
		qh.setMpiCreateDate(Utils.String2Date(data.getString("MPI_CREATE_DATE")));
		qh.setContainClassification(qh.getMpiName().contains("分级"));
		qh.setContainStructured(qh.getMpiName().contains("结构化"));

		return qh;
	}

	@Override
	protected String createUrl(String baseUrl) {
		return baseUrl + "?page.pageNo=" + this.page + "&page.pageSize=" + this.size
				+ "&filter_LIKES_MPI_NAME=&filter_LIKES_AOI_NAME=&filter_LIKES_MPI_PRODUCT_CODE=&filter_GES_MPI_CREATE_DATE=&filter_LES_MPI_CREATE_DATE=&page.searchFileName=publicity_web&page.sqlKey=PAGE_QH_PUBLICITY_WEB&page.sqlCKey=SIZE_QH_PUBLICITY_WEB&_search=false&nd=&page.orderBy=MPI_CREATE_DATE&page.order=desc";
	}

}
