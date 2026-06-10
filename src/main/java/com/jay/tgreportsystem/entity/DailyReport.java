package com.jay.tgreportsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "daily_report")
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate reportDate;

    private Integer totalMembers;
    private Integer activeMembers;
    private Integer newMembers;

    private BigDecimal totalWinLose;
    private BigDecimal rebateAmount;
    private BigDecimal netProfit;

}
