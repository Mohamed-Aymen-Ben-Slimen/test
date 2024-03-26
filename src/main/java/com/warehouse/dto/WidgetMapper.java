package com.warehouse.dto;

import com.warehouse.entity.Widget;

public final class WidgetMapper {
    public Widget toEntity(WidgetDTO dto) {
        if (dto == null) {
            return null;
        }
        Widget entity = new Widget();
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setPrice(dto.getPrice());
        return entity;
    }

    public Widget updateEntityFromDTO(Widget widget, WidgetDTO dto) {
        widget.setName(dto.getName());
        widget.setCategory(dto.getCategory());
        widget.setPrice(dto.getPrice());

        return widget;
    }

    public Widget partialUpdateEntityFromDTO(Widget widget, WidgetDTO dto) {
        if (dto.getName() != null) {
            widget.setName(dto.getName());
        }
        if (dto.getCategory() != null) {
            widget.setCategory(dto.getCategory());
        }
        if (dto.getPrice() != null) {
            widget.setPrice(dto.getPrice());
        }
        return widget;
    }
}
