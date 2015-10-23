package com.yoju360.mgmt.core.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * POI Excel帮助类。
 * 
 * @author Evan Wu
 *
 */
public class ExcelUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(ExcelUtils.class);
	public static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
	public static final int UNIT_OFFSET_LENGTH = 7;
	public static final int[] UNIT_OFFSET_MAP = new int[] { 0, 36, 73, 109,
			146, 182, 219 };
	public static final int EXCEL_MAX_ROWS = 65534;

	/**
	 * 创建Excel。
	 * @param listHeaders
	 * @param listRows
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Workbook createExcelSheet(List<String> listHeaders, List<List> listRows) {
		logger.info("listHeaders:"+listHeaders+"listRows:"+listRows);
		Workbook workbook = new HSSFWorkbook();
		/** create workbook header row */
		// head font style
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.LIME.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		Sheet sheet = workbook.createSheet("Sheet1");
		Row row = sheet.createRow(0);
		for (int i = 0; i < listHeaders.size(); i++) {
			Cell cell = row.createCell(i);
			cell.setCellType(HSSFCell.ENCODING_UTF_16);
			cell.setCellValue(listHeaders.get(i));
			cell.setCellStyle(style);
			sheet.setColumnWidth(i,
					ExcelUtils.pixel2WidthUnits(50));
		}
		/** create data rows */
		for (int i = 0; i < listRows.size(); i++) {
			row = sheet.createRow(i + 1);
			List<Object> bean = listRows.get(i);
			for (int j = 0; j < listHeaders.size(); j++) {
				Cell cell = row.createCell(j);
				cell.setCellValue(bean.get(j).toString());
			}
		}
		return workbook;
	}
	
	/**
	 * 使用模板来创建Excel。使用了jxls库。
	 * <p>
	 * Excel模板请参考：http://jxls.sourceforge.net/reference/simplebeans.html
	 * </p>
	 * 
	 * @see http://jxls.sourceforge.net/reference/simplebeans.html
	 * @param templatePath
	 * @param map
	 * @return
	 * @throws FileNotFoundException
	 * @throws ParsePropertyException
	 * @throws InvalidFormatException
	 */
	public static Workbook createExcelSheetByTemplate(String templatePath,
			Map<String, Object> map) throws FileNotFoundException,
			ParsePropertyException, InvalidFormatException {
		FileInputStream in = new FileInputStream(templatePath);
		XLSTransformer transformer = new XLSTransformer();
		Workbook workbook = transformer.transformXLS(in, map);
		return workbook;
	}

	/**
	 * 帮助方法：设置Excel文件下载的response头。
	 * 
	 * @param response
	 * @param excelFileName
	 */
	public static void setExcelResponseHeader(HttpServletRequest request,
			HttpServletResponse response, String excelFileName) {
		response.setHeader("Pragma", "public");
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");
		response.setContentType("application/octet-stream; charset=utf-8");
		response.setHeader("Content-Type", "application/force-download");
		response.setHeader("Content-Type",
				"application/vnd.ms-excel; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ encodeZhFileName(request, excelFileName));
	}

	/**
	 * 对文件流输出下载的中文文件名进行编码 屏蔽各种浏览器版本的差异性
	 */
	public static String encodeZhFileName(HttpServletRequest request,
			String pFileName) {
		String agent = request.getHeader("USER-AGENT");
		try {
			if (null != agent && -1 != agent.indexOf("MSIE")) {
				pFileName = URLEncoder.encode(pFileName, "utf-8");
			} else {
				pFileName = new String(pFileName.getBytes("utf-8"), "iso8859-1");
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Filename encode error", e);
		}
		return pFileName;
	}

	/**
	 * pixel units to excel width units(units of 1/256th of a character width)
	 * 
	 * @param pxs
	 * @return
	 */
	public static short pixel2WidthUnits(int pxs) {
		short widthUnits = (short) (EXCEL_COLUMN_WIDTH_FACTOR * (pxs / UNIT_OFFSET_LENGTH));

		widthUnits += UNIT_OFFSET_MAP[(pxs % UNIT_OFFSET_LENGTH)];

		return widthUnits;
	}

	/**
	 * excel width units(units of 1/256th of a character width) to pixel units
	 * 
	 * @param widthUnits
	 * @return
	 */
	public static int widthUnits2Pixel(short widthUnits) {
		int pixels = (widthUnits / EXCEL_COLUMN_WIDTH_FACTOR)
				* UNIT_OFFSET_LENGTH;

		int offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR;
		pixels += Math.round((float) offsetWidthUnits
				/ ((float) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH));

		return pixels;
	}
}
