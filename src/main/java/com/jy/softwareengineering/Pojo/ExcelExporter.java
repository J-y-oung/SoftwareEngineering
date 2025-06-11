package com.jy.softwareengineering.Pojo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//这是用POI导出Excel的类，输入房间号和UsageDetailItem列表，导出Excel文件，具体的实现逻辑在Service中
public class ExcelExporter {

    public static void exportUsageDetail(int roomId, List<UsageDetailItem> detailList) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Room " + roomId + " Usage Detail");

        // 标题行
        String[] headers = {"房间号", "模式", "风速", "起始温度", "结束温度", "费用", "开始时间", "结束时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
//        格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        for (int i = 0; i < detailList.size(); i++) {
            UsageDetailItem item = detailList.get(i);
            Row row = sheet.createRow(i + 1);
//             这里按列填充信息
            row.createCell(0).setCellValue(item.getRoomId());
            row.createCell(1).setCellValue(item.getMode() == 1 ? "制热" : "制冷");
            row.createCell(2).setCellValue(getFanSpeedText(item.getFanSpeed()));
            row.createCell(3).setCellValue(item.getStartTemp());
            row.createCell(4).setCellValue(item.getEndTemp());
            row.createCell(5).setCellValue(item.getPrice());
            row.createCell(6).setCellValue(item.getStartTime().format(formatter));
            row.createCell(7).setCellValue(item.getEndTime().format(formatter));
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 文件保存路径，第一句是用来获取项目根目录，第二句加上System.currentTimeMillis()是为了防止文件名重复，否则会报错
        String projectRoot = System.getProperty("user.dir");
        String fileName = "详单_Room" + roomId + "_" + System.currentTimeMillis() + ".xlsx";
        try (FileOutputStream fos = new FileOutputStream(projectRoot + fileName)) {
            workbook.write(fos);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void exportBillToExcel(List<BillItem> billList, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("账单");

        // 标题行
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("房间号");
        header.createCell(1).setCellValue("入住时间");
        header.createCell(2).setCellValue("退房时间");
        header.createCell(3).setCellValue("总费用");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 内容行
        for (int i = 0; i < billList.size(); i++) {
            BillItem item = billList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(item.getRoomId());
            row.createCell(1).setCellValue(item.getCheckinTime().format(formatter));
            row.createCell(2).setCellValue(item.getCheckoutTime().format(formatter));
            row.createCell(3).setCellValue(item.getTotalPrice());
        }
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    private static String getFanSpeedText(int speed) {
        return switch (speed) {
            case 2 -> "高";
            case 1 -> "中";
            case 0 -> "低";
            default -> " ";
        };
    }
}
