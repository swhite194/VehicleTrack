package com.example.swhit.vehicletracking;

public class Driver extends User{

public boolean isEnroute;

public Driver(boolean isEnroute) {
    this.isEnroute = isEnroute;
}

    public boolean isEnroute() {
        return isEnroute;
    }

    public void setEnroute(boolean enroute) {
        isEnroute = enroute;
    }
}
