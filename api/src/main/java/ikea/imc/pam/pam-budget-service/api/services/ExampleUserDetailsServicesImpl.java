package org.imc.pam.boilerplate.api.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.imc.pam.boilerplate.entitymodels.ExampleUser;
import org.imc.pam.boilerplate.entitymodels.ExampleUserDetails;
import org.imc.pam.boilerplate.exceptions.ExampleUserDetailsNotFoundException;
import org.imc.pam.boilerplate.exceptions.ExampleUserNotFoundException;
import org.imc.pam.boilerplate.repositories.ExampleUserDetailsRepository;
import org.imc.pam.boilerplate.tools.UserDetailsManagement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExampleUserDetailsServicesImpl implements ExampleUserDetailsService {

    @Autowired private ExampleUserDetailsRepository repository;

    @Autowired private ExampleUserService exampleUserService;

    @Autowired private ModelMapper modelMapper;

    private UserDetailsManagement userDetailsManagement = new UserDetailsManagement();

    @Override
    public ExampleUserDetails getSingleExampleUserDetails(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> new ExampleUserDetailsNotFoundException(id));
    }

    @Override
    public ExampleUserDetails createExampleUserDetails(
            ExampleUserDetails newExampleUserDetails, Long exampleUserId) {
        Optional<ExampleUser> exampleUser = exampleUserService.findExampleUserById(exampleUserId);
        if (exampleUser.isEmpty()) {
            throw new ExampleUserNotFoundException(exampleUserId);
        } else {
            newExampleUserDetails.setExampleUser(exampleUser.get());
            return repository.save(newExampleUserDetails);
        }
    }

    @Override
    public List<ExampleUserDetails> getExampleUserDetails() {
        return repository.findAll();
    }

    @Override
    public List<ExampleUserDetails> getExampleUserDetailsByDateOfBirth(List<LocalDate> birthDates) {
        return repository.findAll().stream()
                .filter(
                        exampleUserDetails ->
                                birthDates.contains(exampleUserDetails.getDateOfBirth()))
                .collect(Collectors.toList());
    }

    @Override
    public ExampleUserDetails replaceExampleUserDetails(ExampleUserDetails newExampleUserDetails) {
        Optional<ExampleUserDetails> exampleUserDetailsToUpdate =
                repository.findById(newExampleUserDetails.getId());
        if (exampleUserDetailsToUpdate.isEmpty()) {
            throw new ExampleUserDetailsNotFoundException(newExampleUserDetails.getId());
        } else {
            return repository.save(newExampleUserDetails);
        }
    }

    @Override
    public ExampleUserDetails patchExampleUserDetails(ExampleUserDetails patchExampleUserDetails) {
        Optional<ExampleUserDetails> exampleUserDetailsToUpdateOptional =
                repository.findById(patchExampleUserDetails.getId());
        if (exampleUserDetailsToUpdateOptional.isEmpty()) {
            throw new ExampleUserDetailsNotFoundException(patchExampleUserDetails.getId());
        } else {
            ExampleUserDetails exampleUserDetailsToUpdate =
                    exampleUserDetailsToUpdateOptional.get();
            modelMapper.map(patchExampleUserDetails, exampleUserDetailsToUpdate);
            return repository.save(exampleUserDetailsToUpdate);
        }
    }

    @Override
    public ExampleUserDetails deleteExampleUserDetails(Long id) {
        ExampleUserDetails exampleUserDetails =
                repository
                        .findById(id)
                        .orElseThrow(() -> new ExampleUserDetailsNotFoundException(id));
        repository.delete(exampleUserDetails);
        return exampleUserDetails;
    }
}
