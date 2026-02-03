package com.todo.application.controller;

import com.todo.application.dto.TodoItemDTO;
import com.todo.application.model.TodoItem;
import com.todo.application.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @GetMapping("/")
    public String index(Model model) {
        // Fetch Entities -> Convert to DTOs -> Send to View
        var todos = todoRepository.findAllByOrderByCompletedAscCreatedAtDesc()
                .stream()
                .map(TodoItemDTO::fromEntity) // Clean conversion
                .collect(Collectors.toList());
        
        model.addAttribute("todos", todos);
        return "index";
    }

    @PostMapping("/add")
    public String addTodo(@RequestParam("title") String title) {
        if (title != null && !title.trim().isEmpty()) {
            // Create Entity from input
            TodoItem todo = new TodoItem();
            todo.setTitle(title.trim());
            todo.setCompleted(false);
            todoRepository.save(todo);
        }
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/toggle/{id}")
    public String toggleTodo(@PathVariable Long id) {
        todoRepository.findById(id).ifPresent(todo -> {
            todo.setCompleted(!todo.isCompleted());
            todoRepository.save(todo);
        });
        return "redirect:/";
    }
}