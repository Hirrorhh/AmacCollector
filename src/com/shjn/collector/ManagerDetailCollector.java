package com.shjn.collector;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.shjn.model.Manager;
import com.shjn.model.ManagerDetail;
import com.shjn.utils.Utils;

public class ManagerDetailCollector extends PageCollector {

	private String baseUrl = "http://gs.amac.org.cn/amac-infodisc/res/pof/manager/";

	// 私募基金管理人详细
	public void run() {
		int count = 0;

		Configuration cfg = new Configuration();
		SessionFactory sf = cfg.configure().buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();

		// 遍历manager表获得详细页面url
		@SuppressWarnings({ "unchecked", "deprecation" })
		List<Manager> list = session
				.createQuery(
						"from Manager where date_format(create_timestamp,'%Y-%m-%d')=date_format(now(),'%Y-%m-%d')")
				.list();
		for (int i = 0; i < list.size(); i++) {
			System.out.println("ManagerDetail:" + (i + 1) + "/" + list.size());

			Manager m = list.get(i);
			try {
				// 得到网页源代码
				String result = this.getResponse(baseUrl + m.getUrl());

				if (result != null) {
					// 从源代码中取得数据
					ManagerDetail md = this.getData(result);
					// 部分数据从manager表中获取
					md.setManagerId(m.getManagerId());
					md.setManagerNameC(m.getManagerNameC());
					md.setManageScalaZero(m.getFundScale() == 0);
					md.setOneYearManageScalaZero(
							md.getManageScalaZero() && isOneYear(m.getEstablishDate(), new Date()));

					session.save(md);
					count += 1;
				}
			} catch (Exception e) {
				logger.error("Manager id:" + m.getId() + " ManagerId:" + m.getManagerId());
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

		logger.info("ManagerDetailCollector总共插入" + count + "条数据");
	}

	// 获得数据
	private ManagerDetail getData(String result) throws Exception {
		// 私募基金管理人详细
		ManagerDetail md = new ManagerDetail();

		md.setManagerNameE(this.collectString("基金管理人全称\\(英文\\)", result));
		md.setOrgCode(this.collectString("组织机构代码", result));
		md.setRegisterAddress(this.collectString("注册地址", result));
		md.setOfficeAddress(this.collectString("办公地址", result));
		md.setRegisterCapital(String2Double(this.collectString("注册资本\\(万元\\)\\(人民币\\)", result)));
		md.setPaidCapital(String2Double(this.collectString("实缴资本\\(万元\\)\\(人民币\\)", result)));
		md.setRegisterCapitalUsd(String2Double(this.collectString("注册资本\\(万元\\)\\(美元\\)", result)));
		md.setPaidCapitalUsd(String2Double(this.collectString("实缴资本\\(万元\\)\\(美元\\)", result)));
		md.setPaidCapitalRatio(String2Double(this.collectString("注册资本实缴比例", result)));
		md.setEnterpriseNature(this.collectString("管理基金主要类别", result));
		md.setPrimaryInvestType(this.collectString("管理基金主要类别", result));
		md.setOtherBusinessApplications(this.collectString("申请的其他业务类型", result));
		md.setEmployeesNumber(String2Int(this.collectString("员工人数", result)));
		md.setOrgUrl(this.collectString("机构网址", result));
		String member = this.collectString("是否为会员", result);
		md.setMember(member != null && member.equals("是"));
		md.setMemberType(this.collectString("当前会员类型", result));
		md.setJoinTime(Utils.String2Date(this.collectString("入会时间", result)));
		md.setLegalOpinion(this.collectString("法律意见书状态", result));
		md.setLawOffice(this.collectString("律师事务所名称", result));
		md.setLawyer(this.collectString("律师姓名", result));
		md.setHandInUnderRegister25p(md.getPaidCapitalRatio() != null && md.getPaidCapitalRatio() < 25);
		md.setHandInLe100w(md.getPaidCapital() != null && md.getPaidCapital() < 100);
		md.setFaithInfo1(result.contains("确认该机构处于失联(异常)状态"));
		md.setFaithInfo2(result.contains("异常原因："));
		md.setFaithInfo3(result.contains("虚假填报："));
		md.setFaithInfo4(result.contains("重大遗漏："));
		md.setFaithInfo5(result.contains("违反八条底线内容："));
		md.setFaithInfo6(result.contains("相关主体存在的不良诚信记录："));
		md.setNoQualifications(result.contains("namesStr.push"));
		this.setManager(result, md);

		return md;
	}

	private void setManager(String result, ManagerDetail md) {
		result = this.collectManagerString("高管情况", result);
		Document doc = Jsoup.parse(result);
		Elements trs = doc.select("table").select("tr");
		for (int i = 1; i < trs.size(); i++) {
			Elements tds = trs.get(i).select("td");
			String name = "";
			for (int j = 0; j < tds.size(); j++) {
				String text = tds.get(j).text();
				switch (j) {
					case 0:
						name = text;
					case 1:
						if (text.contains("法定代表人")) {
							if (md.getLegalRepresentative() == null)
								md.setLegalRepresentative(name);
							else
								md.setLegalRepresentative(md.getLegalRepresentative() + " " + name);
						}
						if (text.contains("总经理")) {
							if (md.getGeneralManager() == null)
								md.setGeneralManager(name);
							else
								md.setGeneralManager(md.getGeneralManager() + " " + name);
						}
						if (text.contains("合规风控")) {
							if (md.getRiskControlManager() == null)
								md.setRiskControlManager(name);
							else
								md.setRiskControlManager(md.getRiskControlManager() + " " + name);
						}
				}
			}
		}
	}
}