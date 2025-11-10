package ru.itis.expensetracker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class ExpenseDetailDto {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime expenseDate;
    private String categoryName;
    private String userName;

    public String getFormattedDate() {
        if (expenseDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return expenseDate.format(formatter);
    }
}
