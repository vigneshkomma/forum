package com.example.forum.web;

import com.example.forum.domain.Post;
import com.example.forum.domain.User;
import com.example.forum.dto.CommentView;
import com.example.forum.dto.PostSummary;
import com.example.forum.config.SecurityConfig;
import com.example.forum.repository.UserRepository;
import com.example.forum.service.CommentService;
import com.example.forum.service.LikeService;
import com.example.forum.service.PostService;
import com.example.forum.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PostController.class, AuthController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfig.class)
class WebPagesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private LikeService likeService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository users;

    @Test
    void indexRenders() throws Exception {
        PostSummary summary = new PostSummary(
                1L, "Hello world", "Some body text", "alice",
                Instant.parse("2026-01-01T10:00:00Z"), 2L, 3L);

        when(postService.mainPage(0, 10)).thenReturn(new PageImpl<>(List.of(summary)));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello world")))
                .andExpect(content().string(containsString("/css/app.css")));
    }

    @Test
    void postDetailRenders() throws Exception {
        User author = mock(User.class);
        when(author.getUsername()).thenReturn("alice");
        when(author.getDisplayName()).thenReturn("Alice");

        Post post = mock(Post.class);
        when(post.getId()).thenReturn(1L);
        when(post.getTitle()).thenReturn("Hello world");
        when(post.getBody()).thenReturn("Body text");
        when(post.getAuthor()).thenReturn(author);
        when(post.getCreatedAtFullDisplay()).thenReturn("Jan 1, 2026 at 10:00");

        when(postService.getPost(1L)).thenReturn(post);
        when(commentService.commentsFor(1L)).thenReturn(List.of(
                new CommentView(5L, "Nice post", "bob",
                        Instant.parse("2026-01-02T10:00:00Z"), 1L)));
        when(likeService.countPostLikes(1L)).thenReturn(3L);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello world")))
                .andExpect(content().string(containsString("Nice post")));
    }

    @Test
    void authAndNewPostPagesRender() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
        mockMvc.perform(get("/register")).andExpect(status().isOk());
        mockMvc.perform(get("/posts/new")).andExpect(status().isOk());
    }
}
