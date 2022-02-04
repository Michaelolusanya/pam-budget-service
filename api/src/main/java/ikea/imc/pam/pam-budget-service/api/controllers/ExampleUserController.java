package org.imc.pam.boilerplate.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imc.pam.boilerplate.api.configuration.ResponseMsg;
import org.imc.pam.boilerplate.api.exampleresponsemodels.common.BatchSuccessResponse;
import org.imc.pam.boilerplate.api.exampleresponsemodels.common.MessageSuccessResponse;
import org.imc.pam.boilerplate.api.exampleresponsemodels.exampleuser.ExampleUserSuccessResponse;
import org.imc.pam.boilerplate.api.exampleresponsemodels.exampleuser.ExampleUsersSuccessResponse;
import org.imc.pam.boilerplate.api.models.BatchCreateResponse;
import org.imc.pam.boilerplate.api.models.exampleusers.ExampleUserDTO;
import org.imc.pam.boilerplate.api.services.ExampleUserService;
import org.imc.pam.boilerplate.entitymodels.ExampleUser;
import org.imc.pam.boilerplate.exceptions.ExampleUserAlreadyExistException;
import org.imc.pam.boilerplate.exceptions.ExampleUserNotFoundException;
import org.imc.pam.boilerplate.tools.UserManagement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/web/v1")
@Tag(name = "Example users", description = "Apis for example user")
@SecurityRequirement(name = "Open Id Connect")
@SecurityRequirement(name = "JWT-Token")
public class ExampleUserController {

    private static Logger logger = LogManager.getLogger(ExampleUserController.class);

    @Autowired private ExampleUserService service;

    @Autowired private ModelMapper mapper;

    private UserManagement userManagement = new UserManagement();

