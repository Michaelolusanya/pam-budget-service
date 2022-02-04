package org.imc.pam.boilerplate.entitymodels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import javax.persistence.*;

@Entity
public class ExampleUserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adress;

    private String phonenumber;

    private LocalDate dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "exampleUser_id")
    @JsonIgnore
    private ExampleUser exampleUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public ExampleUser getExampleUser() {
        return exampleUser;
    }

    public void setExampleUser(ExampleUser exampleUser) {
        this.exampleUser = exampleUser;
    }

    @Override
    public String toString() {
        return "ExampleUserDetails{"
                + "id="
                + id
                + ", adress='"
                + adress
                + '\''
                + ", phonenumber='"
                + phonenumber
                + '\''
                + ", dateOfBirth="
                + dateOfBirth
                + '}';
    }
}
