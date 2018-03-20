package edu.netcracker.project.logistic.controllers;

import edu.netcracker.project.logistic.dao.CourierDataDao;
import edu.netcracker.project.logistic.model.CourierData;
import edu.netcracker.project.logistic.model.Route;
import edu.netcracker.project.logistic.model.RoutePoint;
import edu.netcracker.project.logistic.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/courier/route/orders")
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

    @PostMapping("{orderId}/confirm")
    public ResponseEntity confirmDelivery(@PathVariable Long orderId, Principal principal) {
        CourierData data = courierDataDao.findOne(principal.getName())
                .orElseThrow(IllegalStateException::new);
        orderService.confirmDelivered(data, orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{orderId}/fail")
    public ResponseEntity cancelDelivery(@PathVariable Long orderId, Principal principal) {
        CourierData data = courierDataDao.findOne(principal.getName())
                .orElseThrow(IllegalStateException::new);
        orderService.confirmFailed(data, orderId);
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
