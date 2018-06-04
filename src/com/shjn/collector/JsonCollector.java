package com.shjn.collector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public abstract class JsonCollector {

	protected static Logger logger = Logger.getLogger(JsonCollector.class);

	protected abstract String createUrl(String baseUrl);

	protected int page = 1;
	protected int size = 100;
	protected CharSequence csq = "{}";
	private int timeout = 3000;
	private int maxRetry = 5;
	private int retryNum = 0;

	// 得到页面的JSON信息
	protected String getResponse(String baseUrl) {
		String result = null;
		if (baseUrl != null) {
			String fullUrl = "";
			if (this.retryNum == 0) {
				fullUrl = this.createUrl(baseUrl);
				this.page++;
			} else {
				fullUrl = baseUrl;
			}
			result = this.post(fullUrl);
		}
		return result;
	}

	// 发送HttpPost请求
	private String post(String fullUrl) {

		String result = null;
		try {
			// 创建连接
			logger.info("获取页面数据的fullUrl: "+fullUrl);
			URL url = new URL(fullUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setConnectTimeout(this.timeout);
			connection.setReadTimeout(this.timeout);
			// 设置请求方式
			connection.setRequestMethod("POST");
			// 设置接收数据的格式
			connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
			if (fullUrl.contains("amacWeb/user!"))
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			else
				connection.setRequestProperty("Content-Type", "application/json");

			connection.connect();
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			out.append(this.csq);
			out.flush();
			out.close();

			InputStream is = connection.getInputStream();
			// 获取响应代码
			int rc = (int) connection.getResponseCode();

			// 得到网站正常响应
			if (rc == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

				StringBuilder responseBuilder = new StringBuilder();
				String chunk;
				while ((chunk = br.readLine()) != null) {
					responseBuilder.append(chunk);
				}

				result = responseBuilder.toString();
			}
		} catch (Exception e) {
			if (this.retryNum < this.maxRetry) {
				this.retryNum++;
				result = this.getResponse(fullUrl);
			} else {
				logger.error(fullUrl);
				logger.error(e.getMessage(), e);
			}
		}

		this.retryNum = 0;
		return result;
	}
}