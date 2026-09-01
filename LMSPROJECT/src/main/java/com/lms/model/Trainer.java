package com.lms.model;

public class Trainer {
    private int trainerId;
    private String name;
    private String email;
    private String expertise;
    private int addedBy;

    public Trainer() {}

    public Trainer(int trainerId, String name, String email, String expertise) {
        this.trainerId = trainerId;
        this.name = name;
        this.email = email;
        this.expertise = expertise;
    }

    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getExpertise() { return expertise; }
    public void setExpertise(String expertise) { this.expertise = expertise; }
    public int getAddedBy() { return addedBy; }
    public void setAddedBy(int addedBy) { this.addedBy = addedBy; }

    @Override
    public String toString() { return name + " (" + expertise + ")"; }
}
