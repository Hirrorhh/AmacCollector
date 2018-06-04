package com.shjn.collector;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.shjn.model.Qh;
import com.shjn.model.QhDetail;
import com.shjn.utils.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QhDetailCollector extends JsonCollector {

	private String url = "http://ba.amac.org.cn/pages/amacWeb/user!search.action";
	private Long mpiId;

	// 期货资管产品数据
	public void run() {
		int count = 0;

		Configuration cfg = new Configuration();
		SessionFactory sf = cfg.configure().buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		// 遍历manager表获得详细页面url
		@SuppressWarnings({ "unchecked", "deprecation" })
		List<Qh> list = session.createQuery("from Qh where date_format(create_timestamp,'%Y-%m-%d')=date_format(now(),'%Y-%m-%d')").list();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("QhDetail:" + (i + 1) + "/" + list.size());

			Qh qh = list.get(i);
			this.mpiId = qh.getMpiId();
			try {
				// 得到页面的JSON信息
				String result = this.getResponse(this.url);

				if (result != null) {
					// 解析数据
					JSONArray jsonArray = JSONArray.fromObject(result);

					// 证券公司私募产品
					QhDetail qhd = this.getData(jsonArray.getJSONObject(0));

					session.save(qhd);
					count += 1;
				}
			} catch (Exception e) {
				logger.error("Qh id:" + qh.getId() + " MpiId:" + this.mpiId);
				logger.error(e.getMessage(), e);
			}

			if (i % 100 == 0) {
				session.flush();
				session.clear();
			}
		}

		tx.commit();
		session.close();
		sf.close();

		logger.info("QhDetailCollector总共插入" + count + "条数据");
	}

	// 获得数据
	private QhDetail getData(JSONObject data) throws Exception {
		// 期货资管产品数据
		QhDetail qhd = new QhDetail();

		qhd.setMpiId(this.mpiId);
		qhd.setMpiProductCode(data.getString("MPI_PRODUCT_CODE"));
		qhd.setAoiName(data.getString("AOI_NAME"));
		qhd.setTrusteeName(data.getString("MPI_TRUSTEE"));
		qhd.setMpiCreateDate(Utils.String2Date(data.getString("MPI_CREATE_DATE")));
		qhd.setInvestmentType(data.getString("TZLX"));
		qhd.setRaiseScale(data.getDouble("MPI_TOTAL_MONEY"));
		String SFJGH = data.getString("SFJGH");
		if (SFJGH != null) {
			qhd.setStructured(SFJGH.equals("是"));
		}
		qhd.setPrincipalsNumber(data.getInt("MPI_PARTICIPATION_USER"));

		return qhd;
	}

	@Override
	protected String createUrl(String baseUrl) {
		return baseUrl + "?filter_EQS_MPI_ID=" + this.mpiId + "&sqlkey=publicity_web&sqlval=GET_QH_WEB_BY_MPI_ID";
	}
}