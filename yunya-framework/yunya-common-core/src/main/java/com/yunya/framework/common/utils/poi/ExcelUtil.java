package com.yunya.framework.common.utils.poi;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.yunya.framework.common.annation.Excel;
import com.yunya.framework.common.annation.Excel.Type;
import com.yunya.framework.common.annation.Excels;
import com.yunya.framework.common.utils.ReflectionUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.text.Convert;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel相关处理
 *
 * @author: chow
 * @date: 2020/7/18 21:46
 * @description:
 * @since: 1.0.0
 */
public class ExcelUtil<T> {
  private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

  /** Excel sheet最大行数，默认65536 */
  public static final int SHEET_SIZE = 65536;

  /** 工作表名称 */
  private String sheetName;

  /** 导出类型（EXPORT:导出数据；IMPORT：导入模板） */
  private Type type;

  /** 工作薄对象 */
  private Workbook wb;

  /** 工作表对象 */
  private Sheet sheet;

  /** 样式列表 */
  private Map<String, CellStyle> styles;

  /** 导入导出数据列表 */
  private List<T> list;

  /** 注解列表 */
  private List<Object[]> fields;

  /** 最大高度 */
  private short maxHeight;

  /** 实体对象 */
  public Class<T> clazz;

  /** 单元格合并区域 */
  private List<CellRangeAddress> regions;

  /** 统计列表 */
  private final Map<Integer, Double> statistics = new HashMap<>(16);

  /** 数字格式 */
  private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("######0.00");

  public ExcelUtil(Class<T> clazz) {
    this.clazz = clazz;
  }

  public void init(List<T> list, String sheetName, Type type) {
    if (list == null) {
      list = new ArrayList<>();
    }
    this.list = list;
    this.sheetName = sheetName;
    this.type = type;
    createExcelField();
    createWorkbook();
  }

  /**
   * 对excel表单默认第一个索引名转换成list
   *
   * @param is 输入流
   * @return 转换后集合
   */
  public List<T> importExcel(InputStream is) throws Exception {
    return importExcel(StringHelper.EMPTY, is);
  }

  /**
   * 对excel表单指定表格索引名转换成list
   *
   * @param sheetName 表格索引名
   * @param is 输入流
   * @return 转换后集合
   */
  public List<T> importExcel(String sheetName, InputStream is) throws Exception {
    this.type = Type.IMPORT;
    this.wb = WorkbookFactory.create(is);
    List<T> list = new ArrayList<>();
    // 如果指定sheet名,则取指定sheet中的内容.
    Sheet sheet = StringHelper.isNotEmpty(sheetName) ? wb.getSheet(sheetName) : wb.getSheetAt(0);
    // 如果传入的sheet名不存在则默认指向第1个sheet.
    if (sheet == null) {
      throw new IOException("文件sheet不存在");
    }
    int rows = sheet.getPhysicalNumberOfRows();
    if (rows > 0) {
      // 定义一个map用于存放excel列的序号和field.
      Map<String, Integer> cellMap = new HashMap<>(16);
      // 获取表头
      Row heard = sheet.getRow(0);
      int bound = heard.getPhysicalNumberOfCells();
      for (int i = 0; i < bound; i++) {
        Cell cell = heard.getCell(i);
        if (StringHelper.isNotNull(cell)) {
          String value = this.getCellValue(heard, i).toString();
          cellMap.put(value, i);
        } else {
          cellMap.put(null, i);
        }
      }
      // 有数据时才处理 得到类的所有field.
      Field[] allFields = clazz.getDeclaredFields();
      // 定义一个map用于存放列的序号和field.
      Map<Integer, Field> fieldsMap = new HashMap<>(16);
      setFieldColumn(cellMap, allFields, fieldsMap);
      addEntityToList(list, sheet, rows, fieldsMap);
    }
    return list;
  }

