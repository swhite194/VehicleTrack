package com.example.swhit.vehicletracking;

public class Customer extends User{

    public String address;
    public String city;
    public String postcode;
//https://stackoverflow.com/questions/47706601/users-does-not-define-no-argument-constructor
    //avoids error that says it does not define a no-argument constructor
    //also means i dont need to call the values in UserInfo at the start when declaring Customer
    public Customer(){
        //
    }
//https://www.geeksforgeeks.org/inheritance-in-java/https://www.geeksforgeeks.org/inheritance-in-java/https://www.geeksforgeeks.org/inheritance-in-java/
    public Customer(String name, String email, double latitude, double longitude, String address, String city, String postcode){
        //is that fine, or needs this?
        super(name, email, latitude, longitude);
        this.address = address;
        this.city = city;
        this.postcode = postcode;

    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
