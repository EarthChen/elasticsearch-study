package com.earthchen.elasticsearchspringbootstudy.config;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public TransportClient transportClient() throws UnknownHostException {

        //设置端口名字
        InetSocketTransportAddress node = new InetSocketTransportAddress(
                InetAddress.getByName("localhost"),
                9300
        );
        //设置名字
        Settings settings = Settings.builder().put("cluster.name", "earthchen").build();

        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node);
        return client;
    }
}
