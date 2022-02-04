package org.imc.pam.boilerplate.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imc.pam.boilerplate.api.models.ExampleFile;
import org.imc.pam.boilerplate.exceptions.ExampleFileNotFoundException;
import org.imc.pam.boilerplate.properties.PathProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileManagement {

    private static final Logger logger = LogManager.getLogger(FileManagement.class);

    private final PathProperties pathProperties;

    public FileManagement(PathProperties pathProperties) {
        this.pathProperties = pathProperties;
    }

    public ExampleFile getFileInfoFromStorage(String fileName) throws IOException {
        if (Files.exists(Paths.get(pathProperties.getFileStoragePath(), fileName))) {
            ExampleFile fileInfo =
                    setFileInfo(new File(pathProperties.getFileStoragePath(), fileName));
            return fileInfo;
        } else {
            throw new ExampleFileNotFoundException();
        }
    }

    public boolean save(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        Path root = Paths.get(pathProperties.getFileStoragePath(), file.getOriginalFilename());
        Files.write(root, bytes);
        return Files.exists(root);
    }

    public void delete(String fileName) {
        try {
            Path root = Paths.get(pathProperties.getFileStoragePath(), fileName);
            Files.delete(root);
        } catch (IOException e) {
            logger.warn("Could not delete the file. Error: " + e.getMessage());
        }
    }

    public Resource download(String fileName) {
        try {
            Path filePath = Paths.get(pathProperties.getFileStoragePath(), fileName);
            if (filePath != null) {
                return new UrlResource(filePath.toUri());
            }
        } catch (Exception e) {
            logger.warn("Could not download the file. Error: " + e.getMessage());
        }
        return null;
    }

    public static LocalDateTime getCreateTime(File file) throws IOException {
        Path path = Paths.get(file.getPath());
        BasicFileAttributeView basicfile =
                Files.getFileAttributeView(
                        path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        BasicFileAttributes attr = basicfile.readAttributes();
        long date = attr.creationTime().toMillis();
        Instant instant = Instant.ofEpochMilli(date);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDateTime getChangedTime(File file) throws IOException {
        Path path = Paths.get(file.getPath());
        BasicFileAttributeView basicfile =
                Files.getFileAttributeView(
                        path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        BasicFileAttributes attr = basicfile.readAttributes();
        long date = attr.lastModifiedTime().toMillis();
        Instant instant = Instant.ofEpochMilli(date);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static ExampleFile setFileInfo(File file) throws IOException {
        ExampleFile tempFileInfo = new ExampleFile();
        Path filePath = Paths.get(file.getAbsolutePath());
        BasicFileAttributes fileAttr = Files.readAttributes(filePath, BasicFileAttributes.class);
        tempFileInfo.setFileName(file.getName());
        tempFileInfo.setCreated(FileManagement.getCreateTime(file));
        tempFileInfo.setFilePath(file.getPath());
        tempFileInfo.setSizeInKb(fileAttr.size());
        tempFileInfo.setChanged(FileManagement.getChangedTime(file));
        tempFileInfo.setMimeType(Files.probeContentType(filePath));
        return tempFileInfo;
    }

    public static List<String> listFilesOnly(String pathName) {
        try {
            Path folder = Paths.get(pathName);
            return Files.list(folder)
                    .map(Path::getFileName)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public Map<String, Object> javaObjectToJsonObject(ExampleFile data) {
        Map<String, Object> json = new HashMap<>();
        json.put("fileName", data.getFileName());
        json.put("filePath", data.getFilePath());
        json.put("mimeType", data.getMimeType());
        json.put("created", data.getCreated());
        json.put("changed", data.getChanged());
        json.put("sizeInKb", data.getSizeInKb());
        return json;
    }

    public Boolean fileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}
