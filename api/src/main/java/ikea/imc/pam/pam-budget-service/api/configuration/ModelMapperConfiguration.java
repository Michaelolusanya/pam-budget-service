package org.imc.pam.boilerplate.api.configuration;

import org.imc.pam.boilerplate.api.models.exampleusers.ExampleUserDetailsDTO;
import org.imc.pam.boilerplate.entitymodels.ExampleUserDetails;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper
                .getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setPropertyCondition(Conditions.isNotNull());

        modelMapper
                .createTypeMap(ExampleUserDetails.class, ExampleUserDetailsDTO.class)
                .addMapping(
                        source -> source.getExampleUser().getId(),
                        ExampleUserDetailsDTO::setExampleUserId);

        return modelMapper;
    }
}
