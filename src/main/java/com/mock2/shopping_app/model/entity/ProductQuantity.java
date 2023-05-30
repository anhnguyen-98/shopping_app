package com.mock2.shopping_app.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_quantity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductQuantity {
    @Id
    @Column(name = "product_id")
    private Long productId;

    private Long quantity;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;
}
