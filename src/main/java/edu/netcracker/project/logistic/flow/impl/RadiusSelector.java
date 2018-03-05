package edu.netcracker.project.logistic.flow.impl;

import edu.com.google.maps.StaticMap;
import edu.com.google.maps.model.LatLng;
import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.model.Order;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RadiusSelector extends FlowBuilderImpl {

    @Autowired
    public RadiusSelector(RoleCrudDao roleCrudDao) {
        super(roleCrudDao);
    }

    @Override
    public List<Order> calculatePath() {
        return null;
    }

    @Override
    public List<Order> confirmCourier() {
        return null;
    }

    @Override
    public List<Order> getOrdersSequence() {
        return null;
    }

    @Override
    public List<LatLng> getPath() {
        return null;
    }

    @Override
    public StaticMap getStaticMap() {
        return null;
    }

    @Override
    public void process() {

    }


}
