package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "warehouse_products")
public class WarehouseProduct {

    @Id
    @Column(name = "product_id")
    private UUID productId;

    private Boolean fragile;

    private Double width;

    private Double height;

    private Double depth;

    private Double weight;

    private Long quantity;

}
