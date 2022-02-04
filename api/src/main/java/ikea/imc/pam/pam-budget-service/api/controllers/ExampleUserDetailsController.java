package org.imc.pam.boilerplate.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imc.pam.boilerplate.api.configuration.ResponseMsg;
import org.imc.pam.boilerplate.api.exampleresponsemodels.common.BatchSuccessResponse;
import org.imc.pam.boilerplate.api.exampleresponsemodels.exampleuserdetails.ExampleUserDetailsSuccessResponse;
import org.imc.pam.boilerplate.api.exampleresponsemodels.exampleuserdetails.ExampleUsersDetailsSuccessResponse;
import org.imc.pam.boilerplate.api.models.BatchCreateResponse;
import org.imc.pam.boilerplate.api.models.exampleusers.ExampleUserDetailsDTO;
import org.imc.pam.boilerplate.api.services.ExampleUserDetailsService;
import org.imc.pam.boilerplate.entitymodels.ExampleUserDetails;
import org.imc.pam.boilerplate.exceptions.ExampleUserDetailsAlreadyExistsException;
import org.imc.pam.boilerplate.exceptions.ExampleUserDetailsNotFoundException;
import org.imc.pam.boilerplate.exceptions.ExampleUserNotFoundException;
import org.imc.pam.boilerplate.tools.UserDetailsManagement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/web/v1")
@Tag(name = "Example users details", description = "Apis for example user details")
@SecurityRequirement(name = "Open Id Connect")
@SecurityRequirement(name = "JWT-Token")
public class ExampleUserDetailsController {

    private static Logger logger = LogManager.getLogger(ExampleUserDetailsController.class);

    @Autowired private ExampleUserDetailsService service;

    @Autowired private ModelMapper mapper;

    private UserDetailsManagement userDetailsManagement = new UserDetailsManagement();

