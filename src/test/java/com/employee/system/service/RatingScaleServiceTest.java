import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class RatingScaleServiceTest {

    private RatingScaleService ratingScaleService;
    private RatingScaleRepository ratingScaleRepository;

    @BeforeEach
    public void setUp() {
        ratingScaleRepository = mock(RatingScaleRepository.class);
        ratingScaleService = new RatingScaleService(ratingScaleRepository);
    }

    @Test
    @DisplayName("Given non-existing rating scale ID, when deleting rating scale, then throw RuntimeException")
    public void givenNonExistingRatingScaleId_whenDeleteRatingScale_thenThrowRuntimeException() {
        // Arrange
        when(ratingScaleRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingScaleService.deleteRatingScale(999L);
        });

        assertEquals("Rating scale not found with id: 999", exception.getMessage());
        verify(ratingScaleRepository, times(1)).existsById(999L);
        verify(ratingScaleRepository, never()).deleteById(anyLong());
    }

}
