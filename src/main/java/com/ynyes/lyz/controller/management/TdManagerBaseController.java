package com.ynyes.lyz.controller.management;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class TdManagerBaseController {

	/**
	 * 字符串转换时间默认格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @param time
	 *            需要转换的时间
	 * @param dateFormat
	 *            时间格式
	 * @return
	 */
	public Date stringToDate(String time, String dateFormat) {
		if (null == dateFormat || "".equals(dateFormat)) {
			dateFormat = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = null;
		if (StringUtils.isNotBlank(time)) {
			try {
				date = sdf.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * 添加导出列名
	 * 
	 * @param CellValues
	 *            列名数组
	 * @param style
	 *            样式
	 * @param style
	 *            当前行
	 * @return
	 */
	public void cellDates(String[] cellValues, HSSFCellStyle style,
			HSSFRow row) {
		HSSFCell cell = null;
		if (null != cellValues && cellValues.length > 0) {
			for (int i = 0; i < cellValues.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(cellValues[i]);
				cell.setCellStyle(style);
			}
		}
	}

	/**
	 * @author lc
	 * @注释：下载
	 */
	public Boolean download(HSSFWorkbook wb, String exportUrl,
			HttpServletResponse resp) {
		try {
			OutputStream os;
			try {
				os = resp.getOutputStream();
				try {
					resp.reset();
					resp.setHeader("Content-Disposition",
							"attachment; filename=" + "table.xls");
					resp.setContentType("application/octet-stream; charset=utf-8");
					wb.write(os);
					os.flush();
				} finally {
					if (os != null) {
						os.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
