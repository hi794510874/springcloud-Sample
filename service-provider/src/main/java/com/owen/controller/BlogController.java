package com.owen.controller;

import com.owen.jsonUtil.JacksonUtils;
import com.owen.mapper.BlogMapper;
import com.owen.model.BlogEntity;
import com.owen.model.CommonRQ;
import com.owen.model.CommonRS;
import com.owen.model.Head;
import com.owen.redis.helper.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.SpanAccessor;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by huang_b on 2017/9/25.
 * redis  自动缓存 http://blog.csdn.net/tianyaleixiaowu/article/details/70314277
 *//*
@RequestMapping("blog")*/
@RestController
public class BlogController {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private Tracer tracer;

    @Autowired
    private SpanAccessor accessor;

    @Autowired
    private RedisTemplate redisTemplate;


    /*@GetMapping(value = "getblogbyid")*/
    @RequestMapping(value = "/getblogbyid/{id}", method = RequestMethod.GET)
    public CommonRS<BlogEntity> getBlogById(@PathVariable String id) throws Exception {

        BlogEntity blogEntity = blogMapper.getOne(id);
        CommonRS<BlogEntity> blogEntityCommonRS = new CommonRS<>();
        Head head = new Head();
        head.setCode(200);
        head.setMsg("ok");
        blogEntityCommonRS.setData(blogEntity);
        blogEntityCommonRS.setHead(head);
        this.tracer.addTag("request", JacksonUtils.toJson(id));
        this.tracer.addTag("response", JacksonUtils.toJson(blogEntityCommonRS));


        return blogEntityCommonRS;
    }

    @RequestMapping(value = "getAllBlog", method = RequestMethod.GET)
    public CommonRS<List<BlogEntity>> getAllBlog() {
        List<BlogEntity> blogEntityList = blogMapper.getAll();
        CommonRS<List<BlogEntity>> listBlogCommonRS = new CommonRS<>();
        listBlogCommonRS.setData(blogEntityList);
        Head head = new Head();
        head.setMsg("ok");
        head.setCode(200);
        listBlogCommonRS.setHead(head);

        return listBlogCommonRS;
    }

    @RequestMapping(value = "addBlog/{id}", method = RequestMethod.POST)
    public CommonRS<Boolean> addBlog(@RequestBody CommonRQ<BlogEntity> request, @PathVariable String id) {
        BlogEntity entity = request.data;
        entity.setId(id);
        blogMapper.insert(entity);

        CommonRS<Boolean> rs = new CommonRS<>();
        Head head = new Head();
        head.setCode(200);
        head.setMsg("ok");
        rs.setData(true);
        rs.setHead(head);

        this.tracer.addTag("request", JacksonUtils.toJson(request));
        this.tracer.addTag("response", JacksonUtils.toJson(rs));
        return rs;
    }

    @RequestMapping(value = "/delblogbyid/{id}", method = RequestMethod.GET)
    public CommonRS<Boolean> delBlogById(@PathVariable String id) {

        CommonRS<Boolean> rs = new CommonRS<>();
        Head head = new Head();
        head.setCode(200);
        head.setMsg("ok");

        blogMapper.delete(id);

        rs.setData(true);
        rs.setHead(head);
        return rs;
    }

    @RequestMapping(value = "/updateblogbyid/{id}", method = RequestMethod.POST)
    public CommonRS<BlogEntity> updateBlogById(@RequestBody CommonRQ<BlogEntity> request) throws Exception {

        blogMapper.update(request.data);
        CommonRS<BlogEntity> blogEntityCommonRS = new CommonRS<>();
        Head head = new Head();
        head.setCode(200);
        head.setMsg("ok");
        blogEntityCommonRS.setData(request.data);
        blogEntityCommonRS.setHead(head);
        this.tracer.addTag("request", JacksonUtils.toJson(request));
        this.tracer.addTag("response", JacksonUtils.toJson(blogEntityCommonRS));

        return blogEntityCommonRS;
    }


    @RequestMapping(value = "/saveperson2redis/{id}", method = RequestMethod.POST)
    public CommonRS<Boolean> saveperson2redis(@RequestBody CommonRQ<BlogEntity> request) {

        CommonRS<Boolean> rs = new CommonRS<>();
        Head head = new Head();
        head.setCode(200);
        head.setMsg("ok");

        String hash = "ddd";

        redisTemplate.opsForHash().put(hash, "kkkt", request);

        request = (CommonRQ<BlogEntity>) redisTemplate.opsForHash().get(hash, "kkkt");
        rs.setData(true);
        rs.setHead(head);
        return rs;
    }
}
