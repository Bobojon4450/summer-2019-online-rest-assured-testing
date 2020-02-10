package com.automation.pojos;

public class Room {

    //if you don't want to serialize some property use 'transient'
    // from POJO -> to JSON it will not have id
   private transient int id;
    private String name;
    private String description;
    private String capacity;
    private String withTV;
    private String withWhiteBoard;

    public Room(){}

    public Room(String name, String description, String capacity, String withTV, String withWhiteBoard) {
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.withTV = withTV;
        this.withWhiteBoard = withWhiteBoard;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getWithTV() {
        return withTV;
    }

    public void setWithTV(String withTV) {
        this.withTV = withTV;
    }

    public String getWithWhiteBoard() {
        return withWhiteBoard;
    }

    public void setWithWhiteBoard(String withWhiteBoard) {
        this.withWhiteBoard = withWhiteBoard;

    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", capacity='" + capacity + '\'' +
                ", withTV='" + withTV + '\'' +
                ", withWhiteBoard='" + withWhiteBoard + '\'' +
                '}';
    }
}
