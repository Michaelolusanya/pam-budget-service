package org.imc.pam.boilerplate.api.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.imc.pam.boilerplate.api.configuration.ResponseMsg;
import org.imc.pam.boilerplate.api.exampleresponsemodels.smokeTest.GeneralResponse;
import org.imc.pam.boilerplate.api.services.SmokeTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Smoke Test", description = "Running smoke test.")
@SecurityRequirement(name = "Open Id Connect")
@SecurityRequirement(name = "JWT-Token")
@ApiResponses(
        value = {
            @ApiResponse(
                    // responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(implementation = GeneralResponse.class))})
        })
public class SmokeTestController {

    private final SmokeTestService smokeTestService;

    public SmokeTestController(SmokeTestService smokeTestService) {
        this.smokeTestService = smokeTestService;
    }

    @GetMapping("/web/v1/smokeTest")
    public ResponseEntity<Object> runSmokeTest(
            @RequestHeader MultiValueMap<String, String> headers) {
        String token = headers.get("authorization").get(0);
        ResponseMsg resp = smokeTestService.runSmokeTest(token);
        return new ResponseEntity<>(resp.getBody(), resp.getHttpStatus());
    }
}
