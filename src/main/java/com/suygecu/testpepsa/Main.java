package com.suygecu.testpepsa;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        Task task0 = new Task("Моя пивасная программа","Лью пиво на свой код", LocalDate.of(2024, 9, 11));
        Task task1 = new Task("Моя пивасная программа","Лью пиво на свой код", LocalDate.of(2024, 9, 11));



        TaskManager taskManager = new TaskManager();


        taskManager.addTask(task0);
        taskManager.addTask(task1);

        taskManager.viewingTask();

        taskManager.removeTask(task1);
        taskManager.viewingTask();




    }




}
