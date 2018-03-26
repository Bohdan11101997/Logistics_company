package edu.netcracker.project.logistic.dao;

import edu.netcracker.project.logistic.model.OrderDraft;

import java.util.List;

public interface OrderDraftDao extends CrudDao<OrderDraft, Long> {
    List<OrderDraft> findByPersonId(Long personId);
}
