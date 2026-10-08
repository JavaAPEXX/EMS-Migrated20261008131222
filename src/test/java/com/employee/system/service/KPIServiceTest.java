package com.employee.system.service;

import com.employee.system.dto.KPIDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.KPI;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.KPIRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KPIServiceTest {

    @Mock
    private KPIRepository kpiRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private KPIService kpiService;

    private Employee createMockEmployee(Long id, String firstName, String lastName) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        return employee;
    }

    private KPI createMockKPI(Long id, Employee employee, String kpiName, String status, BigDecimal currentValue) {
        KPI kpi = new KPI();
        kpi.setId(id);
        kpi.setEmployee(employee);
        kpi.setKpiName(kpiName);
        kpi.setKpiDescription("Test Description");
        kpi.setMeasurementUnit("percent");
        kpi.setTargetValue(new BigDecimal("100"));
        kpi.setCurrentValue(currentValue);
        kpi.setWeight(new BigDecimal("10"));
        kpi.setFrequency("MONTHLY");
        kpi.setStartDate(LocalDate.of(2023, 1, 1));
        kpi.setEndDate(LocalDate.of(2023, 12, 31));
        kpi.setStatus(status);
        kpi.setRemarks("Test Remarks");
        kpi.setCreatedAt(LocalDateTime.now());
        kpi.setUpdatedAt(LocalDateTime.now());
        return kpi;
    }

    private KPIDTO createMockKPIDTO(Long id, Long employeeId, String kpiName, String status, BigDecimal currentValue) {
        KPIDTO dto = new KPIDTO();
        dto.setId(id);
        dto.setEmployeeId(employeeId);
        dto.setEmployeeName("John Doe");
        dto.setKpiName(kpiName);
        dto.setKpiDescription("Test Description");
        dto.setMeasurementUnit("percent");
        dto.setTargetValue(new BigDecimal("100"));
        dto.setCurrentValue(currentValue);
        dto.setWeight(new BigDecimal("10"));
        dto.setFrequency("MONTHLY");
        dto.setStartDate(LocalDate.of(2023, 1, 1));
        dto.setEndDate(LocalDate.of(2023, 12, 31));
        dto.setStatus(status);
        dto.setRemarks("Test Remarks");
        dto.setCreatedAt(LocalDateTime.now());
        dto.setUpdatedAt(LocalDateTime.now());
        return dto;
    }

    @Test
    @DisplayName("Given valid KPI DTO with existing employee, when createKPI is called, then return created KPI DTO with default values")
    void givenValidKPIDTOWithExistingEmployee_whenCreateKPI_thenReturnCreatedKPIDTOWithDefaultValues() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = createMockEmployee(employeeId, "John", "Doe");
        KPIDTO inputDTO = createMockKPIDTO(null, employeeId, "Sales Target", "ACTIVE", new BigDecimal("50"));
        
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        
        KPI savedKPI = createMockKPI(1L, employee, "Sales Target", "ACTIVE", new BigDecimal("50"));
        when(kpiRepository.save(any(KPI.class))).thenReturn(savedKPI);

        // Act
        KPIDTO result = kpiService.createKPI(inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals("Sales Target", result.getKpiName());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(new BigDecimal("50"), result.getCurrentValue());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(kpiRepository, times(1)).save(any(KPI.class));
    }

    @Test
    @DisplayName("Given valid KPI DTO with null currentValue, when createKPI is called, then return created KPI DTO with zero currentValue")
    void givenValidKPIDTOWithNullCurrentValue_whenCreateKPI_thenReturnCreatedKPIDTOWithZeroCurrentValue() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = createMockEmployee(employeeId, "John", "Doe");
        KPIDTO inputDTO = createMockKPIDTO(null, employeeId, "Sales Target", "ACTIVE", null);
        
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        
        KPI savedKPI = createMockKPI(1L, employee, "Sales Target", "ACTIVE", BigDecimal.ZERO);
        when(kpiRepository.save(any(KPI.class))).thenReturn(savedKPI);

        // Act
        KPIDTO result = kpiService.createKPI(inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getCurrentValue());
        verify(kpiRepository, times(1)).save(any(KPI.class));
    }

    @Test
    @DisplayName("Given valid KPI DTO with null status, when createKPI is called, then return created KPI DTO with default ACTIVE status")
    void givenValidKPIDTOWithNullStatus_whenCreateKPI_thenReturnCreatedKPIDTOWithDefaultActiveStatus() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = createMockEmployee(employeeId, "John", "Doe");
        KPIDTO inputDTO = createMockKPIDTO(null, employeeId, "Sales Target", null, new BigDecimal("50"));
        
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        
        KPI savedKPI = createMockKPI(1L, employee, "Sales Target", "ACTIVE", new BigDecimal("50"));
        when(kpiRepository.save(any(KPI.class))).thenReturn(savedKPI);

        // Act
        KPIDTO result = kpiService.createKPI(inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(kpiRepository, times(1)).save(any(KPI.class));
    }

    @Test
    @DisplayName("Given non-existing employee ID, when createKPI is called, then throw RuntimeException")
    void givenNonExistingEmployeeId_whenCreateKPI_thenThrowRuntimeException() {
        // Arrange
        Long employeeId = 999L;
        KPIDTO inputDTO = createMockKPIDTO(null, employeeId, "Sales Target", "ACTIVE", new BigDecimal("50"));
        
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kpiService.createKPI(inputDTO);
        });
        
        assertTrue(exception.getMessage().contains("Employee not found with id: " + employeeId));
        verify(kpiRepository, never()).save(any(KPI.class));
    }

    @Test
    @DisplayName("Given existing KPI ID, when getKPIById is called, then return KPI DTO")
    void givenExistingKPIId_whenGetKPIById_thenReturnKPIDTO() {
        // Arrange
        Long kpiId = 1L;
        Employee employee = createMockEmployee(1L, "John", "Doe");
        KPI kpi = createMockKPI(kpiId, employee, "Sales Target", "ACTIVE", new BigDecimal("50"));
        
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

        // Act
        KPIDTO result = kpiService.getKPIById(kpiId);

        // Assert
        assertNotNull(result);
        assertEquals(kpiId, result.getId());
        assertEquals("Sales Target", result.getKpiName());
        assertEquals("John Doe", result.getEmployeeName());
        verify(kpiRepository, times(1)).findById(kpiId);
    }

    @Test
    @DisplayName("Given non-existing KPI ID, when getKPIById is called, then throw RuntimeException")
    void givenNonExistingKPIId_whenGetKPIById_thenThrowRuntimeException() {
        // Arrange
        Long kpiId = 999L;
        
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kpiService.getKPIById(kpiId);
        });
        
        assertTrue(exception.getMessage().contains("KPI not found with id: " + kpiId));
    }

    @Test
    @DisplayName("Given existing employee with KPIs, when getKPIsByEmployee is called, then return list of KPI DTOs")
    void givenExistingEmployeeWithKPIs_whenGetKPIsByEmployee_thenReturnListOfKPIDTOs() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = createMockEmployee(employeeId, "John", "Doe");
        KPI kpi1 = createMockKPI(1L, employee, "KPI 1", "ACTIVE", new BigDecimal("50"));
        KPI kpi2 = createMockKPI(2L, employee, "KPI 2", "ACTIVE", new BigDecimal("75"));
        List<KPI> kpiList = List.of(kpi1, kpi2);
        
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(kpiRepository.findByEmployee(employee)).thenReturn(kpiList);

        // Act
        List<KPIDTO> result = kpiService.getKPIsByEmployee(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("KPI 1", result.get(0).getKpiName());
        assertEquals("KPI 2", result.get(1).getKpiName());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(kpiRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("Given existing employee with no KPIs, when getKPIsByEmployee is called, then return empty list")
    void givenExistingEmployeeWithNoKPIs_whenGetKPIsByEmployee_thenReturnEmptyList() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = createMockEmployee(employeeId, "John", "Doe");
        
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(kpiRepository.findByEmployee(employee)).thenReturn(Collections.emptyList());

        // Act
        List<KPIDTO> result = kpiService.getKPIsByEmployee(employeeId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(kpiRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("Given non-existing employee ID, when getKPIsByEmployee is called, then throw RuntimeException")
    void givenNonExistingEmployeeId_whenGetKPIsByEmployee_thenThrowRuntimeException() {
        // Arrange
        Long employeeId = 999L;
        
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kpiService.getKPIsByEmployee(employeeId);
        });
        
        assertTrue(exception.getMessage().contains("Employee not found with id: " + employeeId));
        verify(kpiRepository, never()).findByEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("Given active KPIs exist, when getActiveKPIs is called, then return list of active KPI DTOs")
    void givenActiveKPIsExist_whenGetActiveKPIs_thenReturnListOfActiveKPIDTOs() {
        // Arrange
        Employee employee = createMockEmployee(1L, "John", "Doe");
        KPI kpi1 = createMockKPI(1L, employee, "KPI 1", "ACTIVE", new BigDecimal("50"));
        KPI kpi2 = createMockKPI(2L, employee, "KPI 2", "ACTIVE", new BigDecimal("75"));
        List<KPI> activeKPIs = List.of(kpi1, kpi2);
        
        when(kpiRepository.findByStatus("ACTIVE")).thenReturn(activeKPIs);

        // Act
        List<KPIDTO> result = kpiService.getActiveKPIs();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
        assertEquals("ACTIVE", result.get(1).getStatus());
        verify(kpiRepository, times(1)).findByStatus("ACTIVE");
    }

    @Test
    @DisplayName("Given no active KPIs exist, when getActiveKPIs is called, then return empty list")
    void givenNoActiveKPIsExist_whenGetActiveKPIs_thenReturnEmptyList() {
        // Arrange
        when(kpiRepository.findByStatus("ACTIVE")).thenReturn(Collections.emptyList());

        // Act
        List<KPIDTO> result = kpiService.getActiveKPIs();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(kpiRepository, times(1)).findByStatus("ACTIVE");
    }

    @Test
    @DisplayName("Given KPIs with specific frequency exist, when getKPIsByFrequency is called, then return list of KPI DTOs")
    void givenKPIsWithSpecificFrequencyExist_whenGetKPIsByFrequency_thenReturnListOfKPIDTOs() {
        // Arrange
        String frequency = "MONTHLY";
        Employee employee = createMockEmployee(1L, "John", "Doe");
        KPI kpi1 = createMockKPI(1L, employee, "KPI 1", "ACTIVE", new BigDecimal("50"));
        kpi1.setFrequency(frequency);
        KPI kpi2 = createMockKPI(2L, employee, "KPI 2", "ACTIVE", new BigDecimal("75"));
        kpi2.setFrequency(frequency);
        List<KPI> kpiList = List.of(kpi1, kpi2);
        
        when(kpiRepository.findByFrequency(frequency)).thenReturn(kpiList);

        // Act
        List<KPIDTO> result = kpiService.getKPIsByFrequency(frequency);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(frequency, result.get(0).getFrequency());
        assertEquals(frequency, result.get(1).getFrequency());
        verify(kpiRepository, times(1)).findByFrequency(frequency);
    }

    @Test
    @DisplayName("Given no KPIs with specific frequency exist, when getKPIsByFrequency is called, then return empty list")
    void givenNoKPIsWithSpecificFrequencyExist_whenGetKPIsByFrequency_thenReturnEmptyList() {
        // Arrange
        String frequency = "QUARTERLY";
        
        when(kpiRepository.findByFrequency(frequency)).thenReturn(Collections.emptyList());

        // Act
        List<KPIDTO> result = kpiService.getKPIsByFrequency(frequency);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(kpiRepository, times(1)).findByFrequency(frequency);
    }

    @Test
    @DisplayName("Given existing KPI and valid update DTO, when updateKPI is called, then return updated KPI DTO")
    void givenExistingKPIAndValidUpdateDTO_whenUpdateKPI_thenReturnUpdatedKPIDTO() {
        // Arrange
        Long kpiId = 1L;
        Employee employee = createMockEmployee(1L, "John", "Doe");
        KPI existingKPI = createMockKPI(kpiId, employee, "Old Name", "ACTIVE", new BigDecimal("50"));
        KPIDTO updateDTO = createMockKPIDTO(kpiId, 1L, "New Name", "COMPLETED", new BigDecimal("100"));
        
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(existingKPI));
        
        KPI updatedKPI = createMockKPI(kpiId, employee, "New Name", "COMPLETED", new BigDecimal("100"));
        when(kpiRepository.save(any(KPI.class))).thenReturn(updatedKPI);

        // Act
        KPIDTO result = kpiService.updateKPI(kpiId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getKpiName());
        assertEquals("COMPLETED", result.getStatus());
        verify(kpiRepository, times(1)).findById(kpiId);
        verify(kpiRepository, times(1)).save(any(KPI.class));
    }

    @Test
    @DisplayName("Given non-existing KPI ID, when updateKPI is called, then throw RuntimeException")
    void givenNonExistingKPIId_whenUpdateKPI_thenThrowRuntimeException() {
        // Arrange
        Long kpiId = 999L;
        KPIDTO updateDTO = createMockKPIDTO(kpiId, 1L, "New Name", "COMPLETED", new BigDecimal("100"));
        
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kpiService.updateKPI(kpiId, updateDTO);
        });
        
        assertTrue(exception.getMessage().contains("KPI not found with id: " + kpiId));
        verify(kpiRepository, never()).save(any(KPI.class));
    }

    @Test
    @DisplayName("Given existing KPI and valid current value, when updateKPIProgress is called, then return updated KPI DTO with new current value")
    void givenExistingKPIAndValidCurrentValue_whenUpdateKPIProgress_thenReturnUpdatedKPIDTOWithNewCurrentValue() {
        // Arrange
        Long kpiId = 1L;
        Employee employee = createMockEmployee(1L, "John", "Doe");
        KPI existingKPI = createMockKPI(kpiId, employee, "Sales Target", "ACTIVE", new BigDecimal("50"));
        BigDecimal newValue = new BigDecimal("75");
        
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(existingKPI));
        
        KPI updatedKPI = createMockKPI(kpiId, employee, "Sales Target", "ACTIVE", newValue);
        when(kpiRepository.save(any(KPI.class))).thenReturn(updatedKPI);

        // Act
        KPIDTO result = kpiService.updateKPIProgress(kpiId, newValue);

        // Assert
        assertNotNull(result);
        assertEquals(newValue, result.getCurrentValue());
        verify(kpiRepository, times(1)).findById(kpiId);
        verify(kpiRepository, times(1)).save(any(KPI.class));
    }

    @Test
    @DisplayName("Given non-existing KPI ID, when updateKPIProgress is called, then throw RuntimeException")
    void givenNonExistingKPIId_whenUpdateKPIProgress_thenThrowRuntimeException() {
        // Arrange
        Long kpiId = 999L;
        BigDecimal newValue = new BigDecimal("75");
        
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kpiService.updateKPIProgress(kpiId, newValue);
        });
        
        assertTrue(exception.getMessage().contains("KPI not found with id: " + kpiId));
        verify(kpiRepository, never()).save(any(KPI.class));
    }

    @Test
    @DisplayName("Given existing KPI ID, when deleteKPI is called, then delete KPI successfully")
    void givenExistingKPIId_whenDeleteKPI_thenDeleteKPISuccessfully() {
        // Arrange
        Long kpiId = 1L;
        Employee employee = createMockEmployee(1L, "John", "Doe");
        KPI kpi = createMockKPI(kpiId, employee, "Sales Target", "ACTIVE", new BigDecimal("50"));
        
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.of(kpi));

        // Act
        kpiService.deleteKPI(kpiId);

        // Assert
        verify(kpiRepository, times(1)).findById(kpiId);
        verify(kpiRepository, times(1)).delete(kpi);
    }

    @Test
    @DisplayName("Given non-existing KPI ID, when deleteKPI is called, then throw RuntimeException")
    void givenNonExistingKPIId_whenDeleteKPI_thenThrowRuntimeException() {
        // Arrange
        Long kpiId = 999L;
        
        when(kpiRepository.findById(kpiId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kpiService.deleteKPI(kpiId);
        });
        
        assertTrue(exception.getMessage().contains("KPI not found with id: " + kpiId));
        verify(kpiRepository, never()).delete(any(KPI.class));
    }
}