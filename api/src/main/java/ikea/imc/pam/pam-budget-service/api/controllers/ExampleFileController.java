package org.imc.pam.boilerplate.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imc.pam.boilerplate.api.configuration.ResponseMsg;
import org.imc.pam.boilerplate.api.exampleresponsemodels.exampleFile.ArrayExampleFileResponse;
import org.imc.pam.boilerplate.api.exampleresponsemodels.exampleFile.ExampleFileResponse;
import org.imc.pam.boilerplate.api.exampleresponsemodels.exampleFile.ExampleFileUploadsResponse;
import org.imc.pam.boilerplate.api.exampleresponsemodels.smokeTest.GeneralResponse;
import org.imc.pam.boilerplate.api.models.BatchCreateResponse;
import org.imc.pam.boilerplate.api.models.ExampleFile;
import org.imc.pam.boilerplate.api.services.ExampleFileInfoService;
import org.imc.pam.boilerplate.exceptions.ExampleFileAlreadyExistException;
import org.imc.pam.boilerplate.exceptions.ExampleFileNotFoundException;
import org.imc.pam.boilerplate.tools.FileManagement;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api")
@Tag(name = "Example-Files", description = "Apis for example-files")
@SecurityRequirement(name = "Open Id Connect")
@SecurityRequirement(name = "JWT-Token")
public class ExampleFileController {

    private static Logger logger = LogManager.getLogger(ExampleFileController.class);

    private final ExampleFileInfoService service;

    private final FileManagement fileManagement;

    public ExampleFileController(
            FileManagement fileManagement, ExampleFileInfoService fileInfoService) {
        this.service = fileInfoService;
        this.fileManagement = fileManagement;
    }

