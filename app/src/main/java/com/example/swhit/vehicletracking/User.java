package com.example.swhit.vehicletracking;

public class User {




    //id: https://www.quora.com/How-do-I-register-a-users-Detail-in-firebase
    public String id;

    //   //andy gave me idea to do this but also
    //    //https://firebase.google.com/docs/database/android/read-and-write

    //HOW HEAVILY HAS THAT FIREBASE THING BEEN LOOKED AT? IT TALKS ABOUT USERID and idk where its really from, the quora might be of relevance
    //but honestly im just copying and pasting the stuff ive got up/looking at other examples so idk.

    //except im not even using it right because im saying (Long.class) when im reading in data not even (User.class)
    //    //and other sources have said
    //    //https://stackoverflow.com/questions/50114944/why-we-need-an-empty-constructor-to-passing-save-a-data-from-firebase
    //    //not sure that that really gave me the idea, i mean it was mainly andy and im going blindly off that

    //I DONT UNDERSTAND HOW getId works, its not in the constructor... get calling it always works.

    public String name;
    public String email;
    public double latitude;
    public double longitude;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public User(){
        //
    }

//    public User(String email, double longitude, double latitude){
//        this.email = email;
//        this.longitude = longitude;
//        this.latitude = latitude;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //leaving id out?
    public User(String name, String email, double latitude, double longitude) {
        this.name = name;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}
