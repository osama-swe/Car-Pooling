package com.example.carpooling;

import java.util.Objects;

public class Route {
    String  routeId,from, to, time, price, date;
    public Route(){}

    public Route(String from, String to, String time, String price, String date) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.price = price;
        this.date = date;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Route route = (Route) obj;
        return Objects.equals(routeId, route.routeId);
    }


    @Override
    public int hashCode() {
        return Objects.hash(routeId);
    }


}
