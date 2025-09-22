package dao;

import model.Post;
import model.Comment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommunityDAO {

    /**
     * Fetches all posts, joining with the users table to get the author's name.
     */
    public static List<Post> getAllPosts() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.name as username FROM community_posts p " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "ORDER BY p.post_timestamp DESC";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Post post = new Post();
                post.setPostId(rs.getInt("post_id"));
                post.setUserId(rs.getInt("user_id"));
                post.setPostTitle(rs.getString("post_title"));
                post.setPostContent(rs.getString("post_content"));
                post.setPostTimestamp(rs.getTimestamp("post_timestamp"));
                post.setUsername(rs.getString("username"));
                posts.add(post);
            }
        }
        return posts;
    }

    /**
     * Fetches a single post by its ID, joining with users table.
     */
    public static Post getPostById(int postId) throws SQLException {
        String sql = "SELECT p.*, u.name as username FROM community_posts p " +
                     "JOIN users u ON p.user_id = u.user_id " +
                     "WHERE p.post_id = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Post post = new Post();
                    post.setPostId(rs.getInt("post_id"));
                    post.setUserId(rs.getInt("user_id"));
                    post.setPostTitle(rs.getString("post_title"));
                    post.setPostContent(rs.getString("post_content"));
                    post.setPostTimestamp(rs.getTimestamp("post_timestamp"));
                    post.setUsername(rs.getString("username"));
                    return post;
                }
            }
        }
        return null; // No post found
    }

    /**
     * Fetches all comments for a specific post, joining with users table.
     */
    public static List<Comment> getCommentsForPost(int postId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.name as username FROM community_comments c " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "WHERE c.post_id = ? " +
                     "ORDER BY c.comment_timestamp ASC"; // Show oldest comments first
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setPostId(rs.getInt("post_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setCommentContent(rs.getString("comment_content"));
                    comment.setCommentTimestamp(rs.getTimestamp("comment_timestamp"));
                    comment.setUsername(rs.getString("username"));
                    comments.add(comment);
                }
            }
        }
        return comments;
    }

    /**
     * Creates a new post in the database.
     */
    public static void addPost(Post post) throws SQLException {
        String sql = "INSERT INTO community_posts (user_id, post_title, post_content) VALUES (?, ?, ?)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, post.getUserId());
            ps.setString(2, post.getPostTitle());
            ps.setString(3, post.getPostContent());
            
            ps.executeUpdate();
        }
    }
    
    /**
     * Adds a new comment to a post.
     */
    public static void addComment(Comment comment) throws SQLException {
        String sql = "INSERT INTO community_comments (post_id, user_id, comment_content) VALUES (?, ?, ?)";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, comment.getPostId());
            ps.setInt(2, comment.getUserId());
            ps.setString(3, comment.getCommentContent());
            
            ps.executeUpdate();
        }
    }

    /**
     * ✅ NEW: Admin method to delete a single comment.
     */
    public static void deleteComment(int commentId) throws SQLException {
        String sql = "DELETE FROM community_comments WHERE comment_id = ?";
        
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, commentId);
            ps.executeUpdate();
        }
    }
    
    /**
     * ✅ NEW: Admin method to delete a post and all its comments.
     * This uses a transaction to ensure both operations succeed or fail together.
     */
    public static void deletePost(int postId) throws Exception {
        String deleteCommentsSQL = "DELETE FROM community_comments WHERE post_id = ?";
        String deletePostSQL = "DELETE FROM community_posts WHERE post_id = ?";
        
        Connection con = null;
        try {
            con = DBUtil.getConnection();
            con.setAutoCommit(false); // Start transaction

            // Step 1: Delete all comments associated with the post
            try (PreparedStatement ps = con.prepareStatement(deleteCommentsSQL)) {
                ps.setInt(1, postId);
                ps.executeUpdate();
            }
            
            // Step 2: Delete the post itself
            try (PreparedStatement ps = con.prepareStatement(deletePostSQL)) {
                ps.setInt(1, postId);
                ps.executeUpdate();
            }
            
            con.commit(); // Commit the transaction
            
        } catch (Exception e) {
            if (con != null) con.rollback(); // Roll back if anything failed
            throw new Exception("Error deleting post", e);
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }
}