package com.gdg.todolist.dto;

import com.gdg.todolist.domain.TodoList;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoListInfoResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private Long status;

    @Builder
    public TodoListInfoResponseDto(Long id, Long userId, String title, String description, Long status) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public static TodoListInfoResponseDto from(TodoList todoList) {
        return TodoListInfoResponseDto.builder()
                .id(todoList.getId())
                .userId(todoList.getUser().getId())
                .title(todoList.getTitle())
                .description(todoList.getDescription())
                .status(todoList.getStatus())
                .build();
    }
}
