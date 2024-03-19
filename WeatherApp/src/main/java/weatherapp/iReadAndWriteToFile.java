package weatherapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface iReadAndWriteToFile {

        default String readFromFile(String fileName) throws Exception {
        try {
            Path filePath = Paths.get(fileName);
            byte[] fileBytes = Files.readAllBytes(filePath);
            return new String(fileBytes);
        } catch (IOException e) {
            throw new Exception("Error reading from file");
        }
    }

   default boolean writeToFile(String fileName) throws Exception {
        try {
            String dataToWrite = "";
            Path filePath = Paths.get(fileName);
            Files.write(filePath, dataToWrite.getBytes());
            return true;
        } catch (IOException e) {
            throw new Exception("Error writing to file");
        }
    }
}
