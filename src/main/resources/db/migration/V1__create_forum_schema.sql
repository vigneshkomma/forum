CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(30)  NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    display_name  VARCHAR(50),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email    UNIQUE (email)
);

CREATE TABLE posts (
    id         BIGSERIAL PRIMARY KEY,
    author_id  BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title      VARCHAR(200) NOT NULL,
    body       TEXT         NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_posts_created_at ON posts (created_at DESC, id DESC);
CREATE INDEX idx_posts_author     ON posts (author_id);

CREATE TABLE comments (
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT      NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    author_id  BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    body       TEXT        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_comments_post   ON comments (post_id, created_at);
CREATE INDEX idx_comments_author ON comments (author_id);

CREATE TABLE post_likes (
    post_id    BIGINT      NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id    BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (post_id, user_id)
);

CREATE INDEX idx_post_likes_user ON post_likes (user_id);

CREATE TABLE comment_likes (
    comment_id BIGINT      NOT NULL REFERENCES comments(id) ON DELETE CASCADE,
    user_id    BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (comment_id, user_id)
);

CREATE INDEX idx_comment_likes_user ON comment_likes (user_id);