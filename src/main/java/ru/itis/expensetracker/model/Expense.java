package ru.itis.expensetracker.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
public class Expense {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime expenseDate;
    private Long userId;
    private Long walletId;
    private Long categoryId;
}
