package com.careerx.serviceImpl;

import com.careerx.entities.Blog;
import com.careerx.repository.BlogRepository;
import com.careerx.services.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;

    @Override
    public List<Blog> getAllPublishedBlogs() {
        return blogRepository.findByPublishedTrueOrderByCreatedDateDesc();
    }

    @Override
    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
    }

    @Override
    public Blog createBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog existingBlog = getBlogById(id);
        existingBlog.setTitle(blog.getTitle());
        existingBlog.setContent(blog.getContent());
        existingBlog.setImageUrl(blog.getImageUrl());
        existingBlog.setAuthor(blog.getAuthor());
        existingBlog.setPublished(blog.isPublished());
        return blogRepository.save(existingBlog);
    }

    @Override
    public void deleteBlog(Long id) {
        Blog blog = getBlogById(id);
        blogRepository.delete(blog);
    }
}
