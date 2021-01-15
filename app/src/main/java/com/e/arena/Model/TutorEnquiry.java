package com.e.arena.Model;

public class TutorEnquiry {
    String name;
    String email;
    String phoneNumber;
    String studentName;
    String classs;
    String subject;
    String gender;
    String address;
    String fee;
    long timestamp;
    String city;
    String state;
    String locality;
    String subLocality;
    String uid;
    String status;

    public TutorEnquiry(String name, String email, String phoneNumber, String studentName, String classs, String subject, String gender, String address, String fee, long timestamp, String city, String state, String locality, String subLocality, String uid, String status) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.studentName = studentName;
        this.classs = classs;
        this.subject = subject;
        this.gender = gender;
        this.address = address;
        this.fee = fee;
        this.timestamp = timestamp;
        this.city = city;
        this.state = state;
        this.locality = locality;
        this.subLocality = subLocality;
        this.uid = uid;
        this.status = status;
    }

    public String getFee() {
        return fee;
    }

    public String getUid() {
        return uid;
    }

    public String getLocality() {
        return locality;
    }

    public String getSubLocality() {
        return subLocality;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getClasss() {
        return classs;
    }

    public String getSubject() {
        return subject;
    }

    public String getGender() {
        return gender;
    }
}
