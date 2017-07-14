package com.example.wx.apas;

/**
 * Created by clp on 2017/3/23.
 */

public class Data {
    private int question_id;
    private int assignment_id;

    private String title;
    private String title2;
    
    private String question_type;
    private String required_language;
    private int difficulty;
    private String content;
    private String code_template;
    private String solution;
    private String question_topic;
    private String submission_time;
    private String program_output;
    private boolean submitted;

    private String description;
    private String start_submission_time;
    private String end_submission_time;

    public int getId() {
        return question_id;
    }
    public void setId(String url) {
        String[] str = url.split("/");
        //int a=Integer.parseInt(s)
        int question_id = Integer.parseInt(str[5]);
        this.question_id = question_id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion_type() {
        return question_type;
    }
    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public String getRequired_language() {
        return required_language;
    }
    public void setRequired_language(String required_language) {
        this.required_language = required_language;
    }

    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getCodeTemplate() {
        return code_template;
    }
    public void setCodeTemplate(String code_template) {
        this.code_template = code_template;
    }

    public String getSolution() {
        return solution;
    }
    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getQuestion_topic() {
        return question_topic;
    }
    public void setQuestion_topic(String question_topic) {
        this.question_topic = question_topic;
    }

    public String getSubmission_time() {
        return submission_time;
    }
    public void setSubmission_time(String submission_time) {
        this.submission_time = submission_time;
    }
    public String getProgram_output() {
        return program_output;
    }
    public void setProgram_output(String program_output) {
        this.program_output = program_output;
    }


    public int getId2() {
        return assignment_id;
    }
    public void setId2(String url) {
        String[] str = url.split("/");
        //int a=Integer.parseInt(s)
        int assignment_id = Integer.parseInt(str[5]);
        this.assignment_id = assignment_id;
    }

    public String getTitle2() {
        return title2;
    }
    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /*public String getStart_submission_time() {
        return start_submission_time;
    }
    public void setStart_submission_time(String start_submission_time) {
        this.start_submission_time = start_submission_time;
    }*/

    public String getEnd_submission_time() {
        return end_submission_time;
    }
    public void setEnd_submission_time(String end_submission_time) {
        this.end_submission_time = end_submission_time;
    }

    public boolean getSubmitted() {
        return submitted;
    }
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    @Override
    public String toString() {
        return "Data [title=" + title + ", question_type=" + question_type + ", required_language="
                + required_language +", difficulty=" + difficulty +"]";
    }
}