    @GetMapping("exampleUserDetails/{id}")
    @Operation(summary = "Get example user details by id.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ExampleUserDetailsSuccessResponse
                                                                        .class)))
            })
    public ResponseEntity<Object> getSingleExampleUserDetails(@PathVariable Long id) {
        ResponseEntity<Object> responseEntity;
        try {
            ExampleUserDetails exampleUserDetails = service.getSingleExampleUserDetails(id);
            responseEntity = createReponseEntity(HttpStatus.OK, exampleUserDetails);
        } catch (ExampleUserDetailsNotFoundException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    // Kanske göra om NOTE
    @GetMapping("exampleUsersDetails")
    @Operation(
            summary =
                    "Get all example user details that matches query. If no query is provided get all example user details")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema =
                                    @Schema(
                                            implementation =
                                                    ExampleUsersDetailsSuccessResponse.class)))
    public ResponseEntity<Object> getExampleUserDetails(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                    List<LocalDate> birthDates) {
        List<ExampleUserDetails> exampleUserDetails;
        if (birthDates == null || birthDates.isEmpty()) {
            exampleUserDetails = service.getExampleUserDetails();
        } else {
            exampleUserDetails = service.getExampleUserDetailsByDateOfBirth(birthDates);
        }
        return createReponseEntity(HttpStatus.OK, exampleUserDetails);
    }

    @PostMapping("exampleUserDetails")
    @Operation(summary = "Create example user details")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ExampleUserDetailsSuccessResponse
                                                                        .class)))
            })
    public ResponseEntity<Object> createExampleUserDetails(
            @RequestBody ExampleUserDetailsDTO exampleUserDetailsDTO) {
        ResponseEntity<Object> responseEntity;
        try {
            Long exampleUserId = exampleUserDetailsDTO.getExampleUserId();
            ExampleUserDetails exampleUserDetailsToCreate =
                    mapper.map(exampleUserDetailsDTO, ExampleUserDetails.class);
            ExampleUserDetails createdUserDetails =
                    service.createExampleUserDetails(exampleUserDetailsToCreate, exampleUserId);
            responseEntity =
                    createReponseEntity(
                            HttpStatus.CREATED,
                            String.format(
                                    "UserDetails with id: %d was created successfully.",
                                    createdUserDetails.getId()),
                            createdUserDetails);
        } catch (ExampleUserDetailsAlreadyExistsException | ExampleUserNotFoundException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    @PostMapping("exampleUsersDetails")
    @Operation(summary = "Creates multiple example users.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                BatchSuccessResponse.class))),
            })
    public ResponseEntity<Object> createExampleUsersDetails(
            @NotNull @RequestBody List<@Valid ExampleUserDetailsDTO> exampleUsersDetailsDTO) {
        BatchCreateResponse batchCreateResponse = new BatchCreateResponse();
        for (ExampleUserDetailsDTO exampleUserDetailsDTO : exampleUsersDetailsDTO) {
            ExampleUserDetails exampleUserDetails =
                    mapper.map(exampleUserDetailsDTO, ExampleUserDetails.class);
            try {
                Long exampleUserId = exampleUserDetailsDTO.getExampleUserId();
                ExampleUserDetails createdUserDetails =
                        service.createExampleUserDetails(exampleUserDetails, exampleUserId);
                Object userDetailsInfo =
                        userDetailsManagement.javaObjectToJsonObject(createdUserDetails);
                batchCreateResponse.addSuccess(userDetailsInfo);
            } catch (Exception ex) {
                String message =
                        String.format("Could not create example user : %s", exampleUserDetails);
                batchCreateResponse.addError(message);
            }
        }
        HttpStatus status =
                batchCreateResponse.containsSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        ResponseMsg resp = new ResponseMsg(status.value(), batchCreateResponse);
        logger.info(resp.getBody().get("message"));
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }

    @PutMapping("exampleUserDetails/{id}")
    @Operation(summary = "Update example user details for the given input.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ExampleUserDetailsSuccessResponse
                                                                        .class)))
            })
    public ResponseEntity<Object> putExampleUserDetails(
            @PathVariable Long id,
            @Valid @RequestBody ExampleUserDetailsDTO exampleUserDetailsDTO) {
        ResponseEntity<Object> responseEntity;
        try {
            exampleUserDetailsDTO.setId(id);
            ExampleUserDetails userDetailsToUpdate =
                    mapper.map(exampleUserDetailsDTO, ExampleUserDetails.class);
            ExampleUserDetails updatedUserDetails =
                    service.replaceExampleUserDetails(userDetailsToUpdate);
            responseEntity =
                    createReponseEntity(
                            HttpStatus.OK,
                            String.format(
                                    "UserDetails with id: %d was updated successfully.",
                                    updatedUserDetails.getId()),
                            updatedUserDetails);
        } catch (ExampleUserDetailsNotFoundException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    @PatchMapping("exampleUserDetails/{id}")
    @Operation(summary = "Patch example user details for the given input.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ExampleUserDetailsSuccessResponse
                                                                        .class)))
            })
    public ResponseEntity<Object> patchExampleUserDetails(
            @PathVariable Long id, @RequestBody ExampleUserDetailsDTO exampleUserDetailsDTO) {
        ResponseEntity<Object> responseEntity;
        try {
            exampleUserDetailsDTO.setId(id);
            ExampleUserDetails exampleUserDetailsToUpdate =
                    mapper.map(exampleUserDetailsDTO, ExampleUserDetails.class);
            ExampleUserDetails updatedUserDetails =
                    service.patchExampleUserDetails(exampleUserDetailsToUpdate);
            responseEntity =
                    createReponseEntity(
                            HttpStatus.OK,
                            String.format(
                                    "UserDetails with id: %d was updated successfully.",
                                    updatedUserDetails.getId()),
                            updatedUserDetails);
        } catch (ExampleUserDetailsNotFoundException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    @DeleteMapping("exampleUserDetails/{id}")
    @Operation(summary = "Delete example user details.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ExampleUserDetailsSuccessResponse
                                                                        .class)))
            })
    public ResponseEntity<Object> deleteExampleUserDetails(@PathVariable Long id) {
        ResponseEntity<Object> responseEntity;
        try {
            ExampleUserDetails exampleUserDetails = service.deleteExampleUserDetails(id);
            responseEntity =
                    createReponseEntity(
                            HttpStatus.OK,
                            "UserDetails was deleted successfully.",
                            exampleUserDetails);
        } catch (ExampleUserDetailsNotFoundException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    private ResponseEntity<Object> createReponseEntity(
            HttpStatus status, String msg, ExampleUserDetails exampleUserDetails) {
        Object userDetailsInfo = userDetailsManagement.javaObjectToJsonObject(exampleUserDetails);
        ResponseMsg responseMsg = new ResponseMsg(status.value(), msg, userDetailsInfo);
        return new ResponseEntity<>(responseMsg.getBody(), responseMsg.getHttpStatus());
    }

    private ResponseEntity<Object> createReponseEntity(
            HttpStatus status, ExampleUserDetails exampleUserDetails) {
        Object userDetailsInfo = userDetailsManagement.javaObjectToJsonObject(exampleUserDetails);
        ResponseMsg responseMsg = new ResponseMsg(status.value(), userDetailsInfo);
        logger.info(responseMsg.getBody().get("message"));
        return new ResponseEntity<>(responseMsg.getBody(), responseMsg.getHttpStatus());
    }

    private ResponseEntity<Object> createReponseEntity(
            HttpStatus status, List<ExampleUserDetails> exampleUsersDetails) {
        List<Object> exampleUsersDetailsInfo =
                exampleUsersDetails.stream()
                        .map(userDetailsManagement::javaObjectToJsonObject)
                        .collect(Collectors.toList());
        ResponseMsg resp = new ResponseMsg(status.value(), exampleUsersDetailsInfo);
        logger.info(resp.getBody().get("message"));
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }

    private ResponseEntity<Object> createReponseEntity(Exception e) {
        ResponseMsg resp = new ResponseMsg(e);
        logger.info(resp.getBody());
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }
}
