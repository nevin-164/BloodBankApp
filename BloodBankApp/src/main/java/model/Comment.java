package model;

import java.sql.Timestamp;

public class Comment {

    private int commentId;
    private int postId;
    private int userId;
    private String commentContent;
    private Timestamp commentTimestamp;
    
    // We'll also add username here to show who commented
    private String username;

    // Getters
    public int getCommentId() {
        return commentId;
    }
    public int getPostId() {
        return postId;
    }
    public int getUserId() {
        return userId;
    }
    public String getCommentContent() {
        return commentContent;
    }
    public Timestamp getCommentTimestamp() {
        return commentTimestamp;
    }
    public String getUsername() {
        return username;
    }

    // Setters
    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
    public void setPostId(int postId) {
        this.postId = postId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
    public void setCommentTimestamp(Timestamp commentTimestamp) {
        this.commentTimestamp = commentTimestamp;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}