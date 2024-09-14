package com.suygecu.testpepsa;

import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDate date;

    // Конструктор для новой задачи без id
    public Task(String title, String description, LocalDate date) {
        this(-1, title, description, date); // id будет -1 для новых задач
    }

    // Конструктор с id для загрузки задач из базы данных
    public Task(int id, String title, String description, LocalDate date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Задача: " + title + ", Дата: " + date;
    }
}
