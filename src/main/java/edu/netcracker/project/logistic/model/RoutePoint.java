package edu.netcracker.project.logistic.model;

public class RoutePoint {
    private String latitude;
    private String longitude;
    private DeliveryStatus status;
    private OrderDTO order;

    public RoutePoint() {
    }

    public RoutePoint(String latitude, String longitude, DeliveryStatus status, OrderDTO order) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.order = order;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public OrderDTO getOrder() {
        return order;
    }

    public void setOrder(OrderDTO order) {
        this.order = order;
    }
}
