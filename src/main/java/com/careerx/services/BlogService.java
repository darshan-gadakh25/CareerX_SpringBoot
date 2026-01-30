package com.careerx.services;

import com.careerx.entities.Blog;
import java.util.List;

public interface BlogService {
    List<Blog> getAllPublishedBlogs();

    Blog getBlogById(Long id);

    Blog createBlog(Blog blog);

    Blog updateBlog(Long id, Blog blog);

    void deleteBlog(Long id);
}
