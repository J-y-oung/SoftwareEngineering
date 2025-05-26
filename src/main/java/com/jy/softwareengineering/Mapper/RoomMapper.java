package com.jy.softwareengineering.Mapper;

import com.jy.softwareengineering.Pojo.Room;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface RoomMapper {
    public void update(Room room);
    public Room selectById(Integer roomId);
}
