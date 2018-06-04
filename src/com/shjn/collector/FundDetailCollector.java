package com.shjn.collector;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.shjn.model.Fund;
import com.shjn.model.FundDetail;
import com.shjn.utils.Utils;

public class FundDetailCollector extends PageCollector {

	private String baseUrl = "http://gs.amac.org.cn/amac-infodisc/res/pof/fund/";

	// 私募基金数据详细
	public void run() {
		int count = 0;

		Configuration cfg = new Configuration();
		SessionFactory sf = cfg.configure().buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		// 遍历fund表获得详细页面url
		@SuppressWarnings({ "unchecked", "deprecation" })
		List<Fund> list = session.createQuery("from Fund where date_format(create_timestamp,'%Y-%m-%d')=date_format(now(),'%Y-%m-%d')").list();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("FundDetail:" + (i + 1) + "/" + list.size());

			Fund f = list.get(i);
			try {
				// 得到网页源代码
				String result = this.getResponse(baseUrl + f.getUrl());

				if (result != null) {
					// 从源代码中取得数据
					FundDetail fd = this.getData(result);
					// 部分数据从fund表中获取
					fd.setFundId(f.getFundId());
					fd.setEstablishDate(f.getEstablishDate());
					fd.setPutOnRecordDate(f.getPutOnRecordDate());

					session.save(fd);
					count += 1;
				}
			} catch (Exception e) {
				logger.error("Fund id:" + f.getId() + " FundId:" + f.getFundId());
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

		logger.info("FundDetailCollector总共插入" + count + "条数据");
	}

	// 获得数据
	private FundDetail getData(String result) throws Exception {

		// 私募基金数据详细
		FundDetail fd = new FundDetail();

		fd.setFundNo(this.collectString("基金编号", result));
		String putOnRecordPhase = this.collectString("基金备案阶段", result);
		fd.setPutOnRecordPhase(putOnRecordPhase != null && putOnRecordPhase.equals("暂行办法实施后成立的基金"));
		fd.setFundType(this.collectString("基金类型", result));
		fd.setCurrency(this.collectString("币种", result));
		fd.setManagerType(this.setManagerType(this.collectString("管理类型", result)));
		fd.setTrusteeName(this.collectString("托管人名称", result));
		fd.setMainInvestment(this.collectString("主要投资领域", result));
		fd.setWorkingState(this.setWorkingState(this.collectString("运作状态", result)));
		fd.setLastUpdated(Utils.String2Date(this.collectString("基金信息最后更新时间", result)));
		fd.setSpecialNote(this.collectString("基金协会特别提示\\（针对基金\\）", result));
		if (result.contains("信息披露情况")) {
			fd.setInformationDisclosure(
					"月报:" + this.collectString("月报", result) + "半年报:" + this.collectString("半年报", result) + "年报:"
							+ this.collectString("年报", result) + "季报:" + this.collectString("季报", result));
		}

		return fd;
	}

	private int setManagerType(String s) {
		if (s != null) {
			switch (s) {
				case "受托管理":
					return 1;
				case "自我管理":
					return 2;
				case "顾问管理":
					return 3;
			}
		}

		// 无管理类型
		return 0;
	}

	private int setWorkingState(String s) {
		if (s != null) {
			switch (s) {
				case "正在运作":
					return 1;
				case "延期清盘":
					return 2;
				case "提前清盘":
					return 3;
				case "正常清盘":
					return 4;
				case "非正常清盘":
					return 5;
			}
		}

		// 无工作状态
		return 0;
	}
}