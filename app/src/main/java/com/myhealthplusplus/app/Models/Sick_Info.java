package com.myhealthplusplus.app.Models;

public class Sick_Info {
    private String topic;
    private String sub_topic;

    public String getTopic() {
        return topic;
    }

    public String getSub_topic() {
        return sub_topic;
    }

    public Sick_Info(String topic, String sub_topic) {
        this.topic = topic;
        this.sub_topic = sub_topic;
    }
}
