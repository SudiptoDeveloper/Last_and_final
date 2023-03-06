package com.abd.lastandfinal;


public class Students {

    String id;
    String name;
    String rollno;

    String mobileNo;
    String email;
    String course;

    public Students() {
    }

    public Students(String id,String name, String rollno,String mobileNo, String email, String course) {
        this.id = id;
        this.name = name;
        this.rollno = rollno;
        this.mobileNo = mobileNo;
        this.email = email;
        this.course = course;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRollno() {
        return rollno;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }
}