package main.java.com.jaehyeoklim.wasrestboard.board.repository;

import main.java.com.jaehyeoklim.wasrestboard.board.domain.Post;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.*;
import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;

public class PostRepository {

    private static final String DIRECTORY_PATH = "./temp";
    private static final String FILE_PATH = "./temp/posts.dat";
    private static final String DELIMITER = ",";

    public synchronized void add(Post post) {
        createDirectoryIfNotExists();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_PATH, UTF_8, true))) {
            bufferedWriter.write(
                    post.getId() + DELIMITER +
                        post.getOwner() + DELIMITER +
                        post.getCreatedAt() + DELIMITER +
                        post.getTitle() + DELIMITER +
                        post.getContent()
            );
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDirectoryIfNotExists() {
        try {
            Files.createDirectories(Path.of(DIRECTORY_PATH));
            log("Ensure directory exists: " + DIRECTORY_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + DIRECTORY_PATH, e);
        }
    }

    public synchronized Post findById(UUID id) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_PATH, UTF_8))){
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] postData = line.split(DELIMITER);
                if (postData.length != 5) continue;

                if (postData[0].equals(id.toString())) {
                    return new Post(
                            UUID.fromString(postData[0]),
                            postData[1],
                            LocalDateTime.parse(postData[2]),
                            postData[3],
                            postData[4]
                    );
                }
            }
        } catch (FileNotFoundException e) {
            log("File not found: " + FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public synchronized List<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] postData = line.split(DELIMITER);

                if (postData.length != 5) continue;

                posts.add(new Post(
                        UUID.fromString(postData[0]),
                        postData[1],
                        LocalDateTime.parse(postData[2]),
                        postData[3],
                        postData[4]
                ));
            }
            return posts;
        } catch (FileNotFoundException e) {
            log("File not found: " + FILE_PATH);
            return List.of();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void deleteByPostId(String postId) {
        File originalFile = new File(FILE_PATH);
        File tempFile = new File(FILE_PATH + ".tmp");

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(originalFile, UTF_8));
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile, UTF_8))) {

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] postData = line.split(DELIMITER);

                if (postData.length != 5) continue;

                if (!postData[0].equals(postId)) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete post by postId", e);
        }

        if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
            throw new RuntimeException("Failed to replace post file after deletion");
        }
    }

    public synchronized void update(String postId, String title, String content) {
        File originalFile = new File(FILE_PATH);
        File tempFile = new File(FILE_PATH + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile, UTF_8));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] postData = line.split(DELIMITER);

                if (postData.length != 5) continue;

                String currentPostId = postData[0];

                if (currentPostId.equals(postId)) {
                    String updatedLine = currentPostId + DELIMITER +
                            postData[1] + DELIMITER +
                            postData[2] + DELIMITER +
                            title + DELIMITER +
                            content;
                    writer.write(updatedLine);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to update post by postId", e);
        }

        if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
            throw new RuntimeException("Failed to replace post file after update");
        }
    }
}
