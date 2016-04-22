package com.mobmedianet.trackergps.Project.Objects;

/**
 * Created by Cesar on 9/9/2015.
 */
public class AlertsCommentObject {
    String title, date, comment;

    public AlertsCommentObject(String title, String date, String comment) {
        this.title = title;
        this.date = date;
        this.comment = comment;
    }

    public AlertsCommentObject() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }
}
