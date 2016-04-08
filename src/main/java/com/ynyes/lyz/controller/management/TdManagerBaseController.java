package com.ynyes.lyz.controller.management;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
	 * 设置列宽
	 * @param sheet
	 * @param widths
	 */
	public void sheetColumnWidth(HSSFSheet sheet,int[] widths){
		for (int i = 0; i < widths.length; i++) {
			sheet.setColumnWidth(i , widths[i]*256);
		}
		
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
	public Boolean download(HSSFWorkbook wb, String exportUrl,HttpServletResponse resp,String fileName) {
		String filename="table";
		try {
			filename = new String(fileName.getBytes("GBK"), "ISO-8859-1");
		} catch (UnsupportedEncodingException e1) {
			System.out.println("下载文件名格式转换错误！");
		}
		try {
			OutputStream os;
			try {
				os = resp.getOutputStream();
				try {
					resp.reset();
					resp.setHeader("Content-Disposition",
							"attachment; filename=" + filename +".xls");
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
	
//	/**
//     * 异常处理
//     * @param r
//     * @param e
//     * @return
//     */
//    @ExceptionHandler
//    public String exception(HttpServletRequest r, Exception e) {
//        String view = "exception";
//        r.setAttribute("exception", e);
//        String xRequestedWith = r.getHeader("X-Requested-With");
//        if (!StringUtils.isEmpty(xRequestedWith)) {
//            view = "exceptionAjax";
//        }
//        return view;
//    }

}
