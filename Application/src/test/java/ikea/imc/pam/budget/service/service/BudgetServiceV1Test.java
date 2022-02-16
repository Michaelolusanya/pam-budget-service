package ikea.imc.pam.budget.service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import ikea.imc.pam.budget.service.exception.BadRequestException;
import ikea.imc.pam.budget.service.repository.BudgetRepository;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceV1Test {

    private static final Long BUDGET_ID = 1L, BUDGET_ID_2 = 2L;
    private static final Long BUDGET_VERSION_ID = 3L;

    private static final String BUDGET_VERSION_NAME = "budname";
    private static final LocalDate BUDGET_VERSION_DATE = LocalDate.of(2020, 3, 1);
    private static final int BUDGET_VERSION_FISCAL_YEAR = 20;

    private static final Long ESTIMATED_BUDGET = 100_000L;
    private static final double COMDEV_COST = 50_000d;
    private static final Long PROJECT_ID = 2L;

    private static final String INPUT_FISCAL_YEAR = "FY20";

    @Mock private BudgetRepository repository;

    @InjectMocks private BudgetServiceV1 service;

    @Nested
    class GetByIdTest {

        @Test
        void notFound() {

            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            Optional<Budget> optionalBudget = service.getById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isEmpty());
        }

        @Test
        void oneFound() {

            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            Optional<Budget> optionalBudget = service.getById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(ESTIMATED_BUDGET, budget.getEstimatedBudget());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
            assertEquals(PROJECT_ID, budget.getProjectId());
        }
    }

    @Nested
    class ListBudgetsTest {

        @Test
        void nullInput() {

            // Given
            when(repository.findAll()).thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(null, null);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void emptyInput() {

            // Given
            when(repository.findAll()).thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(List.of(), List.of());

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchProjectIdAndNullFiscalYear() {

            // Given
            List<Long> projectIds = List.of(PROJECT_ID);
            when(repository.getBudgetByProjectId(projectIds))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(projectIds, null);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchFiscalYearAndNullProjectIds() {

            // Given
            List<String> fiscalYears = List.of(INPUT_FISCAL_YEAR);
            when(repository.getBudgetByFiscalYear(List.of(BUDGET_VERSION_FISCAL_YEAR)))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(null, fiscalYears);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchProjectIdAndEmptyFiscalYear() {

            // Given
            List<Long> projectIds = List.of(PROJECT_ID);
            when(repository.getBudgetByProjectId(projectIds))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(projectIds, List.of());

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchFiscalYearAndEmptyProjectIds() {

            // Given
            List<String> fiscalYears = List.of(INPUT_FISCAL_YEAR);
            when(repository.getBudgetByFiscalYear(List.of(BUDGET_VERSION_FISCAL_YEAR)))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(List.of(), fiscalYears);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void searchProjectIdsAndFiscalYear() {

            // Given
            List<Long> projectIds = List.of(PROJECT_ID);
            List<String> fiscalYears = List.of(INPUT_FISCAL_YEAR);
            when(repository.getBudgetByProjectIdAndFiscalYear(projectIds, List.of(BUDGET_VERSION_FISCAL_YEAR)))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(projectIds, fiscalYears);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }

        @Test
        void invalidFiscalYearString() {

            // Given
            List<String> fiscalYears = List.of("INVALID");

            // When
            BadRequestException exception =
                    assertThrows(BadRequestException.class, () -> service.listBudgets(List.of(), fiscalYears));

            // Then
            assertEquals("Fiscal year INVALID is malformed", exception.getMessage());
        }

        @Test
        void invalidFiscalYearStringYearPart() {

            // Given
            List<String> fiscalYears = List.of("FYYY");

            // When
            BadRequestException exception =
                    assertThrows(BadRequestException.class, () -> service.listBudgets(List.of(), fiscalYears));

            // Then
            assertEquals("Fiscal year FYYY is malformed", exception.getMessage());
        }
    }

    private static Budget generateBudget(Long id) {
        return Budget.builder()
                .budgetId(id)
                .estimatedBudget(ESTIMATED_BUDGET)
                .costCOMDEV(COMDEV_COST)
                .projectId(PROJECT_ID)
                .budgetVersion(generateBudgetVersion())
                .expenses(List.of())
                .build();
    }

    private static BudgetVersion generateBudgetVersion() {
        BudgetVersion budgetVersion = new BudgetVersion();
        budgetVersion.setBudgetVersionId(BUDGET_VERSION_ID);
        budgetVersion.setBudgetVersionName(BUDGET_VERSION_NAME);
        budgetVersion.setBudgetVersionDate(BUDGET_VERSION_DATE);
        budgetVersion.setFiscalYear(BUDGET_VERSION_FISCAL_YEAR);
        return budgetVersion;
    }
}
