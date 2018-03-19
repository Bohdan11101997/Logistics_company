package edu.netcracker.project.logistic.model;

import java.util.List;

public class Route {
    private String mapUrl;
    private List<RoutePoint> wayPoints;

    public Route() {
    }

    public Route(String mapUrl, List<RoutePoint> wayPoints) {
        this.mapUrl = mapUrl;
        this.wayPoints = wayPoints;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public List<RoutePoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<RoutePoint> wayPoints) {
        this.wayPoints = wayPoints;
    }
}