  /**
   * 添加实体到列表
   *
   * @param list 数据列表
   * @param sheet sheet表单
   * @param rows 行树
   * @param fieldsMap 字段
   * @throws Exception
   */
  private void addEntityToList(List<T> list, Sheet sheet, int rows, Map<Integer, Field> fieldsMap)
      throws Exception {
    for (int i = 1; i < rows; i++) {
      // 从第2行开始取数据,默认第一行是表头.
      Row row = sheet.getRow(i);
      T entity = null;
      for (Map.Entry<Integer, Field> entry : fieldsMap.entrySet()) {
        Object val = this.getCellValue(row, entry.getKey());
        // 如果不存在实例则新建.
        entity = (entity == null ? clazz.newInstance() : entity);
        // 从map中得到对应列的field.
        Field field = fieldsMap.get(entry.getKey());
        // 取得类型,并根据对象类型设置值.
        Class<?> fieldType = field.getType();
        if (String.class == fieldType) {
          String s = Convert.toStr(val);
          if (StringUtils.endsWith(s, ".0")) {
            val = StringUtils.substringBefore(s, ".0");
          } else {
            String dateFormat = field.getAnnotation(Excel.class).dateFormat();
            if (StringUtils.isNotEmpty(dateFormat)) {
              val = DateUtil.format((Date) val, dateFormat);
            } else {
              val = Convert.toStr(val);
            }
          }
        } else if ((Integer.TYPE == fieldType || Integer.class == fieldType)
            && StringUtils.isNumeric(Convert.toStr(val))) {
          val = Convert.toInt(val);
        } else if (Long.TYPE == fieldType || Long.class == fieldType) {
          val = Convert.toLong(val);
        } else if (Double.TYPE == fieldType || Double.class == fieldType) {
          val = Convert.toDouble(val);
        } else if (Float.TYPE == fieldType || Float.class == fieldType) {
          val = Convert.toFloat(val);
        } else if (BigDecimal.class == fieldType) {
          val = Convert.toBigDecimal(val);
        } else if (Date.class == fieldType) {
          if (val instanceof String) {
            val = DateUtil.parse((String) val);
          } else if (val instanceof Double) {
            val = new DateTime(val).toDate();
          }
        } else if (Boolean.TYPE == fieldType || Boolean.class == fieldType) {
          val = Convert.toBool(val, false);
        }
        Excel attr = field.getAnnotation(Excel.class);
        String propertyName = field.getName();
        if (StringHelper.isNotEmpty(attr.targetAttr())) {
          propertyName = field.getName() + "." + attr.targetAttr();
        } else if (StringHelper.isNotEmpty(attr.readConverterExp())) {
          val = reverseByExp(String.valueOf(val), attr.readConverterExp(), attr.separator());
        }
        ReflectionUtils.invokeSetter(entity, propertyName, val);
      }
      list.add(entity);
    }
  }

