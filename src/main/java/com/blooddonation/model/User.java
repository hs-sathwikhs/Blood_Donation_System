package com.blooddonation.model;

public abstract class User {
    public abstract String getName();
    public abstract String getEmail();
    public abstract String getPassword();
    public abstract void setName(String name);
    public abstract void setEmail(String email);
    public abstract void setPassword(String password);
}
