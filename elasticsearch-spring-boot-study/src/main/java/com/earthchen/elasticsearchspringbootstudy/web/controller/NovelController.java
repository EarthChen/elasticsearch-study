package com.earthchen.elasticsearchspringbootstudy.web.controller;

import com.earthchen.elasticsearchspringbootstudy.domain.Novel;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/book/novel")
@Slf4j
public class NovelController {

    @Autowired
    private TransportClient client;

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Object getNovelById(@PathVariable String id) {
        GetResponse getResponse = client.prepareGet("book", "novel", id).get();
        return getResponse.getSource();
    }

    /**
     * 添加
     *
     * @return
     */
    @PostMapping("/novel")
    public Object addNovel(Novel novel) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject()
                .field("title", novel.getTitle())
                .field("author", novel.getAuthor())
                .field("word_count", novel.getWord_count())
                .field("public_date", novel.getPublic_date())
                .endObject();
        IndexResponse response = this.client.prepareIndex("book", "novel")
                .setSource(builder).get();
        return response.getId();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Object delNovelById(@PathVariable String id) {
        DeleteResponse deleteResponse = client.prepareDelete("book", "novel", id).get();
        return deleteResponse.getResult();
    }

    /**
     * 修改
     *
     * @param novel
     * @return
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PutMapping
    public Object updateNovel(Novel novel) throws IOException, ExecutionException, InterruptedException {
        UpdateRequest request = new UpdateRequest("book", "novel", novel.getId());

        XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        if (novel.getAuthor() != null) {
            builder.field("author", novel.getAuthor());
        }
        if (novel.getTitle() != null) {
            builder.field("title", novel.getTitle());
        }
        builder.endObject();
        request.doc(builder);
        UpdateResponse response = this.client.update(request).get();
        return response.getResult();
    }

    /**
     * 复合查询
     * @param gtWordCount
     * @param author
     * @param title
     * @param ltWordCount
     * @return
     */
    @PostMapping("/query")
    public Object query(@RequestParam(value = "gt_word_count", defaultValue = "0") int gtWordCount,
                        @RequestParam(value = "author", required = false) String author,
                        @RequestParam(value = "title", required = false) String title,
                        @RequestParam(value = "lt_word_count", required = false) Integer ltWordCount) {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        if (author != null) {
            boolBuilder.must(QueryBuilders.matchQuery("author", author));
        }
        if (title != null) {
            boolBuilder.must(QueryBuilders.matchQuery("title", title));
        }
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("word_count").from(gtWordCount);
        if (ltWordCount != null) {
            rangeQuery.to(ltWordCount);
        }
        boolBuilder.filter(rangeQuery);
        SearchRequestBuilder builder = client.prepareSearch("book")
                .setTypes("novel")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(boolBuilder)
                .setFrom(0)
                .setSize(10);
        log.info(String.valueOf(builder));
        SearchResponse response = builder.get();

        List<Map<String, Object>> result = new ArrayList<>();
        response.getHits().forEach((s) -> result.add(s.getSource()));
        return result;
    }
}
