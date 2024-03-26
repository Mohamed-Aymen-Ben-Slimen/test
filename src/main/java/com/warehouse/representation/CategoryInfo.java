package com.warehouse.representation;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class CategoryInfo {
    @Getter
    @Setter
    @ApiModelProperty(value = "Category", example = "Book")
    private String category;

    @Getter
    @Setter
    @ApiModelProperty(value = "Sum of prices of items of an indicated category", example = "110.60")
    private Double totalPrice;

    @Getter
    @Setter
    @ApiModelProperty(value = "The number of items of an indicated category", example = "26")
    private int numberOfWidgets;
}
