package com.example.forum.web;

import com.example.forum.config.SecurityConfig;
import com.example.forum.dto.PostSummary;
import com.example.forum.repository.UserRepository;
import com.example.forum.service.CommentService;
import com.example.forum.service.LikeService;
import com.example.forum.service.PostService;
import com.example.forum.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {PostController.class, AuthController.class})
@Import(SecurityConfig.class)
class SecurityFlowTest {

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
    void loginPageIsPublicAndContainsCsrfToken() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void indexIsPublic() throws Exception {
        PostSummary summary = new PostSummary(
                1L, "Hello world", "Body", "alice",
                Instant.parse("2026-01-01T10:00:00Z"), 0L, 0L);
        when(postService.mainPage(0, 10)).thenReturn(new PageImpl<>(List.of(summary)));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello world")));
    }

    @Test
    void newPostFormRequiresLogin() throws Exception {
        mockMvc.perform(get("/posts/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
