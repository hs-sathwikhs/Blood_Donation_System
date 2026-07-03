package com.blooddonation.model;

public abstract class User {
    protected String name;
    protected String email;
    protected String password;

    public abstract void login();
    public abstract void logout();

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
