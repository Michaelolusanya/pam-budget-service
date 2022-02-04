package org.imc.pam.boilerplate.api.models.exampleusers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExampleUserDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(required = true)
    @NotNull
    private String email;

    @JsonProperty(required = true)
    @NotNull
    private String firstName;

    @JsonProperty(required = true)
    @NotNull
    private String lastName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<ExampleUserDetailsDTO> exampleUserDetails = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    public List<ExampleUserDetailsDTO> getExampleUserDetails() {
        return this.exampleUserDetails;
    }

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    public void setExampleUserDetails(List<ExampleUserDetailsDTO> exampleUserDetails) {
        this.exampleUserDetails = exampleUserDetails;
    }
}
