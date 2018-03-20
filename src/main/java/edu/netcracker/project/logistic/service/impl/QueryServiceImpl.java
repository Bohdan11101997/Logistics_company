package edu.netcracker.project.logistic.service.impl;

import edu.netcracker.project.logistic.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:queries.properties")
public class QueryServiceImpl implements QueryService {

    @Autowired
    Environment environment;

    @Override
    public String getQuery(String queryName) {
        return environment.getRequiredProperty(queryName);
    }
}
