package com.myhealthplusplus.app.Models;

public class Sick_Rows {

    private String topic, content;
    boolean expandable;

    public Sick_Rows(String topic, String content) {
        this.topic = topic;
        this.content = content;
        this.expandable = false;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Sick_Rows{" +
                "topic='" + topic + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