  /**
   * 设置字段属性
   *
   * @param cellMap 单元格
   * @param allFields 字段
   * @param fieldsMap 字段
   */
  private void setFieldColumn(
      Map<String, Integer> cellMap, Field[] allFields, Map<Integer, Field> fieldsMap) {
    for (Field field : allFields) {
      Excel attr = field.getAnnotation(Excel.class);
      if (null != attr) {
        if (attr.type() == Type.ALL || attr.type() == type) {
          // 设置类的私有字段属性可访问.
          field.setAccessible(true);
          Integer column = cellMap.get(attr.name());
          if (null != column) {
            fieldsMap.put(column, field);
          }
        }
      }
    }
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param response 返回数据
   * @param list 导出数据集合
   * @param sheetName 工作表的名称
   * @return 结果
   * @throws IOException
   */
  public void exportExcel(HttpServletResponse response, List<T> list, String sheetName)
      throws IOException {
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    this.init(list, sheetName, Type.EXPORT);
    exportExcel(response.getOutputStream());
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param response 返回数据
   * @param list 导出数据集合
   * @param sheetName 工作表的名称
   * @param fileName excel文件名
   * @return 结果
   * @throws IOException
   */
  public void exportExcel(
      HttpServletResponse response, List<T> list, String sheetName, String fileName)
      throws IOException {
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    response.setHeader(
        "Content-Disposition",
        "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");
    this.init(list, sheetName, Type.EXPORT);
    exportExcel(response.getOutputStream());
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param response 返回数据
   * @param list 导出数据集合
   * @param sheetName 工作表的名称
   * @param fileName excel文件名
   * @param dynamicTitle 动态表头
   * @return 结果
   * @throws IOException
   */
  public void exportExcel(
          HttpServletResponse response, List<T> list, String sheetName, String fileName, Map<String, String> dynamicTitle)
          throws IOException {
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    response.setHeader(
            "Content-Disposition",
            "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");
    this.init(list, sheetName, Type.EXPORT);
    exportExcel(response.getOutputStream(), dynamicTitle);
  }

  /**
   * 获取文件名
   *
   * @param sDate
   * @param eDate
   * @param mid
   * @param tail
   * @return
   */
  public String getFileName(String sDate, String eDate, String mid, String tail) {
    return getFileName(null, sDate, eDate, mid, tail);
  }
  /**
   * 获取文件名
   *
   * @param head
   * @param sDate
   * @param eDate
   * @return
   */
  public String getFileName(String head, String sDate, String eDate, String mid, String tail) {
    StringBuilder res = new StringBuilder();
    if (StringHelper.isNotEmpty(head)) {
      res.append(head);
    }
    if (StringHelper.isNotEmpty(sDate)) {
      String[] str = sDate.split("-");
      if (str.length == 1) { // 年
        res.append(sDate).append("年");
      } else if (str.length == 2) { // 月
        res.append(str[0]).append("年").append(str[1]).append("月");
      } else if (str.length == 3) { // 日
        for (int i = 0; i < str.length; i++) {
          if (i > 0 && res.length() > 0) {
            res.append(".");
          }
          res.append(str[i]);
        }
        if (StringHelper.isNotEmpty(eDate)) {
          str = eDate.split("-");
          res.append("-");
          for (int i = 0; i < str.length; i++) {
            if (i > 0 && res.length() > 0) {
              res.append(".");
            }
            res.append(str[i]);
          }
        }
      }
    }
    if (StringHelper.isNotEmpty(mid)) {
      res.append(mid);
    }
    res.append(tail);
    return res.toString();
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param response 返回数据
   * @param sheetName 工作表的名称
   * @return 结果
   */
  public void importTemplateExcel(HttpServletResponse response, String sheetName)
      throws IOException {
    response.setContentType("application/vnd.ms-excel");
    response.setCharacterEncoding("utf-8");
    this.init(null, sheetName, Type.IMPORT);
    exportExcel(response.getOutputStream());
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param outputStream 输出流
   * @return 结果
   */
  public void exportExcel(OutputStream outputStream) {
    try {
      // 取出一共有多少个sheet.
      double sheetNo = Math.ceil(list.size() / SHEET_SIZE);
      for (int index = 0; index <= sheetNo; index++) {
        // 创建sheet工作表
        createSheet(sheetNo, index);
        // 产生一行
        Row row = sheet.createRow(0);
        int column = 0;
        // 写入各个字段的列头名称
        for (Object[] os : fields) {
          Excel excel = (Excel) os[1];
          this.createCell(excel, row, column++);
        }
        if (Type.EXPORT.equals(type)) {
          fillExcelData(index);
          addStatisticsRow();
        }
      }

      // 设置表格合并
      this.mergeRegion();

      wb.write(outputStream);
    } catch (Exception e) {
      log.error("导出Excel异常{}", e.getMessage());
      log.error("导出Excel异常{}", e);
    } finally {
      if (wb != null) {
        try {
          wb.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
  }

  /**
   * 动态表头
   *
   * @param outputStream
   */
  public void exportExcel(OutputStream outputStream, Map<String, String> dynamicTitle) {
    try {
      // 取出一共有多少个sheet.
      double sheetNo = Math.ceil(list.size() / SHEET_SIZE);
      for (int index = 0; index <= sheetNo; index++) {
        // 创建sheet工作表
        createSheet(sheetNo, index);
        // 产生一行
        Row row = sheet.createRow(0);
        int column = 0;
        for (String head : dynamicTitle.values()) {
          sheet.setColumnWidth(column, (int) ((16 + 0.72) * 256));
          this.createCell(head, row, column++, "header");
        }
        if (Type.EXPORT.equals(type)) {
          int startNo = index * SHEET_SIZE;
          int endNo = Math.min(startNo + SHEET_SIZE, list.size());
          for (int i = startNo; i < endNo; i++) {
            row = sheet.createRow(i + 1 - startNo);
            // 得到导出对象.
            JSONObject obj = (JSONObject) list.get(i);
            int col = 0;
            for (Map.Entry<String, String> entry : dynamicTitle.entrySet()) {
              String value = obj.getString(entry.getKey());
              this.createCell(value, row, col++, "data");
            }
          }
        }
      }
      // 设置表格合并
      this.mergeRegion();
      wb.write(outputStream);
    } catch (Exception e) {
      log.error("导出Excel异常{}", e.getMessage());
      log.error("导出Excel异常{}", e);
    } finally {
      if (wb != null) {
        try {
          wb.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
      log.info("导出完成");
    }
  }

  /**
   * 填充excel数据
   *
   * @param index 序号
   */
  public void fillExcelData(int index) {
    int startNo = index * SHEET_SIZE;
    int endNo = Math.min(startNo + SHEET_SIZE, list.size());
    for (int i = startNo; i < endNo; i++) {
      Row row = sheet.createRow(i + 1 - startNo);
      // 得到导出对象.
      T vo = list.get(i);
      int column = 0;
      for (Object[] os : fields) {
        Field field = (Field) os[0];
        Excel excel = (Excel) os[1];
        // 设置实体类私有属性可访问
        field.setAccessible(true);
        this.addCell(excel, row, vo, field, column++);
      }
    }
  }

  /**
   * 创建表格样式
   *
   * @param wb 工作薄对象
   * @return 样式列表
   */
  private Map<String, CellStyle> createStyles(Workbook wb) {
    // 写入各条记录,每条记录对应excel表中的一行
    Map<String, CellStyle> styles = new HashMap<>(16);
    CellStyle style = wb.createCellStyle();
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setBorderRight(BorderStyle.THIN);
    style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    style.setBorderLeft(BorderStyle.THIN);
    style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    style.setBorderTop(BorderStyle.THIN);
    style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    style.setBorderBottom(BorderStyle.THIN);
    style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    Font dataFont = wb.createFont();
    dataFont.setFontName("宋体");
    dataFont.setFontHeightInPoints((short) 12);
    style.setFont(dataFont);
    style.setWrapText(true);
    styles.put("data", style);

    style = wb.createCellStyle();
    style.cloneStyleFrom(styles.get("data"));
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    Font headerFont = wb.createFont();
    headerFont.setFontName("宋体");
    headerFont.setFontHeightInPoints((short) 12);
    headerFont.setBold(true);
    style.setWrapText(true);
    headerFont.setColor(IndexedColors.WHITE.getIndex());
    style.setFont(headerFont);
    styles.put("header", style);

    style = wb.createCellStyle();
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    Font totalFont = wb.createFont();
    totalFont.setFontName("宋体");
    totalFont.setFontHeightInPoints((short) 12);
    style.setFont(totalFont);
    styles.put("total", style);

    style = wb.createCellStyle();
    style.cloneStyleFrom(styles.get("data"));
    style.setAlignment(HorizontalAlignment.LEFT);
    styles.put("data1", style);

    style = wb.createCellStyle();
    style.cloneStyleFrom(styles.get("data"));
    style.setAlignment(HorizontalAlignment.CENTER);
    styles.put("data2", style);

    style = wb.createCellStyle();
    style.cloneStyleFrom(styles.get("data"));
    style.setAlignment(HorizontalAlignment.RIGHT);
    styles.put("data3", style);

    return styles;
  }

  /**
   * 创建单元格
   *
   * @param attr 属性
   * @param row 行
   * @param column 列
   * @return
   */
  public Cell createCell(Excel attr, Row row, int column) {
    // 创建列
    Cell cell = row.createCell(column);
    // 写入列信息
    cell.setCellValue(attr.name());
    setDataValidation(attr, row, column);
    cell.setCellStyle(styles.get("header"));
    return cell;
  }

  /**
   * 创建单元格
   *
   * @param name 属性
   * @param row 行
   * @param column 列
   * @return
   */
  public Cell createCell(String name, Row row, int column, String titleKey) {
    // 创建列
    Cell cell = row.createCell(column);
    // 写入列信息
    cell.setCellValue(name);
    cell.setCellStyle(styles.get(titleKey));
    return cell;
  }

  /**
   * 设置单元格信息
   *
   * @param value 单元格值
   * @param attr 注解相关
   * @param cell 单元格信息
   */
  public void setCellVo(Object value, Excel attr, Cell cell) {
    switch (attr.cellType()) {
      case STRING:
        cell.setCellValue(StringHelper.isNull(value) ? attr.defaultValue() : value + attr.suffix());
        break;
      case NUMERIC:
        cell.setCellValue(
            StringUtils.contains(Convert.toStr(value), ".")
                ? Convert.toDouble(value)
                : Convert.toInt(value));
        break;
      default:
        break;
    }
  }

  /**
   * 创建表格样式
   *
   * @param attr 属性
   * @param row 行
   * @param column 列
   */
  public void setDataValidation(Excel attr, Row row, int column) {
    if (attr.name().contains("注：")) {
      sheet.setColumnWidth(column, 6000);
    } else {
      // 设置列宽
      sheet.setColumnWidth(column, (int) ((attr.width() + 0.72) * 256));
    }
    // 如果设置了提示信息则鼠标放上去提示.
    if (StringHelper.isNotEmpty(attr.prompt())) {
      // 这里默认设了2-101列提示.
      setXSSFPrompt(sheet, "", attr.prompt(), 1, 100, column, column);
    }
    // 如果设置了combo属性则本列只能选择不能输入
    if (attr.combo().length > 0) {
      // 这里默认设了2-101列只能选择不能输入.
      setXSSFValidation(sheet, attr.combo(), 1, 100, column, column);
    }
  }

  /**
   * 添加单元格
   *
   * @param attr 属性
   * @param row 行
   * @param vo vo
   * @param field 字段
   * @param column 列
   * @return
   */
  public Cell addCell(Excel attr, Row row, T vo, Field field, int column) {
    Cell cell = null;
    try {
      // 设置行高
      row.setHeight(maxHeight);
      // 根据Excel中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
      if (attr.isExport()) {
        // 创建cell
        cell = row.createCell(column);
        int align = attr.align().value();
        cell.setCellStyle(styles.get("data" + (align >= 1 && align <= 3 ? align : "")));
        // 用于读取对象中的属性
        Object value = getTargetValue(vo, field, attr);
        String dateFormat = attr.dateFormat();
        String readConverterExp = attr.readConverterExp();
        String separator = attr.separator();
        if (StringHelper.isNotEmpty(dateFormat) && StringHelper.isNotNull(value)) {
          cell.setCellValue(DateUtil.format((Date) value, dateFormat));
        } else if (StringHelper.isNotEmpty(readConverterExp) && StringHelper.isNotNull(value)) {
          cell.setCellValue(convertByExp(Convert.toStr(value), readConverterExp, separator));
        } else if (value instanceof BigDecimal && -1 != attr.scale()) {
          cell.setCellValue(
              (((BigDecimal) value).setScale(attr.scale(), attr.roundingMode())).toString());
        } else {
          // 设置列类型
          setCellVo(value, attr, cell);
        }
        // 根据Excel注解设置的情况决定是否需要合并，默认false，不合并
        /*boolean attrMerge = attr.isMerge();
        if (attrMerge) {
          mergeCell(i, i + 1, column, column);
        }*/
        addStatisticsData(column, Convert.toStr(value), attr);
      }
    } catch (Exception e) {
      log.error("导出Excel失败{}", e);
    }
    return cell;
  }

  /**
   * 设置 POI XSSFSheet 单元格提示
   *
   * @param sheet 表单
   * @param promptTitle 提示标题
   * @param promptContent 提示内容
   * @param firstRow 开始行
   * @param endRow 结束行
   * @param firstCol 开始列
   * @param endCol 结束列
   */
  public void setXSSFPrompt(
      Sheet sheet,
      String promptTitle,
      String promptContent,
      int firstRow,
      int endRow,
      int firstCol,
      int endCol) {
    DataValidationHelper helper = sheet.getDataValidationHelper();
    DataValidationConstraint constraint = helper.createCustomConstraint("DD1");
    CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
    DataValidation dataValidation = helper.createValidation(constraint, regions);
    dataValidation.createPromptBox(promptTitle, promptContent);
    dataValidation.setShowPromptBox(true);
    sheet.addValidationData(dataValidation);
  }

  /**
   * 设置某些列的值只能输入预制的数据,显示下拉框.
   *
   * @param sheet 要设置的sheet.
   * @param textList 下拉框显示的内容
   * @param firstRow 开始行
   * @param endRow 结束行
   * @param firstCol 开始列
   * @param endCol 结束列
   * @return 设置好的sheet.
   */
  public void setXSSFValidation(
      Sheet sheet, String[] textList, int firstRow, int endRow, int firstCol, int endCol) {
    DataValidationHelper helper = sheet.getDataValidationHelper();
    // 加载下拉列表内容
    DataValidationConstraint constraint = helper.createExplicitListConstraint(textList);
    // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
    CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
    // 数据有效性对象
    DataValidation dataValidation = helper.createValidation(constraint, regions);
    // 处理Excel兼容性问题
    if (dataValidation instanceof XSSFDataValidation) {
      dataValidation.setSuppressDropDownArrow(true);
      dataValidation.setShowErrorBox(true);
    } else {
      dataValidation.setSuppressDropDownArrow(false);
    }
    sheet.addValidationData(dataValidation);
  }

  /**
   * 解析导出值 0=男,1=女,2=未知
   *
   * @param propertyValue 参数值
   * @param converterExp 翻译注解
   * @param separator 分隔符
   * @return 解析后值
   */
  public static String convertByExp(String propertyValue, String converterExp, String separator) {
    StringBuilder propertyString = new StringBuilder();
    String[] convertSource = converterExp.split(",");
    for (String item : convertSource) {
      String[] itemArray = item.split("=");
      if (StringHelper.containsAny(separator, propertyValue)) {
        if (Arrays.stream(propertyValue.split(separator))
            .anyMatch(value -> itemArray[0].equals(value))) {
          propertyString.append(itemArray[1]).append(separator);
          break;
        }
      } else {
        if (itemArray[0].equals(propertyValue)) {
          return itemArray[1];
        }
      }
    }
    return StringHelper.stripEnd(propertyString.toString(), separator);
  }

  /**
   * 反向解析值 男=0,女=1,未知=2
   *
   * @param propertyValue 参数值
   * @param converterExp 翻译注解
   * @param separator 分隔符
   * @return 解析后值
   */
  public static String reverseByExp(String propertyValue, String converterExp, String separator) {
    StringBuilder propertyString = new StringBuilder();
    String[] convertSource = converterExp.split(",");
    for (String item : convertSource) {
      String[] itemArray = item.split("=");
      if (StringUtils.containsAny(separator, propertyValue)) {
        if (Arrays.stream(propertyValue.split(separator))
            .anyMatch(value -> itemArray[1].equals(value))) {
          propertyString.append(itemArray[0]).append(separator);
          break;
        }
      } else {
        if (itemArray[1].equals(propertyValue)) {
          return itemArray[0];
        }
      }
    }
    return StringUtils.stripEnd(propertyString.toString(), separator);
  }

  /** 合计统计信息 */
  private void addStatisticsData(Integer index, String text, Excel entity) {
    if (entity != null && entity.isStatistics()) {
      Double temp = 0D;
      if (!statistics.containsKey(index)) {
        statistics.put(index, temp);
      }
      try {
        temp = Double.valueOf(text);
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
      statistics.put(index, statistics.get(index) + temp);
    }
  }

  /** 创建统计行 */
  public void addStatisticsRow() {
    if (statistics.size() > 0) {
      Cell cell;
      Row row = sheet.createRow(sheet.getLastRowNum() + 1);
      Set<Integer> keys = statistics.keySet();
      cell = row.createCell(0);
      cell.setCellStyle(styles.get("total"));
      cell.setCellValue("合计");

      for (Integer key : keys) {
        cell = row.createCell(key);
        cell.setCellStyle(styles.get("total"));
        cell.setCellValue(DOUBLE_FORMAT.format(statistics.get(key)));
      }
      statistics.clear();
    }
  }

  /**
   * 合并单元格对象
   *
   * @param firstRow 合并起始行
   * @param lastRow 合并结束行
   * @param firstCol 合并开始列
   * @param lastCol 合并结束列
   */
  private void mergeCell(int firstRow, int lastRow, int firstCol, int lastCol) {
    this.regions = new ArrayList<>();
    CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
    regions.add(cellRangeAddress);
  }

  /**
   * 获取bean中的属性值
   *
   * @param vo 实体对象
   * @param field 字段
   * @param excel 注解
   * @return 最终的属性值
   * @throws Exception
   */
  private Object getTargetValue(T vo, Field field, Excel excel) throws Exception {
    Object o = field.get(vo);
    if (StringHelper.isNotEmpty(excel.targetAttr())) {
      String target = excel.targetAttr();
      if (target.contains(".")) {
        String[] targets = target.split("[.]");
        for (String name : targets) {
          o = getValue(o, name);
        }
      } else {
        o = getValue(o, target);
      }
    }
    return o;
  }

  /**
   * 以类的属性的get方法方法形式获取值
   *
   * @param o 对象
   * @param name 名称
   * @return value
   * @throws Exception
   */
  private Object getValue(Object o, String name) throws Exception {
    if (StringUtils.isNotEmpty(name)) {
      Class<?> clazz = o.getClass();
      Field field = clazz.getDeclaredField(name);
      field.setAccessible(true);
      o = field.get(o);
    }
    return o;
  }

  /** 得到所有定义字段 */
  private void createExcelField() {
    this.fields = new ArrayList<>();
    List<Field> tempFields = new ArrayList<>();
    tempFields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
    tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    tempFields.forEach(
        field -> {
          // 单注解
          if (field.isAnnotationPresent(Excel.class)) {
            putToField(field, field.getAnnotation(Excel.class));
          }
          // 多注解
          if (field.isAnnotationPresent(Excels.class)) {
            Excels attrs = field.getAnnotation(Excels.class);
            Excel[] excels = attrs.value();
            Arrays.stream(excels).forEach(excel -> putToField(field, excel));
          }
        });
    this.fields =
        this.fields.stream()
            .sorted(Comparator.comparing(objects -> ((Excel) objects[1]).sort()))
            .collect(Collectors.toList());
    this.maxHeight = getRowHeight();
  }

  /** 根据注解获取最大行高 */
  public short getRowHeight() {
    double maxHeight = 0;
    for (Object[] os : this.fields) {
      Excel excel = (Excel) os[1];
      maxHeight = Math.max(maxHeight, excel.height());
    }
    return (short) (maxHeight * 20);
  }

  /**
   * 放到字段集合中
   *
   * @param field 字段
   * @param attr 属性
   */
  private void putToField(Field field, Excel attr) {
    if (null != attr && (attr.type() == Type.ALL || attr.type() == type)) {
      this.fields.add(new Object[] {field, attr});
    }
  }

  /** 创建一个工作簿 */
  public void createWorkbook() {
    this.wb = new SXSSFWorkbook(500);
  }

  /**
   * 创建工作表
   *
   * @param sheetNo sheet数量
   * @param index 序号
   */
  public void createSheet(double sheetNo, int index) {
    String sname = sheetNo == 0 ? sheetName : sheetName + new DateTime().toString() + "_" + index;
    this.sheet = wb.createSheet();
    this.styles = createStyles(wb);
    // 设置工作表的名称.
    wb.setSheetName(index, sname);
  }

  /**
   * 获取单元格值
   *
   * @param row 获取的行
   * @param column 获取单元格列号
   * @return 单元格值
   */
  public Object getCellValue(Row row, int column) {
    if (row == null) {
      return null;
    }
    Object val = "";
    try {
      Cell cell = row.getCell(column);
      if (StringHelper.isNotNull(cell)) {
        switch (cell.getCellTypeEnum()) {
          case NUMERIC:
          case FORMULA:
            val = cell.getNumericCellValue();
            // POI Excel 日期格式转换/浮点格式处理
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
              val = new DateTime(val).toDate();
            } else {
              if ((Double) val % 1 > 0) {
                val = new BigDecimal(val.toString());
              } else {
                val = new DecimalFormat("0").format(val);
              }
            }
            break;
          case STRING:
            val = cell.getStringCellValue();
            break;
          case BOOLEAN:
            val = cell.getBooleanCellValue();
            break;
          case ERROR:
            val = cell.getErrorCellValue();
            break;
          default:
            break;
        }
      }
    } catch (Exception e) {
      return val;
    }
    return val;
  }

  /**
   * 设置合并单元格区域
   *
   * @param region 单元格区域列表¬
   */
  public void setMergeRegion(List<CellRangeAddress> region) {
    this.regions = region;
  }

  /** 合并单元格 */
  public void mergeRegion() {
    if (StringHelper.isNotEmpty(regions)) {
      regions.forEach(cellRangeAddress -> sheet.addMergedRegion(cellRangeAddress));
    }
  }
}
