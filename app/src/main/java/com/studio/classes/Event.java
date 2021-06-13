package com.studio.classes;

import java.sql.Date;

public class Event {
    private String name;
    private String kind;
    private Date date;
    private String id;
    private String location;
    private String combineId;

    public Event(String id, String name, String kind, Date date, String location, String combineId) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.date = date;
        this.location = location;
        this.combineId = combineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCombineId() {
        return combineId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCombineId(String combineId) {
        this.combineId = combineId;
    }
}