    /***************************************************************************************
     ************************************ GET METHODS ***************************************
     ***************************************************************************************/
    @GetMapping("/web/v1/exampleFile/{fileName}")
    @Operation(summary = "Get an example file by filename.")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleFileResponse.class)))
    public ResponseEntity<Object> getExampleFileInfoByFileName(@PathVariable String fileName) {
        ResponseEntity<Object> responseEntity;
        try {
            ExampleFile exampleFile = service.getOneExampleFileInfo(fileName);
            responseEntity = createReponseEntity(HttpStatus.OK, exampleFile);
        } catch (IOException | ExampleFileNotFoundException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    @GetMapping("/web/v1/exampleFiles")
    @Operation(
            summary =
                    "Get multiple example file by filenames or don't assign a filename to get all.")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ArrayExampleFileResponse.class)))
    public ResponseEntity<Object> getMultipleExampleFilesInfo(
            @RequestParam(value = "fileName", required = false) String[] fileNameList) {
        List<ExampleFile> exampleFiles;
        try {
            if (fileNameList != null) {
                exampleFiles = service.getMultipleExampleFilesInfo(fileNameList);
            } else {
                exampleFiles = service.getAllExampleFilesInfo();
            }
        } catch (JSONException | IOException | ExampleFileNotFoundException e) {
            return createReponseEntity(e);
        }
        return createReponseEntity(HttpStatus.OK, exampleFiles);
    }

    @GetMapping("/web/v1/exampleFile/download/{fileName}")
    @Operation(summary = "Download a file by filename.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "OK",
                        content = @Content(mediaType = "application/octet-stream")),
                @ApiResponse(
                        responseCode = "404",
                        description = "NOT FOUND",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = GeneralResponse.class)))
            })
    public ResponseEntity<Object> downloadExampleFile(
            @PathVariable String fileName, HttpServletRequest request) {

        try {
            return service.downloadExampleFile(fileName, request);
        } catch (IOException | ExampleFileNotFoundException e) {
            return createReponseEntity(e);
        }
    }

    /***************************************************************************************
     ************************************ POST METHODS *************************************
     ***************************************************************************************/

    @PostMapping(path = "/web/v1/exampleFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload an example file.",
            responses = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ExampleFileResponse.class)))
            })
    public ResponseEntity<Object> uploadAExampleFile(
            @RequestParam(value = "file", required = true) MultipartFile mFile) throws IOException {
        ResponseEntity<Object> responseEntity;
        try {
            ExampleFile exampleFile = service.uploadOneExampleFile(mFile);
            responseEntity =
                    createReponseEntity(
                            HttpStatus.CREATED,
                            String.format(
                                    "File with name: %s was uploaded successfully.",
                                    exampleFile.getFileName()),
                            exampleFile);
        } catch (ExampleFileAlreadyExistException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    @PostMapping(path = "/web/v1/exampleFiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple example files.")
    @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleFileUploadsResponse.class)))
    public ResponseEntity<Object> uploadMultipleExampleFiles(
            @RequestPart(required = true, value = "files") MultipartFile[] mFiles) {
        BatchCreateResponse batchCreateResponse = new BatchCreateResponse();
        for (MultipartFile mFile : mFiles) {
            try {
                ExampleFile exampleFile = service.uploadOneExampleFile(mFile);
                Object fileInfo = fileManagement.javaObjectToJsonObject(exampleFile);
                batchCreateResponse.addSuccess(fileInfo);
            } catch (IOException e) {
                ResponseMsg resp = new ResponseMsg(e);
                logger.warn("Exception thrown: ", resp);
                return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
            } catch (ExampleFileAlreadyExistException e) {
                String message =
                        String.format("Could not upload file : %s", mFile.getOriginalFilename());
                batchCreateResponse.addError(message);
            }
        }
        HttpStatus status =
                batchCreateResponse.containsSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        ResponseMsg resp = new ResponseMsg(status.value(), batchCreateResponse);
        logger.info(resp.getBody().get("message"));
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }

    /***************************************************************************************
     *********************************** DELETE METHODS *************************************
     ***************************************************************************************/

    @DeleteMapping("/web/v1/exampleFile/{fileName}")
    @Operation(summary = "Deletes an example file by filename.")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleFileResponse.class)))
    public ResponseEntity<Object> deleteAExampleFile(@PathVariable String fileName)
            throws JSONException, IOException {
        ResponseEntity<Object> responseEntity;
        try {
            ExampleFile exampleFile = service.deleteOneExampleFile(fileName);
            responseEntity =
                    createReponseEntity(
                            HttpStatus.OK,
                            String.format(
                                    "File with name: %s was deleted successfully.",
                                    exampleFile.getFileName()),
                            exampleFile);
        } catch (IOException | ExampleFileNotFoundException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    private ResponseEntity<Object> createReponseEntity(
            HttpStatus status, String msg, ExampleFile exampleFile) {
        Object userInfo = fileManagement.javaObjectToJsonObject(exampleFile);
        ResponseMsg resp = new ResponseMsg(status.value(), msg, userInfo);
        logger.info(resp.getBody().get("message"));
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }

    private ResponseEntity<Object> createReponseEntity(HttpStatus status, ExampleFile exampleFile) {
        Object userInfo = fileManagement.javaObjectToJsonObject(exampleFile);
        ResponseMsg resp = new ResponseMsg(status.value(), userInfo);
        logger.info(resp.getBody().get("message"));
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }

    private ResponseEntity<Object> createReponseEntity(
            HttpStatus status, List<ExampleFile> exampleFiles) {
        List<Object> exampleFileInfo =
                exampleFiles.stream()
                        .map(fileManagement::javaObjectToJsonObject)
                        .collect(Collectors.toList());
        ResponseMsg resp = new ResponseMsg(status.value(), exampleFileInfo);
        logger.info(resp.getBody().get("message"));
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }

    private ResponseEntity<Object> createReponseEntity(Exception e) {
        ResponseMsg resp = new ResponseMsg(e);
        logger.info(resp.getBody());
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }
}
