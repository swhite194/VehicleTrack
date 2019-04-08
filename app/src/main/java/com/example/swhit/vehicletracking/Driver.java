package com.example.swhit.vehicletracking;

public class Driver extends User{

public boolean isEnroute;
public boolean bookable;

    public Driver(){
        //
    }

//https://www.geeksforgeeks.org/inheritance-in-java/
public Driver(String name, String email, double latitude, double longitude, boolean isEnroute, boolean bookable) {
    super(name, email, latitude, longitude);
    this.isEnroute = isEnroute;
    this.bookable = bookable;
}


    public boolean isEnroute() {
        return isEnroute;
    }

    public void setEnroute(boolean enroute) {
        isEnroute = enroute;
    }

    public boolean isBookable() {
        return bookable;
    }

    public void setBookable(boolean bookable) {
        this.bookable = bookable;
    }
}
