package org.imc.pam.boilerplate.api.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.imc.pam.boilerplate.api.models.ExampleFile;
import org.imc.pam.boilerplate.exceptions.ExampleFileAlreadyExistException;
import org.imc.pam.boilerplate.exceptions.ExampleFileNotFoundException;
import org.imc.pam.boilerplate.properties.PathProperties;
import org.imc.pam.boilerplate.tools.FileManagement;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExampleFileInfoService {

    private final FileManagement fileManagement;
    private final PathProperties pathProperties;

    public ExampleFileInfoService(PathProperties pathProperties, FileManagement fileManagement) {
        this.pathProperties = pathProperties;
        this.fileManagement = fileManagement;
    }

    /***************************************************************************************
     ************************************ GET METHODS ***************************************
     ***************************************************************************************/

    public ExampleFile getOneExampleFileInfo(String fileName) throws IOException {
        ExampleFile fileInfo = fileManagement.getFileInfoFromStorage(fileName);
        if (fileInfo != null) {
            return fileInfo;
        } else {
            throw new ExampleFileNotFoundException();
        }
    }

    public List<ExampleFile> getMultipleExampleFilesInfo(String[] fileNameList) throws IOException {
        List<ExampleFile> fileInfoList = new ArrayList<>();
        for (String fileName : fileNameList) {
            if (fileName.length() > 0) {
                ExampleFile fileInfo = fileManagement.getFileInfoFromStorage(fileName);
                if (fileInfo != null) fileInfoList.add(fileInfo);
            }
        }
        if (!fileInfoList.isEmpty()) {
            return fileInfoList;
        } else {
            throw new ExampleFileNotFoundException();
        }
    }

    public List<ExampleFile> getAllExampleFilesInfo() throws IOException {
        List<String> fileNameList =
                FileManagement.listFilesOnly(pathProperties.getFileStoragePath());
        List<ExampleFile> fileInfoList = new ArrayList<>();
        for (String fileName : fileNameList) {
            ExampleFile fileInfo = fileManagement.getFileInfoFromStorage(fileName);
            if (fileInfo != null) {
                fileInfoList.add(fileInfo);
            }
        }
        if (!fileInfoList.isEmpty()) {
            return fileInfoList;
        } else {
            throw new ExampleFileNotFoundException();
        }
    }

    public ResponseEntity<Object> downloadExampleFile(String fileName, HttpServletRequest request)
            throws IOException {
        Resource resource = fileManagement.download(fileName);
        String contentType = null;
        if (resource.exists()) {
            contentType =
                    request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

        } else {
            throw new ExampleFileNotFoundException();
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /***************************************************************************************
     *********************************** POST METHODS ***************************************
     ***************************************************************************************/

    public ExampleFile uploadOneExampleFile(MultipartFile mFile) throws IOException {
        if (!Files.exists(
                Paths.get(pathProperties.getFileStoragePath(), mFile.getOriginalFilename()))) {
            fileManagement.save(mFile);
            return fileManagement.getFileInfoFromStorage(mFile.getOriginalFilename());
        } else {
            throw new ExampleFileAlreadyExistException();
        }
    }

    /***************************************************************************************
     *********************************** DELETE METHODS *************************************
     ***************************************************************************************/

    public ExampleFile deleteOneExampleFile(String fileName) throws IOException {
        ExampleFile fileInfo = fileManagement.getFileInfoFromStorage(fileName);
        if (fileInfo != null) {
            fileManagement.delete(fileName);
            return fileInfo;
        } else {
            throw new ExampleFileNotFoundException(); // ändra till msg
        }
    }
}
