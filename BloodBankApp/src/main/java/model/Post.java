package model;

import java.sql.Timestamp;

public class Post {

    private int postId;
    private int userId;
    private String postTitle;
    private String postContent;
    private Timestamp postTimestamp;
    
    // This isn't in the table, but we'll add it
    // with a SQL JOIN to show the author's name.
    private String username;

    // Getters
    public int getPostId() {
        return postId;
    }
    public int getUserId() {
        return userId;
    }
    public String getPostTitle() {
        return postTitle;
    }
    public String getPostContent() {
        return postContent;
    }
    public Timestamp getPostTimestamp() {
        return postTimestamp;
    }
    public String getUsername() {
        return username;
    }

    // Setters
    public void setPostId(int postId) {
        this.postId = postId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }
    public void setPostTimestamp(Timestamp postTimestamp) {
        this.postTimestamp = postTimestamp;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}