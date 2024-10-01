package com.suygecu.testpepsa;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaskManager {

    private ObservableList<Task> tasks = FXCollections.observableArrayList();


    public ObservableList<Task> getObservableTasks() {
        return tasks;
    }


    public void addTask(Task task) {
        tasks.add(task);

    }
}
