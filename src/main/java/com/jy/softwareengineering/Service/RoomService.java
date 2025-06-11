package com.jy.softwareengineering.Service;

import com.jy.softwareengineering.Mapper.RoomMapper;
import com.jy.softwareengineering.Pojo.BillItem;
import com.jy.softwareengineering.Pojo.ExcelExporter;
import com.jy.softwareengineering.Pojo.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomMapper roomMapper;
    public String checkIn(Integer roomId)
    {
        LocalDateTime  time = LocalDateTime.now();
        Room room = new Room();
        room.setRoomId(roomId);
        room.setCheckinTime(time);
        roomMapper.update(room);
        return "入住成功";
    }
//退房引起结账，并更新数据库，将需要修改的条目清零
    public String checkOut(Integer roomId)
    {
        LocalDateTime  time = LocalDateTime.now();
        Room room = selectById(roomId);
        room.setCheckoutTime(time);
        List<BillItem> bill = List.of(new BillItem(
                room.getRoomId(),
                room.getCheckinTime(),
                room.getCheckoutTime(),
                room.getPrice()
        ));
        try {
            String projectRoot = System.getProperty("user.dir");
            String filePath = projectRoot + "账单_" + room.getRoomId() + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            ExcelExporter.exportBillToExcel(bill, filePath);
            System.out.println("账单已导出：" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return "账单导出失败";
        }
        System.out.println(room);
//        退房后设置入住、离店、费用数据为null，便于set判断，账单的输入在此之前，因此不受影响
        room.setPrice(0.0);
        room.setCheckinTime(null);
        room.setCheckoutTime(null);
        roomMapper.update(room);
        return "退房成功";
    }
    public Room selectById(Integer roomId)
    {
        return roomMapper.selectById(roomId);
    }
}
