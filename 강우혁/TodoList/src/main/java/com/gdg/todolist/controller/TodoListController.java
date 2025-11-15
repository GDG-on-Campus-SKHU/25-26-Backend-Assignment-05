package com.gdg.todolist.controller;

import com.gdg.todolist.dto.LocalUserInfoDto;
import com.gdg.todolist.dto.LocalUserSignUpDto;
import com.gdg.todolist.dto.TodoListInfoResponseDto;
import com.gdg.todolist.dto.TodoListSaveRequestDto;
import com.gdg.todolist.service.TodoListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoListController {

    private final TodoListService todoListService;

    @PostMapping("/create/{id}")
    public ResponseEntity<TodoListInfoResponseDto> createTodo(
            @PathVariable Long id,
            @RequestBody TodoListSaveRequestDto dto
    ) {
        TodoListInfoResponseDto response = todoListService.createTodoList(dto, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<List<TodoListInfoResponseDto>> readTodo(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(todoListService.getTodoLists(id));
    }

    @GetMapping("/search/userid/{id}/title/{title}")
    public ResponseEntity<List<TodoListInfoResponseDto>> search(
            @PathVariable Long id,
            @PathVariable String title
    ){
        return ResponseEntity.status(HttpStatus.OK).body(todoListService.getTodoTitleLists(id, title));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<TodoListInfoResponseDto> updateTodo(@PathVariable Long id, @RequestBody TodoListSaveRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(todoListService.updateTodoList(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<TodoListInfoResponseDto> deleteTodo(@PathVariable Long id) {
        todoListService.deleteTodoList(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
