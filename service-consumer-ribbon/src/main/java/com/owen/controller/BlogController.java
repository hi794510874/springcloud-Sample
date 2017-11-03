package com.owen.controller;

import com.owen.model.BlogEntity;
import com.owen.model.CommonRS;
import com.owen.restTemplateUtil.RestTempLateUtil;
import com.owen.services.BlogService;
import com.owen.services.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by huang_b on 2017/11/3.
 */
@RestController
public class BlogController {
    @Autowired
    BlogService blogService;

    @RequestMapping(value = "/getBlogById/{id}",method = RequestMethod.GET)
    public CommonRS<BlogEntity> getBlogById(@PathVariable String id) {
        CommonRS<BlogEntity> blogEntityCommonRS = blogService.getBlogById(id);
        return blogEntityCommonRS;
    }

    @RequestMapping(value = "/getAllBlog",method = RequestMethod.GET)
    public CommonRS<List< BlogEntity>> getAllBlog() {
        CommonRS<List< BlogEntity>>  listBlogEntityCommonRS = blogService.getAllBlog();
        return listBlogEntityCommonRS;
    }

}
