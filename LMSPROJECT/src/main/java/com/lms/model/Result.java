package com.lms.model;

public class Result {
    private int resultId;
    private int studentId;
    private int quizId;
    private String quizTitle;
    private int score;

    public Result() {}

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
