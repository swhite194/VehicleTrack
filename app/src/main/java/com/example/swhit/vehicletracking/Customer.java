package com.example.swhit.vehicletracking;

public class Customer extends User{

    public String address;
//https://stackoverflow.com/questions/47706601/users-does-not-define-no-argument-constructor
    //avoids error that says it does not define a no-argument constructor
    //also means i dont need to call the values in UserInfo at the start when declaring Customer
    public Customer(){
        //
    }
//https://www.geeksforgeeks.org/inheritance-in-java/https://www.geeksforgeeks.org/inheritance-in-java/https://www.geeksforgeeks.org/inheritance-in-java/
    public Customer(String name, String email, double latitude, double longitude, String address){
        //is that fine, or needs this?
        super(name, email, latitude, longitude);
        this.address = address;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
