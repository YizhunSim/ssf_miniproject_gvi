package vttp2022.ssf.ssf_miniproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);
    public static String saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        try(InputStream inputStream = multipartFile.getInputStream()){
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toAbsolutePath().toString();
        } catch(IOException ex){
            throw new IOException("Could not save file: " + fileName, ex);
        }
    }

    public static void cleanDir(String dir){
        Path dirPath = Paths.get(dir);

        try{
            Files.list(dirPath).forEach(file ->{
                if (!Files.isDirectory(file)){
                    try{
                        Files.delete(file);
                    } catch(IOException ex){
                        LOGGER.error("Could not delete file: " + file);
//                        System.out.println("Could not delete file: " + file);
                    }
                } else{
                    deleteDirectory(file.toFile());
                }
            });
        } catch(IOException ex){
            LOGGER.error("Could not list directory: " + dirPath);
//            System.out.println("Could not list directory: " + dirPath);
        }
    }

    public static void copyFileToDestination(String source, Path targetDirectory){
        System.out.println("copyFileToDestination - source: " + source + "destination: " + targetDirectory.getFileName());
        Path sourceDirectory = Paths.get(source);
        // Path targetDirectory = Paths.get(destination);

        //copy source to target using Files Class
        try {
            Files.copy(sourceDirectory, targetDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDirectory (File directoryToBeDeleted){
        System.out.println("Delete Directory: "+ directoryToBeDeleted.getName());
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
        for (File file : allContents) {
            deleteDirectory(file);
        }
        }
        return directoryToBeDeleted.delete();
    }
}
