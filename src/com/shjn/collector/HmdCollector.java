package com.shjn.collector;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.shjn.model.Hmd;
import com.shjn.utils.Utils;

public class HmdCollector {

	private static Logger logger = Logger.getLogger(HmdCollector.class);

	private String baseUrl = "http://www.amac.org.cn/xxgs/hmd/";

	// 私募基金管理人从业黑名单
	public void run() {
		// 得到网页源代码
		Document doc = null;
		try {
			doc = Jsoup.connect(baseUrl).get();
		} catch (IOException e) {
			logger.error(baseUrl);
			logger.error(e.getMessage(), e);
		}

		if (doc != null) {
			// 解析详情页链接
			Elements es = doc.select("div.newsName").select("a");
			for (Element link : es) {
				// 获取详情页信息
				this.getData(link.attr("abs:href"));
			}
		}
	}

	// 获取详情页信息
	private void getData(String link) {

		Configuration cfg = new Configuration();
		SessionFactory sf = cfg.configure().buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		Document doc = null;
		try {
			doc = Jsoup.connect(link).get();
		} catch (IOException e) {
			logger.error(link);
			logger.error(e.getMessage(), e);
		}

		if (doc != null) {
			Elements es = doc.select("tbody").select("tr");
			for (int i = 1; i < es.size(); i++) {
				try {
					Elements e = es.get(i).select("td");

					Hmd hmd = new Hmd();

					int size = e.size();
					if (size == 4) {
						hmd.setName(e.get(1).text().trim());
					}
					hmd.setOrganization(e.get(size - 2).text().trim());
					hmd.setDisciplinary(e.get(size - 1).text().trim());
					hmd.setRevocationTime(Utils.String2Date(e.get(0).text().trim()));

					session.save(hmd);
				} catch (Exception e) {
					logger.error(link);
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
		}
	}
}