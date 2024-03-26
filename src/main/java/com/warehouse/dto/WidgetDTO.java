package com.warehouse.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Widget DTO")
public class WidgetDTO {
    @JsonProperty("name")
    @Getter
    @Setter
    @NotNull
    @NotBlank()
    @ApiModelProperty(value = "Widget name", example = "Harry Potter and the Philosopher's Stone")
    private String name;

    @JsonProperty("category")
    @Getter
    @Setter
    @NotNull
    @NotBlank()
    @ApiModelProperty(value = "Widget category", example = "Book")
    private String category;

    @JsonProperty("price")
    @Getter
    @Setter
    @NotNull
    @Min(0)
    @ApiModelProperty(value = "Widget price", example = "13.5")
    private Double price;
}
