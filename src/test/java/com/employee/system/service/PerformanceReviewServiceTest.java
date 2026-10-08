import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

public class PerformanceReviewServiceTest {

    private PerformanceReviewService performanceReviewService;
    private PerformanceReviewRepository performanceReviewRepository;
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp() {
        performanceReviewRepository = mock(PerformanceReviewRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        performanceReviewService = new PerformanceReviewService(performanceReviewRepository, employeeRepository);
    }

    @Test
    @DisplayName("Given non-existing review ID, when deleting review, then throw RuntimeException")
    public void givenNonExistingReviewId_whenDeleteReview_thenThrowRuntimeException() {
        // Arrange
        when(performanceReviewRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            performanceReviewService.deleteReview(999L);
        });

        assertEquals("Performance review not found with id: 999", exception.getMessage());
        verify(performanceReviewRepository, times(1)).existsById(999L);
        verify(performanceReviewRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Given existing review, when deleting review, then delete review")
    public void givenExistingReview_whenDeleteReview_thenDeleteReview() {
        // Arrange
        PerformanceReview review = new PerformanceReview();
        review.setId(100L);
        when(performanceReviewRepository.existsById(100L)).thenReturn(true);

        // Act
        performanceReviewService.deleteReview(100L);

        // Assert
        verify(performanceReviewRepository, times(1)).existsById(100L);
        verify(performanceReviewRepository, times(1)).deleteById(100L);
    }

    @Test
    @DisplayName("Given non-existing employee ID, when getting reviews by employee, then throw RuntimeException")
    public void givenNonExistingEmployeeId_whenGetReviewsByEmployee_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            performanceReviewService.getReviewsByEmployee(999L);
        });

        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository, times(1)).findById(999L);
        verify(performanceReviewRepository, never()).findByEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("Given non-existing manager ID, when getting reviews by manager, then throw RuntimeException")
    public void givenNonExistingManagerId_whenGetReviewsByManager_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            performanceReviewService.getReviewsByManager(999L);
        });

        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository, times(1)).findById(999L);
        verify(performanceReviewRepository, never()).findByReviewedBy(any(Employee.class));
    }

}
