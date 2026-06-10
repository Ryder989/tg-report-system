package com.jay.tgreportsystem.repository;

import com.jay.tgreportsystem.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyReportRepository extends JpaRepository<DailyReport,Long> {
    Optional<DailyReport> findByReportDate(LocalDate reportDate);
}
