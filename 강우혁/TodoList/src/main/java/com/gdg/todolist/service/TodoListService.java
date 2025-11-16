package com.gdg.todolist.service;

import com.gdg.todolist.domain.TodoList;
import com.gdg.todolist.domain.User;
import com.gdg.todolist.dto.TodoListInfoResponseDto;
import com.gdg.todolist.dto.TodoListSaveRequestDto;
import com.gdg.todolist.exception.TodoNotFoundException;
import com.gdg.todolist.exception.UserNotFoundException;
import com.gdg.todolist.repository.TodoListRepository;
import com.gdg.todolist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoListService {

    private final TodoListRepository todoListRepository;
    private final UserRepository userRepository;

    @Transactional
    public TodoListInfoResponseDto createTodoList(TodoListSaveRequestDto dto, Long userId) {
        User user = findUserById(userId);

        TodoList todoList = TodoList.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .user(user)
                .build();

        todoListRepository.save(todoList);
        return TodoListInfoResponseDto.from(todoList);
    }

    @Transactional(readOnly = true)
    public List<TodoListInfoResponseDto> getTodoLists(Long userId) {
        User user = findUserById(userId);
        return todoListRepository.findByUser(user).stream()
                .map(TodoListInfoResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TodoListInfoResponseDto> getTodoTitleLists(Long userId, String title) {
        User user = findUserById(userId);
        return todoListRepository.findByUserAndTitleContainingIgnoreCase(user, title).stream()
                .map(TodoListInfoResponseDto::from)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<TodoListInfoResponseDto> getTodoAllLists() {
        return todoListRepository.findAll().stream()
                .map(TodoListInfoResponseDto::from)
                .toList();
    }

    @Transactional
    public TodoListInfoResponseDto updateTodoList(Long todoId, TodoListSaveRequestDto dto) {
        TodoList todoList = todoListRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException("해당 Todo가 존재하지 않습니다."));

        todoList.update(dto.getTitle(), dto.getDescription(), dto.getStatus(), todoList.getUser());

        return TodoListInfoResponseDto.from(todoList);
    }

    @Transactional
    public void deleteTodoList(Long todoId) {
        TodoList todoList = todoListRepository.findById(todoId)
                .orElseThrow(() -> new TodoNotFoundException("해당 Todo가 존재하지 않습니다."));
        todoListRepository.delete(todoList);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자가 없습니다."));
    }
}
