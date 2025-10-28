package ru.itis.expensetracker.model;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class Category {
    private Long id;
    private String name;
    private Long userId; // null, если категория общая
}
