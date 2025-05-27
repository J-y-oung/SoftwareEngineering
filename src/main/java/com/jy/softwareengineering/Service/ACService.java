package com.jy.softwareengineering.Service;

import com.jy.softwareengineering.Mapper.RoomMapper;
import com.jy.softwareengineering.Pojo.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;


@Service
public class ACService {

    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private ACSchedulerService acSchedulerService;
//    开机时先设置三个默认值，制热，中风速，22度
    public String powerOn(Integer roomId)
    {
        Room room = roomService.selectById(roomId);
        room.setAcOn(true);
        room.setMode(1);
        room.setFanSpeed(1);
        room.setCurrentTemp(22.0);
        room.setTargetTemp(22.0);
        set(room);
        return "开机成功";
    }
//    先检查参数是否在老师提供的表格范围内，参数合法就将任务加入线程池
    public String set(Room room){
        if(!checkParams(room))
            return "参数错误,请确保传递参数合法";
        if(!Boolean.TRUE.equals(room.getAcOn())  || (room.getCheckinTime() == null))
            return "未入住、未启动情况下禁止操控空调";
        roomMapper.update(room);
        acSchedulerService.addTask(new RoomTempTask(room.getRoomId(), room.getMode(), room.getFanSpeed()));
        return "设置成功";
    }
    public String powerOff(Integer roomId)
    {
        Room room = roomService.selectById(roomId);
        room.setAcOn(false);
//        每次关机视为过了一天，就加一次房费
        room.setPrice(room.getPrice() + room.getFeeRate());
//        关机开始回温,将目标温度设为null,便于后面逻辑判断
        room.setTargetTemp(null);
        roomMapper.update(room);
        acSchedulerService.addTask(new RoomTempTask(room.getRoomId(), room.getMode(), room.getFanSpeed()));
        return "关机成功";
    }
//    这是一个内部类，继承Runnable接口，用于实现定时任务
    public class RoomTempTask implements Runnable {
        private final int roomId;
        private final int mode;
        private final int fanSpeed;
        private volatile boolean paused = false;
        private volatile boolean canceled = false;
        private volatile boolean completed = false;
        private ScheduledFuture<?> future;
//        暂停
        public void pause() {
            paused = true;
        }
//        获取优先级，直接根据风速来判断
        public int getPriority() {
            return fanSpeed;
        }
//        高风速5秒变一次，以此类推
        public long getDelayInSeconds() {
            return switch (fanSpeed) {
                case 2 -> 5;
                case 1 -> 10;
                case 0 -> 15;
                default -> 10;
            };
        }
//        取消任务
        public void cancel() {
            this.canceled = true;
            if (future != null) {
                future.cancel(true);
            }
        }
//        继续任务
        public void resume() {
            paused = false;
        }

        public boolean isDone() {
            return completed || canceled;
        }

        public void setFuture(ScheduledFuture<?> future) {
            this.future = future;
        }

        public RoomTempTask(int roomId, int mode,  int fanSpeed) {
            this.roomId = roomId;
            this.mode = mode;
            this.fanSpeed = fanSpeed;
        }
//        这里是核心逻辑
        @Override
        public void run() {
            if (paused || canceled || completed) return;

            Room room = roomMapper.selectById(roomId);
            double current = room.getCurrentTemp();
            double target = getTargetTemperature(room);

            // 因为每次变0.5，所以判断差值在0.5以内就算完成
            if (Math.abs(current - target) < 0.5) {
                completed = true;
                if (future != null) {
                    future.cancel(false);
                }
                return;
            }

            // 调整温度，不管制冷制热回温逻辑都是一样的
            double delta = (current < target) ? 0.5 : -0.5;
            room.setCurrentTemp(current + delta);
//            计费，防止回温也算钱，所以加个判断
            if (room.getTargetTemp() != null) {
                room.setPrice(room.getPrice() + 0.5);
            }
            roomMapper.update(room);
        }

        private double getTargetTemperature(Room room) {
            // 若设置了 targetTemp，说明空调开着，使用该温度
            if (room.getTargetTemp() != null) {
                return room.getTargetTemp();
            }
            // 否则为关机回温，使用默认温度
            return (room.getMode() == 1) ? room.getDefaultTemp() : room.getDefaultCoolTemp();
        }

        public int getRoomId() {
            return roomId;
        }
    }

//这里就是一个简单的判断参数合法性
    public boolean checkParams(Room room) {
        if(room.getMode() == 0){//制冷判断
            if(room.getTargetTemp() < 18 ||  room.getTargetTemp() > 28)
                return false;
            if(room.getFanSpeed() < 0 || room.getFanSpeed() > 2)
                return false;
        }
        else{//制热判断
            if(room.getTargetTemp() < 18 || room.getTargetTemp() > 25)
                return false;
            if(room.getFanSpeed() < 0 || room.getFanSpeed() > 2)
                return false;
        }
        return true;
    }
}
