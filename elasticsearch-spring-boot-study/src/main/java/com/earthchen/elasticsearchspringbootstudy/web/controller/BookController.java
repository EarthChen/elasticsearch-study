package com.earthchen.elasticsearchspringbootstudy.web.controller;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private TransportClient client;

    @GetMapping("/novel/{id}")
    public Object getBookById(@PathVariable String id) {
        GetResponse getResponse = client.prepareGet("book", "novel", id).get();
        return getResponse.getSource();
    }


}
