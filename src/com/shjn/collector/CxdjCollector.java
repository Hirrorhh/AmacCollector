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

import com.shjn.model.Cxdj;
import com.shjn.utils.Utils;

public class CxdjCollector {

	private static Logger logger = Logger.getLogger(CxdjCollector.class);

	private String baseUrl = "http://www.amac.org.cn/xxgs/cxdj/";

	// 撤销管理人登记的名单
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
		logger.info("获取撤销详情网站: "+ link);
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

					Cxdj cxdj = new Cxdj();

					cxdj.setOrganization(e.get(1).text().trim());
					cxdj.setDisciplinary(e.get(2).text().trim());
					cxdj.setRevocationDate(Utils.String2Date(e.get(0).text().trim()));

					session.save(cxdj);
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