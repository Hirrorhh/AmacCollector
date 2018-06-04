package com.shjn.collector;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class PageCollector {

	protected String[] keywords = { "对冲基金", "私募基金", "大数据", "大资管", "资产管理", "量化投资", "量化对冲", "期货投资", "证券投资", "二级市场", "PE",
			"天使投资", "投资顾问" };

	protected static Logger logger = Logger.getLogger(PageCollector.class);
	private int timeout = 3000;
	private int maxRetry = 3;
	private int retryNum = 0;

	public String getResponse(String url) {
		String result = null;

		// 读取目的网页URL地址，获取网页源码
		Document doc = null;
		try {
			doc = Jsoup.connect(url).timeout(this.timeout).get();
			String body = doc.body().html();
			String script = doc.getElementsByTag("script").html();
			result = Jsoup.clean(body, Whitelist.relaxed());
			result += script;
		} catch (Exception e) {
			if (this.retryNum < this.maxRetry) {
				this.retryNum++;
				result = this.getResponse(url);
			} else {
				logger.error(url);
				logger.error(e.getMessage(), e);
			}
		}

		this.retryNum = 0;
		return result;
	}

	// 提取数据
	protected String collectString(String key, String result) {
		if (key == null || result == null) {
			return null;
		}

		Pattern p;
		Matcher m;

		// 去除A标签
		result = result.replaceAll("</?a[^>]*>", "");

		p = Pattern.compile(key + ":?</t[dr]>[^>]*>([^<]+)");
		m = p.matcher(result);

		if (m.find()) {
			String v = m.group(1);
			if (v != null && !v.equals("")) {
				return v.trim().replaceAll("&nbsp;", " ");
			}
		}
		return null;
	}

	// 提取数据
	public String collectManagerString(String key, String result) {
		if (key == null || result == null) {
			return null;
		}

		Pattern p;
		Matcher m;

		// 去除A标签
		result = result.replaceAll("</?a[^>]*>", "");

		p = Pattern.compile(key + ":?</td>([\\s\\S]*?)(?=</table>)");
		m = p.matcher(result);

		if (m.find()) {
			String v = m.group(1);
			if (v != null && !v.equals("")) {
				return v.trim().replaceAll("&nbsp;", " ");
			}
		}
		return null;
	}

	// string to int
	protected int String2Int(String s) {
		if (s == null || s.equals("")) {
			return 0;
		} else {
			return Integer.parseInt(s.replaceAll(",", ""));
		}
	}

	// string to double
	protected Double String2Double(String s) {
		if (s == null || s.equals("")) {
			return null;
		} else {
			return Double.parseDouble(s.replaceAll("[^\\d\\.]", ""));
		}
	}

	// 判断时间是否大于一年
	protected boolean isOneYear(Date start, Date end) {
		Calendar startday = Calendar.getInstance();
		Calendar endday = Calendar.getInstance();
		startday.setTime(start);
		startday.add(Calendar.YEAR, 1);
		endday.setTime(end);
		return startday.before(endday);
	}
}