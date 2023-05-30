package com.mock2.shopping_app.model.entity;

import com.mock2.shopping_app.model.audit.DateAudit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(length = 50)
    private String name;

    private float price;

    private String description;

//    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
//    private List<Media> mediaList;

//    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
//    private List<Purchase> purchaseList;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Review> reviews;

    @OneToOne(mappedBy = "product", orphanRemoval = true)
    private ProductQuantity productQuantity;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProductImage> productImages;
}
