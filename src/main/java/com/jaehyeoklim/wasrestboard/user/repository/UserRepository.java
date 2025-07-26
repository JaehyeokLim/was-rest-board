package main.java.com.jaehyeoklim.wasrestboard.user.repository;

import main.java.com.jaehyeoklim.wasrestboard.user.domain.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static main.java.com.jaehyeoklim.wasrestboard.util.Logger.log;

public class UserRepository {

    private static final String DIRECTORY_PATH = "./temp";
    private static final String FILE_PATH = "./temp/users.dat";
    private static final String DELIMITER = ",";

    public synchronized void add(User user) {
        createDirectoryIfNotExists();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_PATH, UTF_8, true))) {
            bufferedWriter.write(
                    user.getId() + DELIMITER +
                            user.getLoginId() + DELIMITER +
                            user.getPassword() + DELIMITER +
                            user.getName()
            );
            bufferedWriter.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void createDirectoryIfNotExists() {
        try {
            Files.createDirectories(Path.of(DIRECTORY_PATH));
            log("Ensured directory exists: " + DIRECTORY_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + DIRECTORY_PATH, e);
        }
    }

    public synchronized User findByLoginId(String loginId) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_PATH, UTF_8))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] userData = line.split(DELIMITER);
                if (userData.length != 4) continue;

                if (userData[1].equals(loginId)) {
                    return new User(
                            UUID.fromString(userData[0]),
                            userData[1],
                            userData[2],
                            userData[3]
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

    public synchronized void deleteByLoginId(String loginId) {
        File originalFile = new File(FILE_PATH);
        File tempFile = new File(FILE_PATH + ".tmp");

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(originalFile, UTF_8));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile, UTF_8))) {

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] userData = line.split(DELIMITER);

                if (userData.length != 4) continue;

                if (userData[1].equals(loginId)) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete user by loginId", e);
        }

        if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
            throw new RuntimeException("Failed to replace user file after deletion");
        }
    }
}
