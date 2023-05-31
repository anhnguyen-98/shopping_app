package com.mock2.shopping_app.model.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Mail {
    private String from;
    private String to;
    private String subject;
    private String content;
    private Map<String, String> model = new HashMap<>();
}
