package com.employee.system.service;

import com.employee.system.dto.ManagerFeedbackDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.ManagerFeedback;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.ManagerFeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerFeedbackServiceTest {

    @Mock
    private ManagerFeedbackRepository feedbackRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ManagerFeedbackService managerFeedbackService;

    private Employee employee;
    private Employee manager;
    private ManagerFeedbackDTO feedbackDTO;
    private ManagerFeedback managerFeedback;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        manager = new Employee();
        manager.setId(2L);
        manager.setFirstName("Jane");
        manager.setLastName("Smith");

        feedbackDTO = new ManagerFeedbackDTO();
        feedbackDTO.setEmployeeId(1L);
        feedbackDTO.setProvidedById(2L);
        feedbackDTO.setFeedbackType("POSITIVE");
        feedbackDTO.setFeedbackCategory("PERFORMANCE");
        feedbackDTO.setFeedbackText("Great job on the project.");
        feedbackDTO.setRating(5);
        feedbackDTO.setActionItems("Continue maintaining high standards.");
        feedbackDTO.setFeedbackDate(LocalDate.of(2023, 10, 15));
        feedbackDTO.setStatus("SUBMITTED");

        managerFeedback = new ManagerFeedback();
        managerFeedback.setId(100L);
        managerFeedback.setEmployee(employee);
        managerFeedback.setProvidedBy(manager);
        managerFeedback.setFeedbackType("POSITIVE");
        managerFeedback.setFeedbackCategory("PERFORMANCE");
        managerFeedback.setFeedbackText("Great job on the project.");
        managerFeedback.setRating(5);
        managerFeedback.setActionItems("Continue maintaining high standards.");
        managerFeedback.setFeedbackDate(LocalDate.of(2023, 10, 15));
        managerFeedback.setStatus("SUBMITTED");
        managerFeedback.setCreatedAt(LocalDateTime.now());
        managerFeedback.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== provideFeedback ====================

    @Test
    @DisplayName("Given valid feedback DTO, when providing feedback, then return saved feedback DTO")
    void givenValidFeedbackDTO_whenProvideFeedback_thenReturnSavedFeedbackDTO() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(feedbackRepository.save(any(ManagerFeedback.class))).thenReturn(managerFeedback);

        // Act
        ManagerFeedbackDTO result = managerFeedbackService.provideFeedback(feedbackDTO);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals(2L, result.getProvidedById());
        assertEquals("Jane Smith", result.getProvidedByName());
        assertEquals("POSITIVE", result.getFeedbackType());
        assertEquals("PERFORMANCE", result.getFeedbackCategory());
        assertEquals("Great job on the project.", result.getFeedbackText());
        assertEquals(5, result.getRating());
        assertEquals("Continue maintaining high standards.", result.getActionItems());
        assertEquals(LocalDate.of(2023, 10, 15), result.getFeedbackDate());
        assertEquals("SUBMITTED", result.getStatus());
        verify(feedbackRepository, times(1)).save(any(ManagerFeedback.class));
    }

    @Test
    @DisplayName("Given non-existent employee ID, when providing feedback, then throw RuntimeException")
    void givenNonExistentEmployeeId_whenProvideFeedback_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());
        feedbackDTO.setEmployeeId(999L);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            managerFeedbackService.provideFeedback(feedbackDTO);
        });
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(feedbackRepository, never()).save(any(ManagerFeedback.class));
    }

    @Test
    @DisplayName("Given non-existent manager ID, when providing feedback, then throw RuntimeException")
    void givenNonExistentManagerId_whenProvideFeedback_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());
        feedbackDTO.setProvidedById(999L);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            managerFeedbackService.provideFeedback(feedbackDTO);
        });
        assertEquals("Manager not found with id: 999", exception.getMessage());
        verify(feedbackRepository, never()).save(any(ManagerFeedback.class));
    }

    @Test
    @DisplayName("Given null feedback date, when providing feedback, then set default date to today")
    void givenNullFeedbackDate_whenProvideFeedback_thenSetDefaultDateToToday() {
        // Arrange
        feedbackDTO.setFeedbackDate(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(feedbackRepository.save(any(ManagerFeedback.class))).thenReturn(managerFeedback);

        // Act
        ManagerFeedbackDTO result = managerFeedbackService.provideFeedback(feedbackDTO);

        // Assert
        assertNotNull(result);
        verify(feedbackRepository, times(1)).save(any(ManagerFeedback.class));
    }

    @Test
    @DisplayName("Given null status, when providing feedback, then set default status to DRAFT")
    void givenNullStatus_whenProvideFeedback_thenSetDefaultStatusToDraft() {
        // Arrange
        feedbackDTO.setStatus(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(feedbackRepository.save(any(ManagerFeedback.class))).thenReturn(managerFeedback);

        // Act
        ManagerFeedbackDTO result = managerFeedbackService.provideFeedback(feedbackDTO);

        // Assert
        assertNotNull(result);
        verify(feedbackRepository, times(1)).save(any(ManagerFeedback.class));
    }

    // ==================== getFeedbackById ====================

    @Test
    @DisplayName("Given existing feedback ID, when getting feedback by ID, then return feedback DTO")
    void givenExistingFeedbackId_whenGetFeedbackById_thenReturnFeedbackDTO() {
        // Arrange
        when(feedbackRepository.findById(100L)).thenReturn(Optional.of(managerFeedback));

        // Act
        ManagerFeedbackDTO result = managerFeedbackService.getFeedbackById(100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals(2L, result.getProvidedById());
        assertEquals("Jane Smith", result.getProvidedByName());
        assertEquals("POSITIVE", result.getFeedbackType());
        assertEquals("PERFORMANCE", result.getFeedbackCategory());
        assertEquals("Great job on the project.", result.getFeedbackText());
        assertEquals(5, result.getRating());
        assertEquals("Continue maintaining high standards.", result.getActionItems());
        assertEquals(LocalDate.of(2023, 10, 15), result.getFeedbackDate());
        assertEquals("SUBMITTED", result.getStatus());
    }

    @Test
    @DisplayName("Given non-existent feedback ID, when getting feedback by ID, then throw RuntimeException")
    void givenNonExistentFeedbackId_whenGetFeedbackById_thenThrowRuntimeException() {
        // Arrange
        when(feedbackRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            managerFeedbackService.getFeedbackById(999L);
        });
        assertEquals("Manager feedback not found with id: 999", exception.getMessage());
    }

    // ==================== getFeedbackByEmployee ====================

    @Test
    @DisplayName("Given existing employee with feedback, when getting feedback by employee, then return list of feedback DTOs")
    void givenExistingEmployeeWithFeedback_whenGetFeedbackByEmployee_thenReturnListOfFeedbackDTOs() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(feedbackRepository.findByEmployee(employee)).thenReturn(List.of(managerFeedback));

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByEmployee(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals("John Doe", result.get(0).getEmployeeName());
    }

    @Test
    @DisplayName("Given existing employee without feedback, when getting feedback by employee, then return empty list")
    void givenExistingEmployeeWithoutFeedback_whenGetFeedbackByEmployee_thenReturnEmptyList() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(feedbackRepository.findByEmployee(employee)).thenReturn(Collections.emptyList());

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByEmployee(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given non-existent employee ID, when getting feedback by employee, then throw RuntimeException")
    void givenNonExistentEmployeeId_whenGetFeedbackByEmployee_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            managerFeedbackService.getFeedbackByEmployee(999L);
        });
        assertEquals("Employee not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Given employee with multiple feedback entries, when getting feedback by employee, then return all feedback DTOs")
    void givenEmployeeWithMultipleFeedback_whenGetFeedbackByEmployee_thenReturnAllFeedbackDTOs() {
        // Arrange
        ManagerFeedback feedback2 = new ManagerFeedback();
        feedback2.setId(101L);
        feedback2.setEmployee(employee);
        feedback2.setProvidedBy(manager);
        feedback2.setFeedbackType("NEGATIVE");
        feedback2.setFeedbackCategory("BEHAVIOR");
        feedback2.setFeedbackText("Needs improvement.");
        feedback2.setRating(2);
        feedback2.setActionItems("Attend training.");
        feedback2.setFeedbackDate(LocalDate.of(2023, 11, 15));
        feedback2.setStatus("SUBMITTED");
        feedback2.setCreatedAt(LocalDateTime.now());
        feedback2.setUpdatedAt(LocalDateTime.now());

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(feedbackRepository.findByEmployee(employee)).thenReturn(List.of(managerFeedback, feedback2));

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByEmployee(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100L, result.get(0).getId());
        assertEquals(101L, result.get(1).getId());
    }

    // ==================== getFeedbackByManager ====================

    @Test
    @DisplayName("Given existing manager with feedback, when getting feedback by manager, then return list of feedback DTOs")
    void givenExistingManagerWithFeedback_whenGetFeedbackByManager_thenReturnListOfFeedbackDTOs() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(feedbackRepository.findByProvidedBy(manager)).thenReturn(List.of(managerFeedback));

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByManager(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getId());
        assertEquals(2L, result.get(0).getProvidedById());
        assertEquals("Jane Smith", result.get(0).getProvidedByName());
    }

    @Test
    @DisplayName("Given existing manager without feedback, when getting feedback by manager, then return empty list")
    void givenExistingManagerWithoutFeedback_whenGetFeedbackByManager_thenReturnEmptyList() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(manager));
        when(feedbackRepository.findByProvidedBy(manager)).thenReturn(Collections.emptyList());

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByManager(2L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given non-existent manager ID, when getting feedback by manager, then throw RuntimeException")
    void givenNonExistentManagerId_whenGetFeedbackByManager_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            managerFeedbackService.getFeedbackByManager(999L);
        });
        assertEquals("Manager not found with id: 999", exception.getMessage());
    }

    // ==================== getFeedbackByType ====================

    @Test
    @DisplayName("Given valid feedback type, when getting feedback by type, then return list of matching feedback DTOs")
    void givenValidFeedbackType_whenGetFeedbackByType_thenReturnListOfMatchingFeedbackDTOs() {
        // Arrange
        when(feedbackRepository.findByFeedbackType("POSITIVE")).thenReturn(List.of(managerFeedback));

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByType("POSITIVE");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("POSITIVE", result.get(0).getFeedbackType());
    }

    @Test
    @DisplayName("Given feedback type with no matches, when getting feedback by type, then return empty list")
    void givenFeedbackTypeWithNoMatches_whenGetFeedbackByType_thenReturnEmptyList() {
        // Arrange
        when(feedbackRepository.findByFeedbackType("UNKNOWN")).thenReturn(Collections.emptyList());

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByType("UNKNOWN");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getFeedbackByCategory ====================

    @Test
    @DisplayName("Given existing employee and valid category, when getting feedback by category, then return list of feedback DTOs")
    void givenExistingEmployeeAndValidCategory_whenGetFeedbackByCategory_thenReturnListOfFeedbackDTOs() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(feedbackRepository.findByEmployeeAndCategory(employee, "PERFORMANCE")).thenReturn(List.of(managerFeedback));

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByCategory(1L, "PERFORMANCE");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PERFORMANCE", result.get(0).getFeedbackCategory());
    }

    @Test
    @DisplayName("Given existing employee and category with no matches, when getting feedback by category, then return empty list")
    void givenExistingEmployeeAndCategoryWithNoMatches_whenGetFeedbackByCategory_thenReturnEmptyList() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(feedbackRepository.findByEmployeeAndCategory(employee, "UNKNOWN")).thenReturn(Collections.emptyList());

        // Act
        List<ManagerFeedbackDTO> result = managerFeedbackService.getFeedbackByCategory(1L, "UNKNOWN");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Given non-existent employee ID, when getting feedback by category, then throw RuntimeException")
    void givenNonExistentEmployeeId_whenGetFeedbackByCategory_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            managerFeedbackService.getFeedbackByCategory(999L, "PERFORMANCE");
        });
        assertEquals("Employee not found with id: 999", exception.getMessage());
    }

    // ==================== updateFeedback ====================

    @Test
    @DisplayName("Given existing feedback and valid update DTO, when updating feedback, then return updated feedback DTO")
    void givenExistingFeedbackAndValidUpdateDTO_whenUpdateFeedback_thenReturnUpdatedFeedbackDTO() {
        // Arrange
        ManagerFeedbackDTO updateDTO = new ManagerFeedbackDTO();
        updateDTO.setFeedbackType("NEGATIVE");
        updateDTO.setFeedbackCategory("BEHAVIOR");
        updateDTO.setFeedbackText("Needs improvement.");
        updateDTO.setRating(2);
        updateDTO.setActionItems("Attend training.");
        updateDTO.setStatus("SUBMITTED");

        when(feedbackRepository.findById(100L)).thenReturn(Optional.of(managerFeedback));
        when(feedbackRepository.save(any(ManagerFeedback.class))).thenReturn(managerFeedback);

        // Act
        ManagerFeedbackDTO result = managerFeedbackService.updateFeedback(100L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("NEGATIVE", result.getFeedbackType());
        assertEquals("BEHAVIOR", result.getFeedbackCategory());
        assertEquals("Needs improvement.", result.getFeedbackText());
        assertEquals(2, result.getRating());
        assertEquals("Attend training.", result.getActionItems());
        assertEquals("SUBMITTED", result.getStatus());
        verify(feedbackRepository, times(1)).save(any(ManagerFeedback.class));
    }

    @Test
    @DisplayName("Given non-existent feedback ID, when updating feedback, then throw RuntimeException")
    void givenNonExistentFeedbackId_whenUpdateFeedback_thenThrowRuntimeException() {
        // Arrange
        ManagerFeedbackDTO updateDTO = new ManagerFeedbackDTO();
        updateDTO.setFeedbackType("NEGATIVE");
        updateDTO.setFeedbackCategory("BEHAVIOR");
        updateDTO.setFeedbackText("Needs improvement.");
        updateDTO.setRating(2);
        updateDTO.setActionItems("Attend training.");
        updateDTO.setStatus("SUBMITTED");

        when(feedbackRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            managerFeedbackService.updateFeedback(999L, updateDTO);
        });
        assertEquals("Manager feedback not found with id: 999", exception.getMessage());
        verify(feedbackRepository, never()).save(any(ManagerFeedback.class));
    }

    // ==================== deleteFeedback ====================

    @Test
    @DisplayName("Given existing feedback ID, when deleting feedback, then delete feedback from repository")
    void givenExistingFeedbackId_whenDeleteFeedback_thenDeleteFeedbackFromRepository() {
        // Arrange
        when(feedbackRepository.findById(100L)).thenReturn(Optional.of(managerFeedback));

        // Act
        managerFeedbackService.deleteFeedback(100L);

        // Assert
        verify(feedbackRepository, times(1)).delete(managerFeedback);
    }

    @Test
    @DisplayName("Given non-existent feedback ID, when deleting feedback, then throw RuntimeException")
    void givenNonExistentFeedbackId_whenDeleteFeedback_thenThrowRuntimeException() {
        // Arrange
        when(feedbackRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            managerFeedbackService.deleteFeedback(999L);
        });
        assertEquals("Manager feedback not found with id: 999", exception.getMessage());
        verify(feedbackRepository, never()).delete(any(ManagerFeedback.class));
    }
}