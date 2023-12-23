package com.example.driver;

public class Route {
    String  routeId, from, to, time, date, price, driverId, status;

    public Route(){}

    public Route(String from, String to, String time, String date, String price, String driverId) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.date = date;
        this.price = price;
        this.driverId = driverId;
        status = "scheduled";
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
    public void setDriverId(String routeId) {
        this.driverId = driverId;
    }


}
