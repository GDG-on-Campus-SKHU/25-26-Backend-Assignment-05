package com.gdg.todolist.repository;

import com.gdg.todolist.domain.TodoList;
import com.gdg.todolist.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList, Long> {
    List<TodoList> findByUser(User user);
    List<TodoList> findByUserAndTitleContainingIgnoreCase(User user, String title);
}
