package com.warehouse.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "widget")
@ApiModel(description = "A widget that represents an item in the warehouse")
public class Widget {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "Widget ID", example = "123")
    private long id;

    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    @ApiModelProperty(value = "Widget name", example = "Harry Potter and the Philosopher's Stone")
    private String name;

    @Getter
    @Setter
    @Column(name = "category", nullable = false)
    @ApiModelProperty(value = "Widget category", example = "Book")
    private String category;

    @Getter
    @Setter
    @Column(name = "price", nullable = false)
    @ApiModelProperty(value = "Widget price", example = "13.5")
    private Double price;

    @Getter
    @Setter
    @Column(name = "created_at")
    @CreationTimestamp
    @ApiModelProperty(value = "Timestamp when the widget is created", example = "1711300944611")
    private Date createdAt;
}
