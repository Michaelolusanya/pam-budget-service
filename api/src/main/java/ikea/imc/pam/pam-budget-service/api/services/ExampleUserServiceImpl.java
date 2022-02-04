package org.imc.pam.boilerplate.api.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.imc.pam.boilerplate.api.configuration.ResponseMsg;
import org.imc.pam.boilerplate.entitymodels.ExampleUser;
import org.imc.pam.boilerplate.exceptions.ExampleUserAlreadyExistException;
import org.imc.pam.boilerplate.exceptions.ExampleUserNotFoundException;
import org.imc.pam.boilerplate.repositories.ExampleUserRepository;
import org.imc.pam.boilerplate.tools.UserManagement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExampleUserServiceImpl implements ExampleUserService {

    @Autowired private ExampleUserRepository repository;

    @Autowired private ModelMapper modelMapper;

    private UserManagement userManagement = new UserManagement();

    @Override
    public ExampleUser getExampleUserById(Long id) {
        Optional<ExampleUser> exampleUser = repository.findById(id);
        return exampleUser.orElseThrow(() -> new ExampleUserNotFoundException(id));
    }

    @Override
    public Optional<ExampleUser> findExampleUserById(Long id) {
        return repository.findById(id);
    }

    @Override
    public ExampleUser createUser(ExampleUser userToCreate) {
        Optional<ExampleUser> userWithEmail = repository.findByEmail(userToCreate.getEmail());
        if (userWithEmail.isEmpty()) {
            return repository.save(userToCreate);
        } else {
            String message =
                    String.format(
                            "ExampleUser with email %s already exist", userToCreate.getEmail());
            throw new ExampleUserAlreadyExistException(message);
        }
    }

    @Override
    public ExampleUser updateExampleUser(ExampleUser newExampleUser) {
        Optional<ExampleUser> userToUpdateOptional = repository.findById(newExampleUser.getId());
        Optional<ExampleUser> userWithEmail = repository.findByEmail(newExampleUser.getEmail());
        if (userToUpdateOptional.isEmpty()) {
            throw new ExampleUserNotFoundException(newExampleUser.getId());
        } else if (userWithEmail.isPresent()
                && !userWithEmail.get().getId().equals(userToUpdateOptional.get().getId())) {
            String message =
                    String.format(
                            "ExampleUser with email %s already exist", newExampleUser.getEmail());
            throw new ExampleUserAlreadyExistException(message);
        } else {
            ExampleUser userToUpdate = userToUpdateOptional.get();
            modelMapper.map(newExampleUser, userToUpdate);
            return repository.save(userToUpdate);
        }
    }

    @Override
    public ResponseMsg deleteExampleUser(Long id) {
        ExampleUser exampleUser =
                repository.findById(id).orElseThrow(() -> new ExampleUserNotFoundException(id));

        if (exampleUser.getExampleUserDetails().isEmpty()) {
            Object userInfo =
                    userManagement.javaObjectToJsonObject(repository.getById(exampleUser.getId()));
            repository.delete(exampleUser);
            return new ResponseMsg(
                    200, String.format("Example user with id: %d deleted", id), userInfo);
        } else {
            long[] exampleUserDetailsIdArray = new long[exampleUser.getExampleUserDetails().size()];
            for (int i = 0; i < exampleUserDetailsIdArray.length; i++) {
                exampleUserDetailsIdArray[i] = exampleUser.getExampleUserDetails().get(i).getId();
            }
            return new ResponseMsg(
                    400,
                    String.format(
                            "The exampleUsersDetails with id: %s must be deleted first",
                            Arrays.toString(exampleUserDetailsIdArray)));
        }
    }

    @Override
    public List<ExampleUser> getExampleUsers() {
        return repository.findAll();
    }

    @Override
    public List<ExampleUser> getExampleUsersByEmail(List<String> emails) {
        return emails.stream()
                .map((email) -> repository.findByEmail(email))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public ExampleUser patchExampleUser(ExampleUser patchExampleUser) {
        Optional<ExampleUser> userToUpdateOptional = repository.findById(patchExampleUser.getId());
        Optional<ExampleUser> userWithEmail = repository.findByEmail(patchExampleUser.getEmail());

        if (userToUpdateOptional.isEmpty()) {
            throw new ExampleUserNotFoundException(patchExampleUser.getId());
        } else if (userWithEmail.isPresent()
                && !userWithEmail.get().getId().equals(userToUpdateOptional.get().getId())) {
            throw new ExampleUserAlreadyExistException(
                    String.format(
                            "ExampleUser with email %s already exist",
                            patchExampleUser.getEmail()));
        } else {
            ExampleUser userToUpdate = userToUpdateOptional.get();
            modelMapper.map(patchExampleUser, userToUpdate);
            return repository.save(userToUpdate);
        }
    }
}
