package com.suygecu.testpepsa.client;

import com.suygecu.testpepsa.InSorrowPacket;
import com.suygecu.testpepsa.server.ServerConnect;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.LocalDate;

public class TaskPacket extends InSorrowPacket {



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
            ServerConnect.taskQueue.put(task);
            System.out.println("Задача добавлена в очередь: " + task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    }
