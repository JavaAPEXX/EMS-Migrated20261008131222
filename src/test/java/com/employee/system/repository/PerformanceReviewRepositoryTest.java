import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

public class PerformanceReviewRepositoryTest {

    private PerformanceReviewRepository performanceReviewRepository;

    @BeforeEach
    public void setUp() {
        performanceReviewRepository = mock(PerformanceReviewRepository.class);
    }

    @Test
    @DisplayName("Given non-existing review ID, when checking existence, then return false")
    public void givenNonExistingReviewId_whenCheckExistence_thenReturnFalse() {
        // Arrange
        when(performanceReviewRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = performanceReviewRepository.existsById(999L);

        // Assert
        assertFalse(result);
        verify(performanceReviewRepository, times(1)).existsById(999L);
    }

    @Test
    @DisplayName("Given existing review, when checking existence, then return true")
    public void givenExistingReview_whenCheckExistence_thenReturnTrue() {
        // Arrange
        PerformanceReview review = new PerformanceReview();
        review.setId(100L);
        when(performanceReviewRepository.existsById(100L)).thenReturn(true);

        // Act
        boolean result = performanceReviewRepository.existsById(100L);

        // Assert
        assertTrue(result);
        verify(performanceReviewRepository, times(1)).existsById(100L);
    }
}
