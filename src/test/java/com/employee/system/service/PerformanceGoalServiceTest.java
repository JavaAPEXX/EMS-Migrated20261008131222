package com.employee.system.service;

import com.employee.system.dto.PerformanceGoalDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.PerformanceGoal;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.PerformanceGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PerformanceGoalServiceTest {

    @Mock
    private PerformanceGoalRepository goalRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private PerformanceGoalService performanceGoalService;

    private PerformanceGoalDTO goalDTO;
    private PerformanceGoal goal;
    private Employee employee;

    @BeforeEach
    public void setUp() {
        goalDTO = new PerformanceGoalDTO();
        goalDTO.setEmployeeId(1L);
        goalDTO.setGoalTitle("Performance Improvement");
        goalDTO.setGoalDescription("Improve sales by 10% in the next quarter");
        goalDTO.setCategory("Sales");
        goalDTO.setStartDate(LocalDate.now());
        goalDTO.setTargetDate(LocalDate.now().plusMonths(3));
        goalDTO.setStatus("DRAFT");
        goalDTO.setPriority(1);
        goalDTO.setProgressPercentage(0);
        goalDTO.setKeyResults("Increase sales by 5% in the first month");
        goalDTO.setRemarks("None");

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        goal = new PerformanceGoal();
        goal.setId(1L);
        goal.setEmployee(employee);
        goal.setGoalTitle(goalDTO.getGoalTitle());
        goal.setGoalDescription(goalDTO.getGoalDescription());
        goal.setCategory(goalDTO.getCategory());
        goal.setStartDate(goalDTO.getStartDate());
        goal.setTargetDate(goalDTO.getTargetDate());
        goal.setStatus(goalDTO.getStatus());
        goal.setPriority(goalDTO.getPriority());
        goal.setProgressPercentage(goalDTO.getProgressPercentage());
        goal.setKeyResults(goalDTO.getKeyResults());
        goal.setRemarks(goalDTO.getRemarks());
        goal.setCreatedAt(LocalDate.now());
        goal.setUpdatedAt(LocalDate.now());
    }

    @Test
    @DisplayName("givenValidInput_whenCreateGoal_thenReturnSuccess")
    public void givenValidInput_whenCreateGoal_thenReturnSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(goalRepository.save(goal)).thenReturn(goal);

        // Act
        PerformanceGoalDTO result = performanceGoalService.createGoal(goalDTO);

        // Assert
        assertEquals(goalDTO.getId(), result.getId());
        assertEquals(goalDTO.getEmployeeId(), result.getEmployeeId());
        assertEquals(goalDTO.getGoalTitle(), result.getGoalTitle());
        assertEquals(goalDTO.getGoalDescription(), result.getGoalDescription());
        assertEquals(goalDTO.getCategory(), result.getCategory());
        assertEquals(goalDTO.getStartDate(), result.getStartDate());
        assertEquals(goalDTO.getTargetDate(), result.getTargetDate());
        assertEquals(goalDTO.getStatus(), result.getStatus());
        assertEquals(goalDTO.getPriority(), result.getPriority());
        assertEquals(goalDTO.getProgressPercentage(), result.getProgressPercentage());
        assertEquals(goalDTO.getKeyResults(), result.getKeyResults());
        assertEquals(goalDTO.getRemarks(), result.getRemarks());
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenCreateGoal_thenThrowException")
    public void givenNonExistingEmployeeId_whenCreateGoal_thenThrowException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.createGoal(goalDTO));
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).save(goal);
    }

    @Test
    @DisplayName("givenValidInput_whenGetGoalById_thenReturnSuccess")
    public void givenValidInput_whenGetGoalById_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        // Act
        PerformanceGoalDTO result = performanceGoalService.getGoalById(1L);

        // Assert
        assertEquals(goalDTO.getId(), result.getId());
        assertEquals(goalDTO.getEmployeeId(), result.getEmployeeId());
        assertEquals(goalDTO.getGoalTitle(), result.getGoalTitle());
        assertEquals(goalDTO.getGoalDescription(), result.getGoalDescription());
        assertEquals(goalDTO.getCategory(), result.getCategory());
        assertEquals(goalDTO.getStartDate(), result.getStartDate());
        assertEquals(goalDTO.getTargetDate(), result.getTargetDate());
        assertEquals(goalDTO.getStatus(), result.getStatus());
        assertEquals(goalDTO.getPriority(), result.getPriority());
        assertEquals(goalDTO.getProgressPercentage(), result.getProgressPercentage());
        assertEquals(goalDTO.getKeyResults(), result.getKeyResults());
        assertEquals(goalDTO.getRemarks(), result.getRemarks());
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenGetGoalById_thenThrowException")
    public void givenNonExistingGoalId_whenGetGoalById_thenThrowException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.getGoalById(1L));
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenValidEmployeeId_whenGetGoalsByEmployee_thenReturnSuccess")
    public void givenValidEmployeeId_whenGetGoalsByEmployee_thenReturnSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(goalRepository.findByEmployee(employee)).thenReturn(Arrays.asList(goal));

        // Act
        List<PerformanceGoalDTO> result = performanceGoalService.getGoalsByEmployee(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(goalDTO.getId(), result.get(0).getId());
        assertEquals(goalDTO.getEmployeeId(), result.get(0).getEmployeeId());
        assertEquals(goalDTO.getGoalTitle(), result.get(0).getGoalTitle());
        assertEquals(goalDTO.getGoalDescription(), result.get(0).getGoalDescription());
        assertEquals(goalDTO.getCategory(), result.get(0).getCategory());
        assertEquals(goalDTO.getStartDate(), result.get(0).getStartDate());
        assertEquals(goalDTO.getTargetDate(), result.get(0).getTargetDate());
        assertEquals(goalDTO.getStatus(), result.get(0).getStatus());
        assertEquals(goalDTO.getPriority(), result.get(0).getPriority());
        assertEquals(goalDTO.getProgressPercentage(), result.get(0).getProgressPercentage());
        assertEquals(goalDTO.getKeyResults(), result.get(0).getKeyResults());
        assertEquals(goalDTO.getRemarks(), result.get(0).getRemarks());
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenGetGoalsByEmployee_thenThrowException")
    public void givenNonExistingEmployeeId_whenGetGoalsByEmployee_thenThrowException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.getGoalsByEmployee(1L));
        verify(employeeRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).findByEmployee(employee);
    }

    @Test
    @DisplayName("givenValidStatus_whenGetGoalsByStatus_thenReturnSuccess")
    public void givenValidStatus_whenGetGoalsByStatus_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findByStatus("ACTIVE")).thenReturn(Arrays.asList(goal));

        // Act
        List<PerformanceGoalDTO> result = performanceGoalService.getGoalsByStatus("ACTIVE");

        // Assert
        assertEquals(1, result.size());
        assertEquals(goalDTO.getId(), result.get(0).getId());
        assertEquals(goalDTO.getEmployeeId(), result.get(0).getEmployeeId());
        assertEquals(goalDTO.getGoalTitle(), result.get(0).getGoalTitle());
        assertEquals(goalDTO.getGoalDescription(), result.get(0).getGoalDescription());
        assertEquals(goalDTO.getCategory(), result.get(0).getCategory());
        assertEquals(goalDTO.getStartDate(), result.get(0).getStartDate());
        assertEquals(goalDTO.getTargetDate(), result.get(0).getTargetDate());
        assertEquals(goalDTO.getStatus(), result.get(0).getStatus());
        assertEquals(goalDTO.getPriority(), result.get(0).getPriority());
        assertEquals(goalDTO.getProgressPercentage(), result.get(0).getProgressPercentage());
        assertEquals(goalDTO.getKeyResults(), result.get(0).getKeyResults());
        assertEquals(goalDTO.getRemarks(), result.get(0).getRemarks());
        verify(goalRepository, times(1)).findByStatus("ACTIVE");
    }

    @Test
    @DisplayName("givenNonExistingStatus_whenGetGoalsByStatus_thenReturnEmptyList")
    public void givenNonExistingStatus_whenGetGoalsByStatus_thenReturnEmptyList() {
        // Arrange
        when(goalRepository.findByStatus("INACTIVE")).thenReturn(Arrays.asList());

        // Act
        List<PerformanceGoalDTO> result = performanceGoalService.getGoalsByStatus("INACTIVE");

        // Assert
        assertTrue(result.isEmpty());
        verify(goalRepository, times(1)).findByStatus("INACTIVE");
    }

    @Test
    @DisplayName("givenValidInput_whenUpdateGoal_thenReturnSuccess")
    public void givenValidInput_whenUpdateGoal_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(goal);

        // Act
        PerformanceGoalDTO result = performanceGoalService.updateGoal(1L, goalDTO);

        // Assert
        assertEquals(goalDTO.getId(), result.getId());
        assertEquals(goalDTO.getEmployeeId(), result.getEmployeeId());
        assertEquals(goalDTO.getGoalTitle(), result.getGoalTitle());
        assertEquals(goalDTO.getGoalDescription(), result.getGoalDescription());
        assertEquals(goalDTO.getCategory(), result.getCategory());
        assertEquals(goalDTO.getStartDate(), result.getStartDate());
        assertEquals(goalDTO.getTargetDate(), result.getTargetDate());
        assertEquals(goalDTO.getStatus(), result.getStatus());
        assertEquals(goalDTO.getPriority(), result.getPriority());
        assertEquals(goalDTO.getProgressPercentage(), result.getProgressPercentage());
        assertEquals(goalDTO.getKeyResults(), result.getKeyResults());
        assertEquals(goalDTO.getRemarks(), result.getRemarks());
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenUpdateGoal_thenThrowException")
    public void givenNonExistingGoalId_whenUpdateGoal_thenThrowException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.updateGoal(1L, goalDTO));
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).save(goal);
    }

    @Test
    @DisplayName("givenValidInput_whenUpdateGoalProgress_thenReturnSuccess")
    public void givenValidInput_whenUpdateGoalProgress_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(goal)).thenReturn(goal);

        // Act
        PerformanceGoalDTO result = performanceGoalService.updateGoalProgress(1L, 50);

        // Assert
        assertEquals(50, result.getProgressPercentage());
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenUpdateGoalProgress_thenThrowException")
    public void givenNonExistingGoalId_whenUpdateGoalProgress_thenThrowException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.updateGoalProgress(1L, 50));
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).save(goal);
    }

    @Test
    @DisplayName("givenValidInput_whenDeleteGoal_thenReturnSuccess")
    public void givenValidInput_whenDeleteGoal_thenReturnSuccess() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        // Act
        performanceGoalService.deleteGoal(1L);

        // Assert
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    @DisplayName("givenNonExistingGoalId_whenDeleteGoal_thenThrowException")
    public void givenNonExistingGoalId_whenDeleteGoal_thenThrowException() {
        // Arrange
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> performanceGoalService.deleteGoal(1L));
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(0)).delete(goal);
    }
}