package com.lms.model;

public class Material {
    private int materialId;
    private int courseId;
    private String title;
    private String filePath;
    private String fileType;

    public Material() {}

    public Material(int materialId, int courseId, String title, String filePath, String fileType) {
        this.materialId = materialId;
        this.courseId = courseId;
        this.title = title;
        this.filePath = filePath;
        this.fileType = fileType;
    }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    @Override
    public String toString() { return title + " (" + fileType + ")"; }
}
