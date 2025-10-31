package ru.itis.expensetracker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseDetailDto {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime expenseDate;

    private String categoryName;
    private String userName;
}