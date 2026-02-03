package com.todo.application.dto;

import com.todo.application.model.TodoItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemDTO {

    private Long id;
    private String title;
    private boolean completed;
    private LocalDateTime createdAt;

    // Helper method to convert from Entity to DTO
    // In a larger app, you might use a library like MapStruct for this
    public static TodoItemDTO fromEntity(TodoItem item) {
        return TodoItemDTO.builder()
                .id(item.getId())
                .title(item.getTitle())
                .completed(item.isCompleted())
                .createdAt(item.getCreatedAt())
                .build();
    }

    // Helper method to convert from DTO to Entity
    public static TodoItem toEntity(TodoItemDTO dto) {
        return TodoItem.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .completed(dto.isCompleted())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}