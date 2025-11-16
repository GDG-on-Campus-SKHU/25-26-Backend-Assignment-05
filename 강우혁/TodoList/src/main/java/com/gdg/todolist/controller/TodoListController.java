package com.gdg.todolist.controller;

import com.gdg.todolist.dto.TodoListInfoResponseDto;
import com.gdg.todolist.dto.TodoListSaveRequestDto;
import com.gdg.todolist.service.TodoListService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Todo 생성", description = "사용자의 Todo를 생성")
    @PostMapping("/create/{userId}")
    public ResponseEntity<TodoListInfoResponseDto> createTodo(
            @PathVariable Long userId,
            @RequestBody TodoListSaveRequestDto dto
    ) {
        TodoListInfoResponseDto response = todoListService.createTodoList(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Todo 조회", description = "사용자의 Todo를 조회")
    @GetMapping("/read/{userId}")
    public ResponseEntity<List<TodoListInfoResponseDto>> readTodo(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(todoListService.getTodoLists(userId));
    }

    @Operation(summary = "Todo 검색", description = "사용자의 Todo를 검색")
    @GetMapping("/search/userid/{userId}/title/{title}")
    public ResponseEntity<List<TodoListInfoResponseDto>> search(
            @PathVariable Long userId,
            @PathVariable String title
    ){
        return ResponseEntity.status(HttpStatus.OK).body(todoListService.getTodoTitleLists(userId, title));
    }

    @Operation(summary = "Todo 수정", description = "사용자의 Todo를 수정")
    @PatchMapping("/update/{todoId}")
    public ResponseEntity<TodoListInfoResponseDto> updateTodo(@PathVariable Long todoId, @RequestBody TodoListSaveRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(todoListService.updateTodoList(todoId, dto));
    }

    @Operation(summary = "Todo 삭제", description = "사용자의 Todo를 삭제")
    @DeleteMapping("/delete/{todoId}")
    public ResponseEntity<TodoListInfoResponseDto> deleteTodo(@PathVariable Long todoId) {
        todoListService.deleteTodoList(todoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
