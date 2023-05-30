package com.mock2.shopping_app.model.other;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Page<T> {
    private List<T> content;
    private Object currentPage;
    private Object totalItems;
    private Object totalPages;
}
