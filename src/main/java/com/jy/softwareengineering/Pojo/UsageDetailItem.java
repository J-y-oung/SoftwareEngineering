package com.jy.softwareengineering.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/*
  * 这是一个记录房间使用详情的类
  * 逻辑：因为要求每次修改空调参数就产生一条详单中的项，因此先创建一个详单条目类，每次
  * 调用set方法即视为修改空调参数，就生一条详单条目。其中，模式，风速以及起止时间都是
  * 上次使用的值。因为一元一度，因此价格可以直接用两次温度之差记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageDetailItem {
    private int roomId;
    private int mode;
    private int fanSpeed;
    private double startTemp;
    private double endTemp;
    private double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
