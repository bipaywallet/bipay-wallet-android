package com.spark.bipaywallet.entity;


import java.io.Serializable;

public class HttpNew implements Serializable {

//    "id": "4727",
//            "title": "火币全球站支持VEN ERC20旧币兑换成VET新币",
//            "content": "火币全球站公告称，火币全球站支持VEN ERC20旧币兑换成VET新币，并将于新加坡时间8月15日11:00开放VET新币充值，用户可自行将VEN旧币兑换成VET新币，兑换比例为VEN: VET=1:100。",
//            "create_time": "2018-08-15 10:44:07",
//            "grade": "0",
//            "up_counts": "0",
//            "down_counts": "0"

    private String id;
    private String title;
    private String content;
    private String create_time;
    private String grade;
    private String up_counts;
    private String down_counts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getUp_counts() {
        return up_counts;
    }

    public void setUp_counts(String up_counts) {
        this.up_counts = up_counts;
    }

    public String getDown_counts() {
        return down_counts;
    }

    public void setDown_counts(String down_counts) {
        this.down_counts = down_counts;
    }
}
