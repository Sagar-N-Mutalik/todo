package com.todo.application.controller;

import com.todo.application.model.TodoItem;
import com.todo.application.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    // 1. Load the Home Page with the list of Todos
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("todos", todoRepository.findAllByOrderByCompletedAscCreatedAtDesc());
        return "index"; // Looks for index.html in /templates
    }

    // 2. Add a new Todo
    @PostMapping("/add")
    public String addTodo(@RequestParam("title") String title) {
        if (title != null && !title.trim().isEmpty()) {
            TodoItem todo = new TodoItem();
            todo.setTitle(title.trim());
            todo.setCompleted(false);
            todoRepository.save(todo);
        }
        return "redirect:/";
    }

    // 3. Delete a Todo
    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        todoRepository.deleteById(id);
        return "redirect:/";
    }

    // 4. Toggle Complete/Incomplete
    @PostMapping("/toggle/{id}")
    public String toggleTodo(@PathVariable Long id) {
        todoRepository.findById(id).ifPresent(todo -> {
            todo.setCompleted(!todo.isCompleted());
            todoRepository.save(todo);
        });
        return "redirect:/";
    }
}