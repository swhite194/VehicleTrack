package com.example.swhit.vehicletracking;

public class Driver extends User {

    public boolean enroute;
    public String bookable;
    int speed;

    public Driver() {
        //
    }

    //https://www.geeksforgeeks.org/inheritance-in-java/
    public Driver(String name, String email, String phoneNumber, double latitude, double longitude, boolean enroute, String bookable, int speed) {
        super(name, email, phoneNumber, latitude, longitude);
        this.enroute = enroute;
        this.bookable = bookable;
        this.speed = speed;
    }

    public boolean isEnroute() {
        return enroute;
    }

    public void setEnroute(boolean enroute) {
        this.enroute = enroute;
    }

    public String getBookable() {
        return bookable;
    }

    public void setBookable(String bookable) {
        this.bookable = bookable;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}



