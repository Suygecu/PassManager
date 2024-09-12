package com.suygecu.testpepsa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private ArrayList<Task> tasks = new ArrayList<>();


    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        if (tasks.contains(task)) {
            tasks.remove(task);
        } else System.out.println("Задача отсутствует. ");

    }

    public List<Task> filterTaskByDate(LocalDate date) {
        List<Task> filterTask = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDueData().equals(date)) {
                filterTask.add(task);
            }

        }
        return filterTask;

    }

    public List<Task> filterTaskByTitle(String title) {
        List<Task> filteredTasksByTitle = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getTitle().contains(title)) {
                filteredTasksByTitle.add(task);
            }
        }

        return filteredTasksByTitle;
    }

    public void viewingTaskFilteredTasks(String title, LocalDate date) {
        List<Task> filteredByTitle = filterTaskByTitle(title);
        List<Task> filteredByDate = filterTaskByDate(date);

        if (!filteredByTitle.isEmpty()) {
            System.out.println("Задачи отсортированы по заголовку: ");

            for (Task task : filteredByTitle) {
                System.out.printf("Title: %-20s | Description: %-25s | Date: %s",
                        task.getTitle(),
                        task.getDescription(),
                        task.getDueData());
                System.out.println();
            }
        }
        else System.out.println("Задача не найдена. ");

        if (!filteredByDate.isEmpty()) {
            System.out.println("Задачи отсортированы по дате: ");
            for (Task task : filteredByDate) {
                System.out.printf("Title: %-20s | Description: %-25s | Date: %s",
                        task.getTitle(),
                        task.getDescription(),
                        task.getDueData());
                System.out.println();
            }
        }
        else System.out.println("Задача не найдена. ");


    }

    public void viewingTask() {
        for (Task task : tasks) {
            System.out.printf("Title: %-20s | Description: %-25s | Date: %s",
                    task.getTitle(),
                    task.getDescription(),
                    task.getDueData());
            System.out.println();
        }
    }
}
