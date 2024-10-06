package com.suygecu.testpepsa.client;

import com.suygecu.testpepsa.InSorrowPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static com.suygecu.testpepsa.client.TaskMangerApp.saveNewTaskToDatabase;

public class TaskPacket extends InSorrowPacket {


    private static BlockingDeque<Task> taskQueue = new LinkedBlockingDeque<>();
    private String title;
    private String description;
    private String date;

    public TaskPacket(String date, String title, String description) {
        this.date = date;
        this.title = title;
        this.description = description;
    }

    public TaskPacket(){
    toString();
    }

    @Override
    public String toString() {
        return "Задача: " + title + ", Описание: " + description + ", Дата: " + date;
    }


    @Override
    public void writePacket(DataOutput output) throws IOException {
        output.writeUTF(title);
        output.writeUTF(description);
        if (date != null) {
            output.writeUTF(date);
        } else {
            output.writeUTF("");
        }
    }


    @Override
    public void readPacket(DataInput input) throws IOException {
        title = input.readUTF();
        description = input.readUTF();
        date = input.readUTF();
    }


    @Override
    public void processPacket() {
        Task task = new Task(title, description, LocalDate.parse(date));
        try {
            taskQueue.put(task);
            System.out.println("Задача добавлена в очередь: " + task);

            saveNewTaskToDatabase(task);
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }

    }
