package com.jy.softwareengineering.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
//    房间id，唯一标识符，共有五个房间
    private Integer roomId;
//    空调开关
    private Boolean acOn;
//    入住/退房时间，老师说一次开关算一天，这个后续再改
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
//    温度相关参数，在制冷和制热模式下默认温度不一样,前者为制热模式下回温温度
    private Double currentTemp;
    private Double targetTemp;
    private Double defaultTemp;
    private Double defaultCoolTemp;
//    模式，0为制冷，1为制热
    private Integer mode;
//    风扇速度，有三档，0为低，1为中，2为高
    private Integer fanSpeed;
//    房间价格，1-5号房价格分别为100，125，150，200，100
//    退房前该字段的值为空调产生的费用，退房时根据入住天数一并计算房费
    private Double price;
    private Integer feeRate;
}
