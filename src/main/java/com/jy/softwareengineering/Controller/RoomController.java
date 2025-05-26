package com.jy.softwareengineering.Controller;

import com.jy.softwareengineering.Pojo.Room;
import com.jy.softwareengineering.Service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/checkIn")
    public String checkIn(@RequestParam Integer roomId) {
        return roomService.checkIn(roomId);
    }
//    退房操作，实际上退房应当同时引发结账事件，要求输出excel，后续再补充
    @PostMapping("/checkOut")
    public String checkOut(@RequestParam Integer roomId) {
        return roomService.checkOut(roomId);
    }
    @GetMapping("/selectById")
    public Room selectById(@RequestParam Integer roomId) {
        return roomService.selectById(roomId);
    }
}
