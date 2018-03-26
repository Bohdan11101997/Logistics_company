package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.OrderDraft;

import java.util.List;
import java.util.Optional;

public interface OrderDraftDao extends CrudDao<OrderDraft, Long> {
    List<OrderDraft> findByPersonId(Long personId);
}
