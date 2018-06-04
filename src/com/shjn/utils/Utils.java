package com.shjn.utils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class Utils {

	// string to date
	public static final Date String2Date(String s) throws ParseException {
		if (s == null || s.equals("")) {
			return null;
		}

		String format = "yyyy-MM-dd";
		if (s.contains("年")) {
			format = "yyyy年MM月dd";
		} else if (s.contains("/")) {
			format = "yyyy/MM/dd";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(s.replaceAll("[^\\d\\/\\-年月日]", ""));
	}

	public static boolean DB2Excel(ResultSet rs, String title, String fileName) {
		fileName = title + " " + fileName;
		boolean flag = false;
		WritableWorkbook workBook = null;
		WritableSheet sheet = null;
		Label label = null;

		try {
			// 创建Excel表
			String path = fileName;
			workBook = Workbook.createWorkbook(new File(path));
			// 创建Excel表中的sheet
			sheet = workBook.createSheet(title, 0);
			// 向Excel中添加数据
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			String columnName = null;
			int row = 0;
			// 最合适列宽
			int columnBestWidth[] = new int[columnCount];
			int width = 0;
			// 添加标题
			for (int i = 0; i < columnCount; i++) {
				columnName = rsmd.getColumnName(i + 1);
				columnName = replaceTitle(columnName);
				label = new Label(i, row, columnName);
				System.out.println("标题：" + i + "---" + row + "---" + columnName);
				sheet.addCell(label);
				width = getColumnBestWidth(columnName); /// 汉字占2个单位长度
				columnBestWidth[i] = width;
			}
			System.out.println("写入标题成功");

			while (rs.next()) {
				row++;
				for (int i = 0; i < columnCount; i++) {
					String value = rs.getString(i + 1);
					label = new Label(i, row, value);
					System.out.println("内容：" + i + "---" + row + "---" + value);
					sheet.addCell(label);
					width = getColumnBestWidth(value);
					if (columnBestWidth[i] < width) /// 求取到目前为止的最佳列宽
						columnBestWidth[i] = width;
				}
			}
			for (int i = 0; i < columnCount; i++) { /// 设置每列宽
				sheet.setColumnView(i, columnBestWidth[i] + 1);
			}
			System.out.println("写入内容成功");
			// 关闭文件
			workBook.write();
			System.out.println("数据成功写入Excel");
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				workBook.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return flag;
	}

	public static int getColumnBestWidth(String value) {
		if (value == null)
			return 0;
		int width = value.length() + getChineseNum(value); /// 汉字占2个单位长度
		return width;
	}

	public static int getChineseNum(String context) { /// 统计context中是汉字的个数
		int lenOfChinese = 0;
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]"); // 汉字的Unicode编码范围
		Matcher m = p.matcher(context);
		while (m.find()) {
			lenOfChinese++;
		}
		return lenOfChinese;
	}

	public static String replaceTitle(String title) {
		switch (title) {
			case "ID_GLA_ACCOUNT":
				title = "科目号";
				break;
		}

		return title;
	}
}
