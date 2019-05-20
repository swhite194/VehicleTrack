package com.example.swhit.vehicletracking;

import java.util.Date;

public class Order {

    public String id;

    public String customerID;
    public String customerName;
    public String customerEmail;
    public String customerAddress;
    public String customerCity;
    public String customerPostcode;

    public String driverID;
    public String driverName;
    public boolean driverEnroute;

    public String itemID;
    public int itemQuantity;

    public String deliveryRequestedForDate;
    public String deliveryRequestedForTime;
    public String deliveredTime;



    public Order(){
        //
    }

    public Order(String customerID, String customerName, String customerEmail, String customerAddress, String customerCity, String customerPostcode, String driverID, String driverName, boolean driverEnroute, String itemID, int itemQuantity, String deliveryRequestedForDate, String deliveryRequestedForTime, String deliveredTime) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.customerCity = customerCity;
        this.customerPostcode = customerPostcode;
        this.driverID = driverID;
        this.driverName = driverName;
        this.driverEnroute = driverEnroute;
        this.itemID = itemID;
        this.itemQuantity = itemQuantity;

        this.deliveryRequestedForDate = deliveryRequestedForDate;
        this.deliveryRequestedForTime = deliveryRequestedForTime;
        this.deliveredTime = deliveredTime;
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

    public String getCustomerPostcode() {
        return customerPostcode;
    }

    public void setCustomerPostcode(String customerPostcode) {
        this.customerPostcode = customerPostcode;
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

    public boolean isDriverEnroute() {
        return driverEnroute;
    }

    public void setDriverEnroute(boolean driverEnroute) {
        this.driverEnroute = driverEnroute;
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

    public String getDeliveryRequestedForDate() {
        return deliveryRequestedForDate;
    }

    public void setDeliveryRequestedForDate(String deliveryRequestedForDate) {
        this.deliveryRequestedForDate = deliveryRequestedForDate;
    }

    public String getDeliveryRequestedForTime() {
        return deliveryRequestedForTime;
    }

    public void setDeliveryRequestedForTime(String deliveryRequestedForTime) {
        this.deliveryRequestedForTime = deliveryRequestedForTime;
    }

    public String getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(String deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



}

