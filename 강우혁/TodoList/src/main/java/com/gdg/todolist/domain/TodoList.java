package com.gdg.todolist.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "todo")
public class TodoList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TODO_TITLE", nullable = false)
    private String title;

    @Column(name = "TODO_DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "TODO_STATUS", nullable = false)
    private Long status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public TodoList(String title, String description, Long status, User user) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.user = user;
    }

    public void update(String title, String description, Long status, User user) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.user = user;
    }
}
