package com.example.forum.web;

import com.example.forum.domain.Post;
import com.example.forum.dto.CommentView;
import com.example.forum.dto.PostSummary;
import com.example.forum.security.ForumUserDetails;
import com.example.forum.service.CommentService;
import com.example.forum.service.LikeService;
import com.example.forum.service.PostService;
import com.example.forum.web.form.CommentForm;
import com.example.forum.web.form.PostForm;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
public class PostController {

    private static final int PAGE_SIZE = 10;

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PostController(PostService postService,
                          CommentService commentService,
                          LikeService likeService) {
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<PostSummary> posts = postService.mainPage(page, PAGE_SIZE);
        model.addAttribute("posts", posts);
        return "index";
    }

    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new PostForm());
        }
        return "new-post";
    }

    @PostMapping("/posts")
    public String createPost(@Valid @ModelAttribute("form") PostForm form,
                             BindingResult binding,
                             @AuthenticationPrincipal ForumUserDetails user,
                             RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return "new-post";
        }

        Post post = postService.createPost(user.getId(), form.getTitle(), form.getBody());
        redirectAttributes.addFlashAttribute("flash", "Your post is live.");
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}")
    public String showPost(@PathVariable Long id,
                           @AuthenticationPrincipal ForumUserDetails user,
                           Model model) {
        populatePostPage(id, user, model);
        return "post";
    }

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @Valid @ModelAttribute("commentForm") CommentForm form,
                             BindingResult binding,
                             @AuthenticationPrincipal ForumUserDetails user,
                             Model model) {
        if (binding.hasErrors()) {
            populatePostPage(id, user, model);
            return "post";
        }

        commentService.addComment(id, user.getId(), form.getBody());
        return "redirect:/posts/" + id + "#comments";
    }

    @PostMapping("/posts/{id}/like")
    public String togglePostLike(@PathVariable Long id,
                                 @AuthenticationPrincipal ForumUserDetails user) {
        likeService.togglePostLike(id, user.getId());
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/like")
    public String toggleCommentLike(@PathVariable Long postId,
                                    @PathVariable Long commentId,
                                    @AuthenticationPrincipal ForumUserDetails user) {
        likeService.toggleCommentLike(commentId, user.getId());
        return "redirect:/posts/" + postId + "#comment-" + commentId;
    }

    private void populatePostPage(Long postId, ForumUserDetails user, Model model) {
        Post post = postService.getPost(postId);
        List<CommentView> comments = commentService.commentsFor(postId);
        boolean loggedIn = user != null;

        long postLikeCount = likeService.countPostLikes(postId);
        boolean postLiked = loggedIn && likeService.hasLikedPost(postId, user.getId());
        Set<Long> likedCommentIds = loggedIn
                ? likeService.likedCommentIds(user.getId(), comments.stream().map(CommentView::id).toList())
                : Set.of();

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("postLikeCount", postLikeCount);
        model.addAttribute("postLiked", postLiked);
        model.addAttribute("likedCommentIds", likedCommentIds);

        if (!model.containsAttribute("commentForm")) {
            model.addAttribute("commentForm", new CommentForm());
        }
    }
}
