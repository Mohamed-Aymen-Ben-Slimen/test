package com.warehouse.resources;

import com.github.benmanes.caffeine.cache.Cache;
import com.warehouse.dao.WidgetDAO;
import com.warehouse.dto.WidgetDTO;
import com.warehouse.entity.Widget;

import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DropwizardExtensionsSupport.class)
public class WidgetResourceTest {

    private static final WidgetDAO widgetDAO = mock(WidgetDAO.class);
    private static final Cache<Long, Widget> cache = mock(Cache.class);

    private static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(new WidgetResource(widgetDAO, cache))
            .build();

    private Widget widget;

    @BeforeEach
    public void setup() {
        this.widget = new Widget();
        this.widget.setId(1L);
        this.widget.setName("Harry Potter and the Philosopher's Stone");
        this.widget.setCategory("Book");
        this.widget.setPrice(9.60);
    }

    @AfterEach
    public void tearDown() {
        reset(widgetDAO);
    }

    @Test
    @DisplayName("Should return the right widget by ID")
    public void getWidgetSuccessTest() {
        when(widgetDAO.findById(1L)).thenReturn(Optional.of(this.widget));

        Widget foundWidget = EXT.target("/widgets/1").request().get(Widget.class);

        assertThat(foundWidget.getId()).isEqualTo(this.widget.getId());
        assertThat(foundWidget.getName()).isEqualTo(this.widget.getName());

        verify(widgetDAO).findById(1L);
    }

    @Test
    @DisplayName("Should return not-found error for a widget ID that doesn't exist")
    public void getWidgetNotFoundTest() {
        when(widgetDAO.findById(2L)).thenReturn(Optional.empty());
        final Response response = EXT.target("/widgets/2").request().get();

        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        verify(widgetDAO).findById(2L);
    }

    @Test
    @DisplayName("Should return the list of widgets")
    public void listWidgetsTest() {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(widget);

        when(widgetDAO.findAll()).thenReturn(widgets);

        List<Widget> foundWidgets = EXT.target("/widgets").request().get(new GenericType<List<Widget>>() {
        });

        assertThat(foundWidgets).hasSize(1);
        assertThat(foundWidgets.get(0).getId()).isEqualTo(widget.getId());

        verify(widgetDAO).findAll();
    }

    @Test
    @DisplayName("Should return widgets in requested category")
    public void listWidgetsByCategoryTest() {

        List<Widget> widgets = new ArrayList<>();
        widgets.add(widget);

        when(widgetDAO.findAllByCategory(eq("Book"))).thenReturn(widgets);

        List<Widget> foundWidgets = EXT.target("/widgets").queryParam("category", "Book").request()
                .get(new GenericType<List<Widget>>() {
                });

        assertThat(foundWidgets).hasSize(1);
        assertThat(foundWidgets.get(0).getId()).isEqualTo(widget.getId());

        verify(widgetDAO).findAllByCategory(eq("Book"));
    }

    @Test
    @DisplayName("Should create a new widget")
    public void createNewWidgetTest() {
        WidgetDTO widgetDTO = new WidgetDTO();
        widgetDTO.setName("Harry Potter and the Philosopher's Stone");
        widgetDTO.setCategory("Book");
        widgetDTO.setPrice(9.60);

        when(widgetDAO.create(any(WidgetDTO.class))).thenReturn(widget);

        Widget createdWidget = EXT.target("/widgets").request().post(Entity.json(widgetDTO), Widget.class);

        assertThat(createdWidget.getId()).isEqualTo(widget.getId());
        assertThat(createdWidget.getName()).isEqualTo(widget.getName());

        verify(widgetDAO).create(any(WidgetDTO.class));
    }

    @Test
    @DisplayName("Should return an error if data is missing a filed")
    public void createNewWidgetWithMissingFiledTest() {
        // The field "price" is missing in DTO
        WidgetDTO widgetDTO = new WidgetDTO();
        widgetDTO.setName("Harry Potter and the Philosopher's Stone");
        widgetDTO.setCategory("Book");

        when(widgetDAO.create(any(WidgetDTO.class))).thenReturn(widget);

        Response response = EXT.target("/widgets").request().post(Entity.json(widgetDTO));

        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(422); // 422 Unprocessable Entity
    }

    @Test
    @DisplayName("Should return an error if price is negative")
    public void createNewWidgetWithPriceNegativeTest() {
        WidgetDTO widgetDTO = new WidgetDTO();
        widgetDTO.setName("Harry Potter and the Philosopher's Stone");
        widgetDTO.setCategory("Book");
        widget.setPrice(-18.20); // Price is negative

        when(widgetDAO.create(any(WidgetDTO.class))).thenReturn(widget);

        Response response = EXT.target("/widgets").request().post(Entity.json(widgetDTO));

        assertThat(response.getStatusInfo().getStatusCode()).isEqualTo(422); // 422 Unprocessable Entity
    }

    @Test
    @DisplayName("Should update a widget by ID")
    public void updateWidgetByIdTest() {
        WidgetDTO updatedWidgetDTO = new WidgetDTO();
        updatedWidgetDTO.setName("Updated Widget");
        updatedWidgetDTO.setCategory("Updated Category");
        updatedWidgetDTO.setPrice(15.50);

        widget.setId(1L);
        widget.setName("Updated Widget");
        widget.setCategory("Updated Category");
        widget.setPrice(15.50);

        when(widgetDAO.findById(1L)).thenReturn(Optional.of(widget));
        when(widgetDAO.update(any(Widget.class), any(WidgetDTO.class))).thenReturn(widget);

        Widget updatedWidget = EXT.target("/widgets/1").request().put(Entity.json(updatedWidgetDTO), Widget.class);

        assertThat(updatedWidget.getId()).isEqualTo(widget.getId());
        assertThat(updatedWidget.getName()).isEqualTo(updatedWidgetDTO.getName());
        assertThat(updatedWidget.getCategory()).isEqualTo(updatedWidgetDTO.getCategory());
        assertThat(updatedWidget.getPrice()).isEqualTo(updatedWidgetDTO.getPrice());

        verify(widgetDAO).findById(1L);
        verify(widgetDAO).update(any(Widget.class), any(WidgetDTO.class));
    }

    @Test
    public void patchWidgetById() {
        WidgetDTO updatedWidgetDTO = new WidgetDTO();
        updatedWidgetDTO.setName("Updated Widget");

        widget.setName("Updated Widget");

        when(widgetDAO.findById(1L)).thenReturn(Optional.of(widget));
        when(widgetDAO.partialUpdate(any(Widget.class), any(WidgetDTO.class))).thenReturn(widget);

        Widget updatedWidget = EXT.target("/widgets/1").request().method("PATCH", Entity.json(updatedWidgetDTO),
                Widget.class);

        assertThat(updatedWidget.getId()).isEqualTo(widget.getId());
        assertThat(updatedWidget.getName()).isEqualTo(updatedWidgetDTO.getName());
        assertThat(updatedWidget.getCategory()).isEqualTo(widget.getCategory()); // Category should not change
        assertThat(updatedWidget.getPrice()).isEqualTo(widget.getPrice()); // Price should not change

        verify(widgetDAO).findById(1L);
        verify(widgetDAO).partialUpdate(any(Widget.class), any(WidgetDTO.class));
    }

    @Test
    public void deleteWidgetById() {
        when(widgetDAO.findById(1L)).thenReturn(Optional.of(widget));

        Response response = EXT.target("/widgets/1").request().delete();

        verify(widgetDAO).delete(widget);
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

}
