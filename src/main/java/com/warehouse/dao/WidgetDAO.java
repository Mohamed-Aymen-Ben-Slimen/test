package com.warehouse.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.SessionFactory;

import com.warehouse.dto.WidgetDTO;
import com.warehouse.dto.WidgetMapper;
import com.warehouse.entity.Widget;

import io.dropwizard.hibernate.AbstractDAO;

public class WidgetDAO extends AbstractDAO<Widget> {

    private WidgetMapper widgetMapper;

    public WidgetDAO(SessionFactory sessionFactory, WidgetMapper widgetMapper) {
        super(sessionFactory);
        this.widgetMapper = widgetMapper;
    }

    public Widget create(WidgetDTO widgetDTO) {
        Widget widget = this.widgetMapper.toEntity(widgetDTO);
        return this.persist(widget);
    }

    public Widget update(Widget widget, WidgetDTO widgetDTO) {
        Widget updatedWidget = this.widgetMapper.updateEntityFromDTO(widget, widgetDTO);
        return this.persist(updatedWidget);
    }

    public Widget partialUpdate(Widget widget, WidgetDTO widgetDTO) {
        Widget updatedWidget = this.widgetMapper.partialUpdateEntityFromDTO(widget, widgetDTO);
        return this.persist(updatedWidget);
    }

    public List<Widget> findAll() {
        CriteriaQuery<Widget> criteriaQuery = this.criteriaQuery();
        Root<Widget> root = criteriaQuery.from(Widget.class);
        CriteriaQuery<Widget> criteriaQueryAll = criteriaQuery.select(root);

        return this.list(criteriaQueryAll);
    }

    public Optional<Widget> findById(long id) {
        return Optional.ofNullable(this.get(id));
    }

    public List<Widget> findAllByCategory(String category) {
        CriteriaBuilder criteriaBuilder = this.currentSession().getCriteriaBuilder();
        CriteriaQuery<Widget> criteriaQuery = this.criteriaQuery();
        Root<Widget> root = criteriaQuery.from(Widget.class);
        CriteriaQuery<Widget> criteriaQueryAll = criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("category"), category));

        return this.list(criteriaQueryAll);
    }

    public void delete(Widget widget) {
        this.currentSession().delete(widget);
    }
}
