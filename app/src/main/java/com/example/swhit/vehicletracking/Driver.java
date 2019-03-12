package com.example.swhit.vehicletracking;

public class Driver extends User{

public boolean isEnroute;

    public Driver(){
        //
    }

//https://www.geeksforgeeks.org/inheritance-in-java/
public Driver(String name, String email, double latitude, double longitude, boolean isEnroute) {
    super(name, email, latitude, longitude);
    this.isEnroute = isEnroute;
}


    public boolean isEnroute() {
        return isEnroute;
    }

    public void setEnroute(boolean enroute) {
        isEnroute = enroute;
    }
}
