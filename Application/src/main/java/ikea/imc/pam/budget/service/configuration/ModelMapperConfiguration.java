package ikea.imc.pam.budget.service.configuration;

import ikea.imc.pam.budget.service.api.dto.*;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.Expenses;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
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

        mapExpense(modelMapper);
        mapBudget(modelMapper);

        return modelMapper;
    }

    private void mapExpense(ModelMapper modelMapper) {
        modelMapper
                .createTypeMap(Expenses.class, ExpenseDTO.class)
                .addMapping(Expenses::getExpensesId, ExpenseDTO::setId)
                .addMapping(source -> source.getBudget().getBudgetId(), ExpenseDTO::setBudgetId)
                .addMapping(Expenses::getAssignmentId, ExpenseDTO::setAssignmentId)
                .addMapping(Expenses::getAssetTypeId, ExpenseDTO::setAssetTypeId)
                .addMappings(
                        mapper ->
                                mapper.using(toFractionConverter())
                                        .map(Expenses::getPercentCOMDEV, ExpenseDTO::setComdevFraction))
                .addMapping(Expenses::getCostPerUnit, ExpenseDTO::setUnitCost)
                .addMapping(Expenses::getUnits, ExpenseDTO::setUnitCount)
                .addMapping(Expenses::getWeeks, ExpenseDTO::setWeekCount)
                .addMapping(Expenses::getComment, ExpenseDTO::setComment)
                .addMapping(Expenses::getCostCOMDEV, ExpenseDTO::setComdevCost)
                .addMapping(Expenses::getInvoicingTypeName, ExpenseDTO::setPriceModel);

        modelMapper
                .createTypeMap(ExpenseDTO.class, Expenses.class)
                .addMapping(ExpenseDTO::getComdevCost, Expenses::setCostCOMDEV)
                .addMapping(ExpenseDTO::getUnitCost, Expenses::setCostPerUnit)
                .addMapping(ExpenseDTO::getUnitCount, Expenses::setUnits)
                .addMapping(ExpenseDTO::getWeekCount, Expenses::setWeeks)
                .addMapping(ExpenseDTO::getComment, Expenses::setComment)
                .addMappings(
                        mapper ->
                                mapper.using(fromFractionConverter())
                                        .map(ExpenseDTO::getComdevFraction, Expenses::setPercentCOMDEV));
    }

    private void mapBudget(ModelMapper modelMapper) {
        modelMapper
                .createTypeMap(Budget.class, BudgetDTO.class)
                .addMapping(Budget::getBudgetId, BudgetDTO::setId)
                .addMapping(Budget::getCostCOMDEV, BudgetDTO::setComdevCost)
                .addMapping(Budget::getEstimatedBudget, BudgetDTO::setEstimatedCost)
                .addMappings(
                        mapper ->
                                mapper.using(toFiscalYearConverter())
                                        .map(
                                                source -> source.getBudgetVersion().getFiscalYear(),
                                                BudgetDTO::setFiscalYear));

        modelMapper
                .createTypeMap(BudgetDTO.class, Budget.class)
                .addMapping(BudgetDTO::getProjectId, Budget::setProjectId)
                .addMapping(BudgetDTO::getEstimatedCost, Budget::setEstimatedBudget)
                .addMapping(BudgetDTO::getComdevCost, Budget::setCostCOMDEV);
    }

    private Converter<Integer, String> toFiscalYearConverter() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(Integer fiscalYear) {
                return "FY" + fiscalYear;
            }
        };
    }

    private Converter<Byte, Double> toFractionConverter() {
        return new AbstractConverter<>() {
            @Override
            protected Double convert(Byte percent) {
                return percent / 100d;
            }
        };
    }

    private Converter<Double, Byte> fromFractionConverter() {
        return new AbstractConverter<>() {
            @Override
            protected Byte convert(Double fraction) {
                double percent = (fraction * 100);
                return (byte) percent;
            }
        };
    }
}
