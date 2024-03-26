package com.warehouse.resources;

import com.codahale.metrics.annotation.Timed;
import com.github.benmanes.caffeine.cache.Cache;
import com.warehouse.dao.WidgetDAO;
import com.warehouse.dto.WidgetDTO;
import com.warehouse.entity.Widget;
import com.warehouse.representation.CategoryInfo;

import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.List;
import java.util.Optional;

@Path("/widgets")
@Api("/widgets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WidgetResource {
    private WidgetDAO widgetDAO;
    private Cache<Long, Widget> cache;

    public WidgetResource(WidgetDAO widgetDAO, Cache<Long, Widget> cache) {
        this.widgetDAO = widgetDAO;
        this.cache = cache;
    }

    @GET
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Get wigets", httpMethod = "GET", response = Widget.class, responseContainer = "List")
    public List<Widget> listWidgets(@QueryParam("category") Optional<String> category) {
        System.out.println("category present" + category.isPresent());
        if (category.isPresent()) {
            return widgetDAO.findAllByCategory(category.get());
        }
        return widgetDAO.findAll();
    }

    @Path("/{id}")
    @GET
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Get a wiget by id", httpMethod = "GET", response = Widget.class)
    public Widget getWidgetById(@PathParam("id") Long id) {
        Widget cachedWidget = this.cache.getIfPresent(id);
        if (cachedWidget != null) {
            // Return cached widget if present
            return cachedWidget;
        }

        Widget widget = widgetDAO.findById(id)
                .orElseThrow(() -> new WebApplicationException("Widget not found", Response.Status.NOT_FOUND));

        // Cache the widget
        this.cache.put(id, widget);
        return widget;
    }

    @POST
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Create a new widget", httpMethod = "POST", response = Widget.class)
    public Widget createNewWidget(@Valid WidgetDTO widget) {
        Widget createdWidget = widgetDAO.create(widget);

        this.cache.put(createdWidget.getId(), createdWidget);
        return createdWidget;
    }

    @Path("/{id}")
    @PUT
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Update a widget", httpMethod = "PUT", response = Widget.class)
    public Widget putWidgetById(@PathParam("id") Long id, @Valid WidgetDTO widget) {
        Widget foundWidget = widgetDAO.findById(id)
                .orElseThrow(() -> new WebApplicationException("Widget not found", Status.NOT_FOUND));

        Widget updatedWidget = this.widgetDAO.update(foundWidget, widget);

        this.cache.put(id, updatedWidget);
        return updatedWidget;
    }

    @Path("/{id}")
    @PATCH
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Partial update of a widget", httpMethod = "PATCH", response = Widget.class)
    public Widget patchWidgetById(@PathParam("id") Long id, WidgetDTO widgetDTO) {
        Widget foundWidget = widgetDAO.findById(id)
                .orElseThrow(() -> new WebApplicationException("Widget not found", Status.NOT_FOUND));

        Widget updatedWidget = this.widgetDAO.partialUpdate(foundWidget, widgetDTO);

        this.cache.put(id, updatedWidget);
        return updatedWidget;
    }

    @Path("/{id}")
    @DELETE
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Delete a widget by ID", httpMethod = "DELETE")
    public Response patchWidgetById(@PathParam("id") Long id) {
        Widget foundWidget = widgetDAO.findById(id)
                .orElseThrow(() -> new WebApplicationException("Widget not found", Status.NOT_FOUND));

        this.widgetDAO.delete(foundWidget);

        this.cache.invalidate(id);
        return Response.ok().build();
    }

    @Path("/info/{category}")
    @GET
    @Timed
    @UnitOfWork
    @ApiOperation(value = "Calculate the total price for a category", httpMethod = "GET", response = CategoryInfo.class)
    public CategoryInfo getPriceForCategory(@PathParam("category") String category) {
        List<Widget> widgets = this.widgetDAO.findAllByCategory(category);
        Double sum = 0d;
        for (Widget widget : widgets) {
            sum += widget.getPrice();
        }

        CategoryInfo response = new CategoryInfo();
        response.setCategory(category);
        response.setTotalPrice(sum);
        response.setNumberOfWidgets(widgets.size());

        return response;
    }

}