package com.example.swhit.vehicletracking;

public class Order {

    public String id;

    public String customerID;
    public String customerName;
    public String customerEmail;
    public String customerAddress;
    public String customerCity;
    public String customerPostode;

    public String driverID;
    public String driverName;

    public int itemID;
    public int itemQuantity;

    public Order(){
        //
    }

    public Order(String customerID, String customerName, String customerEmail, String customerAddress, String customerCity, String customerPostode, String driverID, String driverName, int itemID, int itemQuantity) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerCity = customerCity;
        this.customerPostode = customerPostode;
        this.driverID = driverID;
        this.driverName = driverName;
        this.itemID = itemID;
        this.itemQuantity = itemQuantity;
    }



    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerPostode() {
        return customerPostode;
    }

    public void setCustomerPostode(String customerPostode) {
        this.customerPostode = customerPostode;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }






}

