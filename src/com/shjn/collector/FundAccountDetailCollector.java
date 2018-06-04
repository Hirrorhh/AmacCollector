package com.shjn.collector;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.shjn.model.FundAccount;
import com.shjn.model.FundAccountDetail;

public class FundAccountDetailCollector extends PageCollector {

	private String baseUrl = "http://gs.amac.org.cn/amac-infodisc/res/fund/account/";

	// 基金专户产品详细
	public void run() {
		int count = 0;

		Configuration cfg = new Configuration();
		SessionFactory sf = cfg.configure().buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		// 遍历fund_account表获得详细页面url
		@SuppressWarnings({ "unchecked", "deprecation" })
		List<FundAccount> list = session.createQuery("from FundAccount where date_format(create_timestamp,'%Y-%m-%d')=date_format(now(),'%Y-%m-%d')").list();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("FundAccountDetail:" + (i + 1) + "/" + list.size());

			FundAccount fa = list.get(i);
			try {
				if (fa.getUrl() != null) {
					// 得到网页源代码
					String result = this.getResponse(baseUrl + fa.getUrl());

					// 从源代码中取得数据
					FundAccountDetail fad = this.getData(result);
					// 部分数据从fund_account表中获取
					fad.setFundAccountId(fa.getFundAccountId());
					fad.setRegisterCode(fa.getRegisterCode());
					fad.setManager(fa.getManager());
					fad.setRegisterDate(fa.getRegisterDate());

					session.save(fad);
					count += 1;
				}
			} catch (Exception e) {
				logger.error("FundAccount id:" + fa.getId() + " FundAccountId:" + fa.getFundAccountId());
				logger.error(e.getMessage(), e);
			}

			if (i % 50 == 0) {
				session.flush();
				session.clear();
			}
		}

		tx.commit();
		session.close();
		sf.close();

		logger.info("FundAccountDetailCollector总共插入" + count + "条数据");
	}

	// 获得数据
	private FundAccountDetail getData(String result) throws Exception {
		// 基金专户产品详细
		FundAccountDetail fad = new FundAccountDetail();

		fad.setTrusteeName(this.collectString("托管人名称", result));
		fad.setContractPeriod(this.collectString("合同期限\\（月\\）", result));
		String initialScale = this.collectString("起始规模\\（亿元\\）", result);
		if (!"".equals(initialScale) && initialScale != null) {
			fad.setInitialScale(Double.valueOf(initialScale));
		}
		String classification = this.collectString("是否分级", result);
		fad.setClassification(classification != null && classification.equals("是"));
		String investorsNumber = this.collectString("成立时投资者数量", result);
		if (investorsNumber != null) {
			fad.setInvestorsNumber(Integer.valueOf(investorsNumber));
		}
		fad.setOtherProductType(this.collectString("非专项资产管理计划产品类型", result));

		return fad;
	}
}