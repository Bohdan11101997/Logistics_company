package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.CourierDataDao;
import edu.netcracker.project.logistic.model.*;
import edu.netcracker.project.logistic.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController("/courier/route")
public class RouteController {
    private CourierDataDao courierDataDao;
    private OrderService orderService;


    public RouteController(CourierDataDao courierDataDao, OrderService orderService) {
        this.courierDataDao = courierDataDao;
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Route> getRoute(Principal principal) {
        Optional<CourierData> data = courierDataDao.findOne(principal.getName());
        if (!data.isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        Route route = data.get().getRoute();
        return ResponseEntity.ok(route);
    }

    @PostMapping("/orders/confirm")
    public ResponseEntity confirmDelivery(@RequestBody RoutePoint routePoint, Principal principal) {
        CourierData data = courierDataDao.findOne(principal.getName())
                .orElseThrow(IllegalStateException::new);
        Route route = data.getRoute();
        List<RoutePoint> points = route.getWayPoints();
        points.removeIf(p -> p.getOrder().getId().equals(routePoint.getOrder().getId()));
        if (points.size() == 0) {
            data.setCourierStatus(CourierStatus.FREE);
            data.setRoute(null);
        }
        data.setLastLocation(String.format("%s,%s", routePoint.getLatitude(), routePoint.getLongitude()));
        data.setRoute(route);
        courierDataDao.save(data);
        orderService.confirmDelivered(data, routePoint.getOrder().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("orders/fail")
    public ResponseEntity cancelDelivery(@RequestBody RoutePoint routePoint, Principal principal) {
        CourierData data = courierDataDao.findOne(principal.getName())
                .orElseThrow(IllegalStateException::new);
        orderService.confirmFailed(data, routePoint.getOrder().getId());
        return ResponseEntity.ok().build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalStateException.class)
    public void handleInternalServerError() {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleBadRequest() {
    }
}