    @GetMapping("exampleUser/{id}")
    @Operation(summary = "Get an example user by id.")
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
                                                                ExampleUserSuccessResponse.class)))
            })
    public ResponseEntity<Object> getExampleUserById(@PathVariable Long id) {
        ResponseEntity<Object> responseEntity;
        try {
            ExampleUser exampleUser = service.getExampleUserById(id);
            responseEntity = createReponseEntity(HttpStatus.OK, exampleUser);
        } catch (ExampleUserNotFoundException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    @PostMapping("exampleUser")
    @Operation(summary = "Create an example user for the given input.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ExampleUserSuccessResponse.class)))
            })
    public ResponseEntity<Object> createExampleUser(
            @Valid @RequestBody ExampleUserDTO exampleUserDTO) {
        ResponseEntity<Object> responseEntity;
        try {
            ExampleUser userToCreate = mapper.map(exampleUserDTO, ExampleUser.class);
            ExampleUser createdUser = service.createUser(userToCreate);
            responseEntity =
                    createReponseEntity(
                            HttpStatus.CREATED,
                            String.format(
                                    "User with email: %s was created successfully.",
                                    createdUser.getEmail()),
                            createdUser);
        } catch (ExampleUserAlreadyExistException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    @PutMapping("exampleUser/{id}")
    @Operation(summary = "Update an example user for the given input.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ExampleUserSuccessResponse.class)))
            })
    public ResponseEntity<Object> updateExampleUser(
            @PathVariable Long id, @Valid @RequestBody ExampleUserDTO exampleUserDTO) {
        ResponseEntity<Object> responseEntity;
        try {
            exampleUserDTO.setId(id);
            ExampleUser userToUpdate = mapper.map(exampleUserDTO, ExampleUser.class);
            ExampleUser exampleUserUpdated = service.updateExampleUser(userToUpdate);
            responseEntity =
                    createReponseEntity(
                            HttpStatus.OK,
                            String.format(
                                    "User with id: %d was updated successfully.",
                                    exampleUserUpdated.getId()),
                            exampleUserUpdated);
        } catch (ExampleUserNotFoundException | ExampleUserAlreadyExistException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    // För komplex för att ändra? Fråga dino
    @DeleteMapping("exampleUser/{id}")
    @Operation(summary = "Delete an example user by id.")
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
                                                                MessageSuccessResponse.class)))
            })
    public ResponseEntity<Object> deleteExampleUser(@PathVariable Long id) {
        try {
            ResponseMsg resp = service.deleteExampleUser(id);
            logger.info(resp.getBody().get("message"));
            return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
        } catch (ExampleUserNotFoundException e) {
            ResponseMsg resp = new ResponseMsg(e);
            logger.warn(resp.getBody());
            return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
        }
    }

    @GetMapping("exampleUsers")
    @Operation(summary = "Get all example users.")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ExampleUsersSuccessResponse.class)))
    public ResponseEntity<Object> getExampleUsers(
            @RequestParam(required = false) List<String> emails) {
        ResponseEntity<Object> responseEntity;
        List<ExampleUser> exampleUsers;
        if (emails == null || emails.isEmpty()) {
            exampleUsers = service.getExampleUsers();
        } else {
            exampleUsers = service.getExampleUsersByEmail(emails);
        }
        responseEntity = createReponseEntity(HttpStatus.OK, exampleUsers);
        return responseEntity;
    }

    @PostMapping("exampleUsers")
    @Operation(summary = "Creates multiple example users.")
    @ApiResponse(
            responseCode = "200",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BatchSuccessResponse.class)))
    public ResponseEntity<Object> createExampleUsers(
            @NotEmpty @RequestBody List<@Valid ExampleUserDTO> exampleUsersDTO) {
        BatchCreateResponse batchCreateResponse = new BatchCreateResponse();
        for (ExampleUserDTO exampleUserDTO : exampleUsersDTO) {
            ExampleUser exampleUser = mapper.map(exampleUserDTO, ExampleUser.class);
            try {
                ExampleUser createdUser = service.createUser(exampleUser);
                Object userInfo = userManagement.javaObjectToJsonObject(createdUser);
                batchCreateResponse.addSuccess(userInfo);
            } catch (Exception ex) {
                String message = String.format("Could not create example user : %s", exampleUser);
                batchCreateResponse.addError(message);
            }
        }
        HttpStatus status =
                batchCreateResponse.containsSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        ResponseMsg responseMessage = new ResponseMsg(status.value(), batchCreateResponse);
        return new ResponseEntity<>(responseMessage.getBody(), responseMessage.getHttpStatus());
    }

    @PatchMapping("exampleUser/{id}")
    @Operation(summary = "Patch the given example user.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                ExampleUserSuccessResponse.class)))
            })
    public ResponseEntity<Object> patchExampleUser(
            @PathVariable Long id, @RequestBody ExampleUserDTO exampleUserDTO) {
        ResponseEntity<Object> responseEntity;
        try {
            exampleUserDTO.setId(id);
            ExampleUser userToUpdate = mapper.map(exampleUserDTO, ExampleUser.class);
            ExampleUser exampleUserUpdated = service.patchExampleUser(userToUpdate);
            responseEntity =
                    createReponseEntity(
                            HttpStatus.OK,
                            String.format(
                                    "User with id: %d was updated successfully.",
                                    exampleUserUpdated.getId()),
                            exampleUserUpdated);
        } catch (ExampleUserNotFoundException | ExampleUserAlreadyExistException e) {
            responseEntity = createReponseEntity(e);
        }
        return responseEntity;
    }

    private ResponseEntity<Object> createReponseEntity(
            HttpStatus status, String msg, ExampleUser exampleUser) {
        Object userInfo = userManagement.javaObjectToJsonObject(exampleUser);
        ResponseMsg responseMsg = new ResponseMsg(status.value(), msg, userInfo);
        return new ResponseEntity<>(responseMsg.getBody(), responseMsg.getHttpStatus());
    }

    private ResponseEntity<Object> createReponseEntity(HttpStatus status, ExampleUser exampleUser) {
        Object userInfo = userManagement.javaObjectToJsonObject(exampleUser);
        ResponseMsg responseMsg = new ResponseMsg(status.value(), userInfo);
        return new ResponseEntity<>(responseMsg.getBody(), responseMsg.getHttpStatus());
    }

    private ResponseEntity<Object> createReponseEntity(
            HttpStatus status, List<ExampleUser> exampleUsers) {
        List<Object> exampleUsersInfo =
                exampleUsers.stream()
                        .map(userManagement::javaObjectToJsonObject)
                        .collect(Collectors.toList());
        ResponseMsg responseMsg = new ResponseMsg(status.value(), exampleUsersInfo);
        return new ResponseEntity<>(responseMsg.getBody(), responseMsg.getHttpStatus());
    }

    private ResponseEntity<Object> createReponseEntity(Exception e) {
        ResponseMsg resp = new ResponseMsg(e);
        logger.info(resp.getBody());
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }
}
