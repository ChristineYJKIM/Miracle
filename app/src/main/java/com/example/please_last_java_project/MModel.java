package com.example.please_last_java_project;

public class MModel {

    private String task, description, id, date, date1, date2;

    public MModel(){

    }



    public MModel(String task, String description, String id, String date, String date1, String date2){

        this.task = task;
        this.description = description;
        this.id = id;
        this.date = date;
        this.date1 = date1;
        this.date2 = date2;




    }

    public String getTask(){
        return task;
    }

    public void setTask(String task){
        this.task = task;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}

    public String getDate() { return date;}

    public String getDate1() { return date1;}

    public String getDate2() { return date2;}


}
