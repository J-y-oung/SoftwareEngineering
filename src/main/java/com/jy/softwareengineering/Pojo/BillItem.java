package com.jy.softwareengineering.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillItem {
    private Integer roomId;
    private LocalDateTime checkinTime;
    private LocalDateTime checkoutTime;
    private Double totalPrice;
}

