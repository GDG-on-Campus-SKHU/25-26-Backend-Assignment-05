package com.gdg.todolist.dto;

import lombok.Getter;

@Getter
public class TodoListSaveRequestDto {
    private String title;
    private String description;
    private Long status;
}
