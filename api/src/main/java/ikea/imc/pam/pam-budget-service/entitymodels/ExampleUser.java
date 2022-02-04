package org.imc.pam.boilerplate.entitymodels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@NamedQueries({
    @NamedQuery(
            name = "ExampleUser.findByEmail",
            query = "SELECT eu FROM ExampleUser eu WHERE eu.email = :email")
})
@Entity
public class ExampleUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "exampleUser")
    @JsonIgnore
    private List<ExampleUserDetails> exampleUserDetails = new ArrayList<>();

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

    public List<ExampleUserDetails> getExampleUserDetails() {
        return this.exampleUserDetails;
    }

    public void setExampleUserDetails(List<ExampleUserDetails> exampleUserDetails) {
        this.exampleUserDetails = exampleUserDetails;
    }
}
