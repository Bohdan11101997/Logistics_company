package edu.netcracker.project.logistic.formatter;

import edu.netcracker.project.logistic.dao.OrderTypeDao;
import edu.netcracker.project.logistic.model.OrderType;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class OrderTypeFormatter implements Formatter<OrderType> {
    private OrderTypeDao orderTypeDao;

    public OrderTypeFormatter(OrderTypeDao orderTypeDao) {
        this.orderTypeDao = orderTypeDao;
    }

    @Override
    public OrderType parse(String text, Locale locale) throws ParseException {
        try {
            return orderTypeDao.findOne(Long.parseLong(text))
                    .orElseThrow(() -> new ParseException("Invalid order type", 0));
        } catch (NumberFormatException ex) {
            throw new ParseException("Invalid order type", 0);
        }
    }

    @Override
    public String print(OrderType orderType, Locale locale) {
        return orderType.getName();
    }
}
