package com.jy.softwareengineering.Controller;

import com.jy.softwareengineering.Pojo.Room;
import com.jy.softwareengineering.Service.ACService;
import com.jy.softwareengineering.Service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/*
    这是跟空调有关的控制类，具体实现都在Service层里

 */
@RestController
@RequestMapping("/ac")
public class ACController {
    @Autowired
    private ACService acService;
    private RoomService roomService;

//    开机，先将需要的值都设为缺省值
    @PostMapping("/powerOn")
    public String powerOn(@RequestParam Integer roomId)
    {
        return acService.powerOn(roomId);
    }
//    调温，调模式调风速等
    @PostMapping("/set")
    public String set(@RequestBody Room room){
        return acService.set(room);
    }
//    关机
    @PostMapping("/powerOff")
    public String powerOff(@RequestParam Integer roomId){
        return acService.powerOff(roomId);
    }
}
