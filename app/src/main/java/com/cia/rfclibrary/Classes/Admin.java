package com.cia.rfclibrary.Classes;

public class Admin {

    private String username, session, clearance;


    public Admin(){
        // empty constructor
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void setClearance(String clearance) {
        this.clearance = clearance;
    }

    public String getUsername() {
        return username;
    }

    public String getSession() {
        return session;
    }

    public String getClearance() {
        return clearance;
    }
}
