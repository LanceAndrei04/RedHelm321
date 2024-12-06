package com.example.redhelm321;

public class HotlineItem {
    private String id;
    private String name;
    private String number;
    private String description;

    public HotlineItem() {
        // Required empty constructor for Firebase
    }

    public HotlineItem(String id, String name, String number, String description) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
