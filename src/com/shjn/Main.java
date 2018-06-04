package com.shjn;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.shjn.collector.CxdjCollector;
import com.shjn.collector.FundAccountCollector;
import com.shjn.collector.FundAccountDetailCollector;
import com.shjn.collector.FundCollector;
import com.shjn.collector.FundDetailCollector;
import com.shjn.collector.HmdCollector;
import com.shjn.collector.ManagerCollector;
import com.shjn.collector.ManagerDetailCollector;
import com.shjn.collector.ManagerDetailFundScaleCollector;
import com.shjn.collector.PageCollector;
import com.shjn.collector.QhCollector;
import com.shjn.collector.QhDetailCollector;
import com.shjn.collector.ZqCollector;
import com.shjn.collector.ZqDetailCollector;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		collector();
		//test();
	}

	public static void test() {
		PageCollector pc = new PageCollector();
		String result1 = pc.getResponse("http://gs.amac.org.cn/amac-infodisc/res/pof/manager/101000000194.html");
		String result = pc.collectManagerString("高管情况", result1);
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

						}
						if (text.contains("总经理")) {

						}
						if (text.contains("合规风控")) {

						}
				}
				System.out.println(text);
			}
		}
		// System.out.println(result);
	}

	public static void collector() {

		// 私募基金管理人从业黑名单
//		new HmdCollector().run();

		// 撤销管理人登记的名单
		new CxdjCollector().run();

		// 基金数据
		//new FundCollector().run();
		//new FundDetailCollector().run();

		// 基金专户产品
		//new FundAccountCollector().run();
		//new FundAccountDetailCollector().run();

		 // 证券公司私募产品数据
		new ZqCollector().run();
		new ZqDetailCollector().run();
		//
		// 期货资管产品
		new QhCollector().run();
		new QhDetailCollector().run();*//*

		// 基金管理人数据
		//new ManagerCollector().run();
		//new ManagerDetailCollector().run();
		//new ManagerDetailFundScaleCollector().run();
		//new ZqCollector().run();
	}
}
