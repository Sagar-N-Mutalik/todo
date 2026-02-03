package com.todo.application.model;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private boolean completed;

    private LocalDateTime createdAt;

    // Automatically set the date when a task is created
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}