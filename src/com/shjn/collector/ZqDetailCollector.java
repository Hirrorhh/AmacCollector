package com.shjn.collector;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.shjn.model.Zq;
import com.shjn.model.ZqDetail;
import com.shjn.utils.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ZqDetailCollector extends JsonCollector {

	private String url = "http://ba.amac.org.cn/pages/amacWeb/user!search.action";
	private Long mpiId;

	// 证券公司私募产品详细
	public void run() {
		int count = 0;

		Configuration cfg = new Configuration();
		SessionFactory sf = cfg.configure().buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		// 遍历manager表获得详细页面url
		@SuppressWarnings({ "unchecked", "deprecation" })
		List<Zq> list = session.createQuery("from Zq where date_format(create_timestamp,'%Y-%m-%d')=date_format(now(),'%Y-%m-%d')").list();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("ZqDetail:" + (i + 1) + "/" + list.size());

			Zq zq = list.get(i);
			this.mpiId = zq.getMpiId();
			try {
				// 得到页面的JSON信息
				String result = this.getResponse(this.url);

				if (result != null) {
					// 解析数据
					JSONArray jsonArray = JSONArray.fromObject(result);

					// 证券公司私募产品
					ZqDetail zqd = this.getData(jsonArray.getJSONObject(0));

					session.save(zqd);
					count += 1;
				}
			} catch (Exception e) {
				logger.error("zq id:" + zq.getId() + " MpiId:" + this.mpiId);
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

		logger.info("ZqDetailCollector总共插入" + count + "条数据");
	}

	// 获得数据
	private ZqDetail getData(JSONObject data) throws Exception {

		// 证券公司私募产品详细
		ZqDetail zqd = new ZqDetail();

		zqd.setMpiId(this.mpiId);
		zqd.setProductCode(data.getString("CPBM"));
		zqd.setManagerName(data.getString("GLJG"));
		zqd.setCreateDate(Utils.String2Date(data.getString("SLRQ")));
		zqd.setExpiryDate(data.getString("DQR"));
		zqd.setInvestmentType(data.getString("TZLX"));
		String SFFJ = data.getString("SFFJ");
		if (SFFJ != null) {
			zqd.setClassification(SFFJ.equals("是"));
		}
		zqd.setManagement(data.getString("GLFS"));
		zqd.setEstablishScale(data.getDouble("CLGM"));
		zqd.setHouseholdsNumber(data.getInt("CLSCYHS"));
		zqd.setTrusteeship(data.getString("TGJG"));
		zqd.setSra(data.getString("FEDJJG"));

		return zqd;
	}

	@Override
	protected String createUrl(String baseUrl) {
		return baseUrl + "?filter_EQS_MPI_ID=" + this.mpiId
				+ "&sqlkey=publicity_web&sqlval=GET_PUBLICITY_WEB_BY_MPI_ID";
	}
}