package com.suygecu.testpepsa.client;

import java.time.LocalDate;
import java.util.Objects;

public class Task{


    private int id;
    private String title;
    private String description;
    private LocalDate date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                Objects.equals(date, task.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, date);
    }


    public Task(String title, String description, LocalDate date) {
        this(-1, title, description, date);
    }



    public Task(int id, String title, String description, LocalDate date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date !=null ? date : LocalDate.now();
    }

    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }



    public LocalDate getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Задача: " + title + ", Дата: " + date;
    }
}
