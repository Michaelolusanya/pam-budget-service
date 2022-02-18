package ikea.imc.pam.budget.service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import ikea.imc.pam.budget.service.repository.BudgetRepository;
import ikea.imc.pam.budget.service.repository.BudgetVersionRepository;
import ikea.imc.pam.budget.service.repository.model.Budget;
import ikea.imc.pam.budget.service.repository.model.BudgetVersion;
import ikea.imc.pam.budget.service.repository.model.utils.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceV1Test {

    private static final Long BUDGET_ID = 1L;
    private static final Long BUDGET_ID_2 = 2L;
    private static final Long BUDGET_VERSION_ID = 3L;

    private static final String BUDGET_VERSION_NAME = "budname";
    private static final LocalDate BUDGET_VERSION_DATE = LocalDate.of(2020, 3, 1);

    private static final Long ESTIMATED_BUDGET = 100_000L;
    private static final Long ESTIMATED_BUDGET_2 = 200_000L;
    private static final double COMDEV_COST = 50_000d;
    private static final double COMDEV_COST_2 = 60_000d;
    private static final Long PROJECT_ID = 2L;

    private static final Integer FISCAL_YEAR = 2020;

    @Mock private BudgetRepository repository;

    @Mock private BudgetVersionRepository budgetVersionRepository;

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
            when(repository.getAllActive()).thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

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
            when(repository.getAllActive()).thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

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
            List<Integer> fiscalYears = List.of(FISCAL_YEAR);
            when(repository.getBudgetByFiscalYear(List.of(FISCAL_YEAR)))
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
            List<Integer> fiscalYears = List.of(FISCAL_YEAR);
            when(repository.getBudgetByFiscalYear(List.of(FISCAL_YEAR)))
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
            List<Integer> fiscalYears = List.of(FISCAL_YEAR);
            when(repository.getBudgetByProjectIdAndFiscalYear(projectIds, List.of(FISCAL_YEAR)))
                    .thenReturn(List.of(generateBudget(BUDGET_ID), generateBudget(BUDGET_ID_2)));

            // When
            List<Budget> budgets = service.listBudgets(projectIds, fiscalYears);

            // Then
            assertEquals(2, budgets.size());
            assertEquals(BUDGET_ID, budgets.get(0).getBudgetId());
            assertEquals(BUDGET_ID_2, budgets.get(1).getBudgetId());
        }
    }

    @Nested
    class DeleteBudgetTest {

        @Test
        void deleteNotFound() {

            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            Optional<Budget> optionalBudget = service.deleteById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isEmpty());
        }

        @Test
        void deleteAlreadyDeleted() {

            // Given
            Budget budget = generateBudget(BUDGET_ID);
            budget.setStatus(Status.ARCHIVED);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(budget));

            // When
            Optional<Budget> optionalBudget = service.deleteById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isEmpty());
        }

        @Test
        void deleteAsExpected() {

            // Given
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            Optional<Budget> optionalBudget = service.deleteById(BUDGET_ID);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(Status.ARCHIVED, budget.getStatus());
        }
    }

    @Nested
    class PatchBudgetTest {

        @Test
        void budgetNotFound() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.empty());

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);

            // Then
            assertTrue(optionalBudget.isEmpty());
        }

        @Test
        void updatedBudgetIsEmpty() {

            // Given
            Budget inputBudget = Budget.builder().build();
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
            assertEquals(Status.ACTIVE, budget.getStatus());
        }

        @Test
        void budgetContainsNoUpdate() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(COMDEV_COST, budget.getCostCOMDEV());
            assertEquals(Status.ACTIVE, budget.getStatus());
        }

        @Test
        void fiscalYearIsNull() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, null, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertNotNull(budget.getBudgetVersion());
            assertEquals(FISCAL_YEAR, budget.getBudgetVersion().getFiscalYear());
        }

        @Test
        void fiscalYearIsZero() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, 0, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertNotNull(budget.getBudgetVersion());
            assertEquals(FISCAL_YEAR, budget.getBudgetVersion().getFiscalYear());
        }

        @Test
        void budgetIsChanged() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            inputBudget.setEstimatedBudget(ESTIMATED_BUDGET_2);
            inputBudget.setCostCOMDEV(COMDEV_COST_2);

            ArgumentCaptor<Budget> budgetVersionCapture = ArgumentCaptor.forClass(Budget.class);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(generateBudget(BUDGET_ID)));
            when(repository.saveAndFlush(budgetVersionCapture.capture())).thenReturn(inputBudget);

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, FISCAL_YEAR, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertEquals(BUDGET_ID, budget.getBudgetId());
            assertEquals(PROJECT_ID, budget.getProjectId());
            assertEquals(ESTIMATED_BUDGET_2, budget.getEstimatedBudget());
            assertEquals(COMDEV_COST_2, budget.getCostCOMDEV());
            assertEquals(Status.ACTIVE, budget.getStatus());
        }

        @Test
        void fiscalYearIsChanged() {

            // Given
            Budget inputBudget = generateBudget(BUDGET_ID);
            Integer newFiscalYear = 2100;
            BudgetVersion updatedBudgetVersion = generateBudgetVersion();
            updatedBudgetVersion.setFiscalYear(newFiscalYear);
            ArgumentCaptor<BudgetVersion> budgetVersionCapture = ArgumentCaptor.forClass(BudgetVersion.class);
            when(repository.findById(BUDGET_ID)).thenReturn(Optional.of(inputBudget));
            when(budgetVersionRepository.saveAndFlush(budgetVersionCapture.capture())).thenReturn(updatedBudgetVersion);

            // When
            Optional<Budget> optionalBudget = service.patchBudget(BUDGET_ID, newFiscalYear, inputBudget);

            // Then
            assertTrue(optionalBudget.isPresent());
            Budget budget = optionalBudget.get();
            assertNotNull(budget.getBudgetVersion());
            assertEquals(newFiscalYear, budget.getBudgetVersion().getFiscalYear());
            assertEquals(newFiscalYear, budgetVersionCapture.getValue().getFiscalYear());
        }
    }

    private static Budget generateBudget(Long id) {
        return Budget.builder()
                .budgetId(id)
                .estimatedBudget(ESTIMATED_BUDGET)
                .costCOMDEV(COMDEV_COST)
                .projectId(PROJECT_ID)
                .status(Status.ACTIVE)
                .budgetVersion(generateBudgetVersion())
                .expenses(List.of())
                .build();
    }

    private static BudgetVersion generateBudgetVersion() {
        BudgetVersion budgetVersion = new BudgetVersion();
        budgetVersion.setBudgetVersionId(BUDGET_VERSION_ID);
        budgetVersion.setBudgetVersionName(BUDGET_VERSION_NAME);
        budgetVersion.setBudgetVersionDate(BUDGET_VERSION_DATE);
        budgetVersion.setFiscalYear(FISCAL_YEAR);
        return budgetVersion;
    }
}
