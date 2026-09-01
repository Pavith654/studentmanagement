package com.lms.model;

public class Course {
    private int courseId;
    private String title;
    private String description;
    private int trainerId;
    private String trainerName; // convenience field for display
    private String duration;
    private int createdBy;

    public Course() {}

    public Course(int courseId, String title, String description, int trainerId, String duration) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.trainerId = trainerId;
        this.duration = duration;
    }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getTrainerId() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId = trainerId; }
    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    @Override
    public String toString() { return title; }
}
