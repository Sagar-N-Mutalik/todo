package com.todo.application.repository;

import com.todo.application.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<TodoItem, Long> {
    // Custom query to keep completed items at the bottom
    List<TodoItem> findAllByOrderByCompletedAscCreatedAtDesc();
}