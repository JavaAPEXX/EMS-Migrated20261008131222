package com.employee.system.service;

import com.employee.system.dto.EmployeeDTO;
import com.employee.system.entity.Employee;
import com.employee.system.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    // --- Helper Methods ---

    private Employee createSampleEmployee(Long id, String employeeId, String firstName, String lastName, String email) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setEmployeeId(employeeId);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPhoneNumber("123-456-7890");
        employee.setDepartment("Engineering");
        employee.setDesignation("Software Engineer");
        employee.setDateOfJoining(LocalDate.of(2023, 1, 15));
        employee.setStatus("ACTIVE");
        employee.setAddress("123 Main St");
        employee.setDateOfBirth(LocalDate.of(1990, 5, 20));
        employee.setGender("Male");
        employee.setCreatedAt(LocalDate.now());
        employee.setUpdatedAt(LocalDate.now());
        return employee;
    }

    private EmployeeDTO createSampleDTO(Long id, String employeeId, String firstName, String lastName, String email) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(id);
        dto.setEmployeeId(employeeId);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setPhoneNumber("123-456-7890");
        dto.setDepartment("Engineering");
        dto.setDesignation("Software Engineer");
        dto.setDateOfJoining(LocalDate.of(2023, 1, 15));
        dto.setStatus("ACTIVE");
        dto.setAddress("123 Main St");
        dto.setDateOfBirth(LocalDate.of(1990, 5, 20));
        dto.setGender("Male");
        return dto;
    }

    // --- createEmployee Tests ---

    @Test
    @DisplayName("Given valid employee DTO, when creating employee, then return saved employee DTO")
    void givenValidEmployeeDTO_whenCreateEmployee_thenReturnSavedEmployeeDTO() {
        // Arrange
        EmployeeDTO inputDTO = createSampleDTO(null, "EMP001", "John", "Doe", "john.doe@example.com");
        Employee savedEmployee = createSampleEmployee(1L, "EMP001", "John", "Doe", "john.doe@example.com");
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // Act
        EmployeeDTO result = employeeService.createEmployee(inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("EMP001", result.getEmployeeId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("ACTIVE", result.getStatus());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given employee DTO with null status, when creating employee, then default status to ACTIVE")
    void givenEmployeeDTOWithNullStatus_whenCreateEmployee_thenDefaultStatusToActive() {
        // Arrange
        EmployeeDTO inputDTO = createSampleDTO(null, "EMP002", "Jane", "Smith", "jane.smith@example.com");
        inputDTO.setStatus(null);
        Employee savedEmployee = createSampleEmployee(2L, "EMP002", "Jane", "Smith", "jane.smith@example.com");
        savedEmployee.setStatus("ACTIVE");
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // Act
        EmployeeDTO result = employeeService.createEmployee(inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given employee DTO with explicit status, when creating employee, then use provided status")
    void givenEmployeeDTOWithExplicitStatus_whenCreateEmployee_thenUseProvidedStatus() {
        // Arrange
        EmployeeDTO inputDTO = createSampleDTO(null, "EMP003", "Bob", "Johnson", "bob.johnson@example.com");
        inputDTO.setStatus("INACTIVE");
        Employee savedEmployee = createSampleEmployee(3L, "EMP003", "Bob", "Johnson", "bob.johnson@example.com");
        savedEmployee.setStatus("INACTIVE");
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // Act
        EmployeeDTO result = employeeService.createEmployee(inputDTO);

        // Assert
        assertNotNull(result);
        assertEquals("INACTIVE", result.getStatus());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given null employee DTO, when creating employee, then throw NullPointerException")
    void givenNullEmployeeDTO_whenCreateEmployee_thenThrowNullPointerException() {
        // Arrange
        EmployeeDTO nullDTO = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> employeeService.createEmployee(nullDTO));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given repository save throws exception, when creating employee, then propagate exception")
    void givenRepositorySaveThrowsException_whenCreateEmployee_thenPropagateException() {
        // Arrange
        EmployeeDTO inputDTO = createSampleDTO(null, "EMP004", "Alice", "Brown", "alice.brown@example.com");
        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.createEmployee(inputDTO));
        assertEquals("Database error", exception.getMessage());
    }

    // --- getEmployeeById Tests ---

    @Test
    @DisplayName("Given existing employee ID, when getting employee by ID, then return employee DTO")
    void givenExistingEmployeeId_whenGetEmployeeById_thenReturnEmployeeDTO() {
        // Arrange
        Long id = 1L;
        Employee employee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        // Act
        EmployeeDTO result = employeeService.getEmployeeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("EMP001", result.getEmployeeId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Given non-existing employee ID, when getting employee by ID, then throw RuntimeException")
    void givenNonExistingEmployeeId_whenGetEmployeeById_thenThrowRuntimeException() {
        // Arrange
        Long id = 999L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.getEmployeeById(id));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Given null employee ID, when getting employee by ID, then throw RuntimeException")
    void givenNullEmployeeId_whenGetEmployeeById_thenThrowRuntimeException() {
        // Arrange
        Long id = null;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.getEmployeeById(id));
        assertEquals("Employee not found with id: null", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
    }

    // --- getEmployeeByEmployeeId Tests ---

    @Test
    @DisplayName("Given existing employee ID string, when getting employee by employee ID, then return employee DTO")
    void givenExistingEmployeeIdString_whenGetEmployeeByEmployeeId_thenReturnEmployeeDTO() {
        // Arrange
        String employeeId = "EMP001";
        Employee employee = createSampleEmployee(1L, employeeId, "John", "Doe", "john.doe@example.com");
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeDTO result = employeeService.getEmployeeByEmployeeId(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("EMP001", result.getEmployeeId());
        assertEquals("John", result.getFirstName());
        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
    }

    @Test
    @DisplayName("Given non-existing employee ID string, when getting employee by employee ID, then throw RuntimeException")
    void givenNonExistingEmployeeIdString_whenGetEmployeeByEmployeeId_thenThrowRuntimeException() {
        // Arrange
        String employeeId = "EMP999";
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.getEmployeeByEmployeeId(employeeId));
        assertEquals("Employee not found with employee id: EMP999", exception.getMessage());
        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
    }

    @Test
    @DisplayName("Given null employee ID string, when getting employee by employee ID, then throw RuntimeException")
    void givenNullEmployeeIdString_whenGetEmployeeByEmployeeId_thenThrowRuntimeException() {
        // Arrange
        String employeeId = null;
        when(employeeRepository.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.getEmployeeByEmployeeId(employeeId));
        assertEquals("Employee not found with employee id: null", exception.getMessage());
        verify(employeeRepository, times(1)).findByEmployeeId(employeeId);
    }

    // --- getAllEmployees Tests ---

    @Test
    @DisplayName("Given multiple employees in repository, when getting all employees, then return list of employee DTOs")
    void givenMultipleEmployeesInRepository_whenGetAllEmployees_thenReturnListOfEmployeeDTOs() {
        // Arrange
        Employee emp1 = createSampleEmployee(1L, "EMP001", "John", "Doe", "john.doe@example.com");
        Employee emp2 = createSampleEmployee(2L, "EMP002", "Jane", "Smith", "jane.smith@example.com");
        Employee emp3 = createSampleEmployee(3L, "EMP003", "Bob", "Johnson", "bob.johnson@example.com");
        List<Employee> employees = List.of(emp1, emp2, emp3);
        when(employeeRepository.findAll()).thenReturn(employees);

        // Act
        List<EmployeeDTO> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("EMP001", result.get(0).getEmployeeId());
        assertEquals("EMP002", result.get(1).getEmployeeId());
        assertEquals("EMP003", result.get(2).getEmployeeId());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Given empty repository, when getting all employees, then return empty list")
    void givenEmptyRepository_whenGetAllEmployees_thenReturnEmptyList() {
        // Arrange
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeDTO> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findAll();
    }

    // --- getEmployeesByDepartment Tests ---

    @Test
    @DisplayName("Given existing department, when getting employees by department, then return list of employee DTOs")
    void givenExistingDepartment_whenGetEmployeesByDepartment_thenReturnListOfEmployeeDTOs() {
        // Arrange
        String department = "Engineering";
        Employee emp1 = createSampleEmployee(1L, "EMP001", "John", "Doe", "john.doe@example.com");
        Employee emp2 = createSampleEmployee(2L, "EMP002", "Jane", "Smith", "jane.smith@example.com");
        List<Employee> employees = List.of(emp1, emp2);
        when(employeeRepository.findByDepartment(department)).thenReturn(employees);

        // Act
        List<EmployeeDTO> result = employeeService.getEmployeesByDepartment(department);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Engineering", result.get(0).getDepartment());
        assertEquals("Engineering", result.get(1).getDepartment());
        verify(employeeRepository, times(1)).findByDepartment(department);
    }

    @Test
    @DisplayName("Given non-existing department, when getting employees by department, then return empty list")
    void givenNonExistingDepartment_whenGetEmployeesByDepartment_thenReturnEmptyList() {
        // Arrange
        String department = "NonExistentDept";
        when(employeeRepository.findByDepartment(department)).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeDTO> result = employeeService.getEmployeesByDepartment(department);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findByDepartment(department);
    }

    @Test
    @DisplayName("Given null department, when getting employees by department, then return empty list")
    void givenNullDepartment_whenGetEmployeesByDepartment_thenReturnEmptyList() {
        // Arrange
        String department = null;
        when(employeeRepository.findByDepartment(department)).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeDTO> result = employeeService.getEmployeesByDepartment(department);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findByDepartment(department);
    }

    // --- getEmployeesByDesignation Tests ---

    @Test
    @DisplayName("Given existing designation, when getting employees by designation, then return list of employee DTOs")
    void givenExistingDesignation_whenGetEmployeesByDesignation_thenReturnListOfEmployeeDTOs() {
        // Arrange
        String designation = "Software Engineer";
        Employee emp1 = createSampleEmployee(1L, "EMP001", "John", "Doe", "john.doe@example.com");
        Employee emp2 = createSampleEmployee(2L, "EMP002", "Jane", "Smith", "jane.smith@example.com");
        List<Employee> employees = List.of(emp1, emp2);
        when(employeeRepository.findByDesignation(designation)).thenReturn(employees);

        // Act
        List<EmployeeDTO> result = employeeService.getEmployeesByDesignation(designation);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Software Engineer", result.get(0).getDesignation());
        assertEquals("Software Engineer", result.get(1).getDesignation());
        verify(employeeRepository, times(1)).findByDesignation(designation);
    }

    @Test
    @DisplayName("Given non-existing designation, when getting employees by designation, then return empty list")
    void givenNonExistingDesignation_whenGetEmployeesByDesignation_thenReturnEmptyList() {
        // Arrange
        String designation = "NonExistentRole";
        when(employeeRepository.findByDesignation(designation)).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeDTO> result = employeeService.getEmployeesByDesignation(designation);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findByDesignation(designation);
    }

    @Test
    @DisplayName("Given null designation, when getting employees by designation, then return empty list")
    void givenNullDesignation_whenGetEmployeesByDesignation_thenReturnEmptyList() {
        // Arrange
        String designation = null;
        when(employeeRepository.findByDesignation(designation)).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeDTO> result = employeeService.getEmployeesByDesignation(designation);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findByDesignation(designation);
    }

    // --- searchEmployeesByName Tests ---

    @Test
    @DisplayName("Given search name with matches, when searching employees by name, then return list of matching employee DTOs")
    void givenSearchNameWithMatches_whenSearchEmployeesByName_thenReturnListOfMatchingEmployeeDTOs() {
        // Arrange
        String name = "John";
        Employee emp1 = createSampleEmployee(1L, "EMP001", "John", "Doe", "john.doe@example.com");
        Employee emp2 = createSampleEmployee(2L, "EMP002", "Johnny", "Depp", "johnny.depp@example.com");
        List<Employee> employees = List.of(emp1, emp2);
        when(employeeRepository.searchByName(name)).thenReturn(employees);

        // Act
        List<EmployeeDTO> result = employeeService.searchEmployeesByName(name);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getFirstName().contains("John"));
        assertTrue(result.get(1).getFirstName().contains("John"));
        verify(employeeRepository, times(1)).searchByName(name);
    }

    @Test
    @DisplayName("Given search name with no matches, when searching employees by name, then return empty list")
    void givenSearchNameWithNoMatches_whenSearchEmployeesByName_thenReturnEmptyList() {
        // Arrange
        String name = "NonExistentName";
        when(employeeRepository.searchByName(name)).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeDTO> result = employeeService.searchEmployeesByName(name);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).searchByName(name);
    }

    @Test
    @DisplayName("Given null search name, when searching employees by name, then return empty list")
    void givenNullSearchName_whenSearchEmployeesByName_thenReturnEmptyList() {
        // Arrange
        String name = null;
        when(employeeRepository.searchByName(name)).thenReturn(Collections.emptyList());

        // Act
        List<EmployeeDTO> result = employeeService.searchEmployeesByName(name);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).searchByName(name);
    }

    // --- updateEmployee Tests ---

    @Test
    @DisplayName("Given existing employee and valid update DTO, when updating employee, then return updated employee DTO")
    void givenExistingEmployeeAndValidUpdateDTO_whenUpdateEmployee_thenReturnUpdatedEmployeeDTO() {
        // Arrange
        Long id = 1L;
        Employee existingEmployee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        EmployeeDTO updateDTO = createSampleDTO(id, "EMP001", "John", "Updated", "john.updated@example.com");
        updateDTO.setStatus("INACTIVE");
        
        Employee updatedEmployee = createSampleEmployee(id, "EMP001", "John", "Updated", "john.updated@example.com");
        updatedEmployee.setStatus("INACTIVE");
        
        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        EmployeeDTO result = employeeService.updateEmployee(id, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Updated", result.getLastName());
        assertEquals("john.updated@example.com", result.getEmail());
        assertEquals("INACTIVE", result.getStatus());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given non-existing employee ID, when updating employee, then throw RuntimeException")
    void givenNonExistingEmployeeId_whenUpdateEmployee_thenThrowRuntimeException() {
        // Arrange
        Long id = 999L;
        EmployeeDTO updateDTO = createSampleDTO(id, "EMP999", "John", "Doe", "john.doe@example.com");
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(id, updateDTO));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given null employee ID, when updating employee, then throw RuntimeException")
    void givenNullEmployeeId_whenUpdateEmployee_thenThrowRuntimeException() {
        // Arrange
        Long id = null;
        EmployeeDTO updateDTO = createSampleDTO(null, "EMP001", "John", "Doe", "john.doe@example.com");
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(id, updateDTO));
        assertEquals("Employee not found with id: null", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given existing employee and null update DTO, when updating employee, then throw NullPointerException")
    void givenExistingEmployeeAndNullUpdateDTO_whenUpdateEmployee_thenThrowNullPointerException() {
        // Arrange
        Long id = 1L;
        Employee existingEmployee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> employeeService.updateEmployee(id, null));
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given repository save throws exception during update, when updating employee, then propagate exception")
    void givenRepositorySaveThrowsExceptionDuringUpdate_whenUpdateEmployee_thenPropagateException() {
        // Arrange
        Long id = 1L;
        Employee existingEmployee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        EmployeeDTO updateDTO = createSampleDTO(id, "EMP001", "John", "Updated", "john.updated@example.com");
        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("Save failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(id, updateDTO));
        assertEquals("Save failed", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // --- deleteEmployee Tests ---

    @Test
    @DisplayName("Given existing employee ID, when deleting employee, then delete employee from repository")
    void givenExistingEmployeeId_whenDeleteEmployee_thenDeleteEmployeeFromRepository() {
        // Arrange
        Long id = 1L;
        Employee employee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployee(id);

        // Assert
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    @DisplayName("Given non-existing employee ID, when deleting employee, then throw RuntimeException")
    void givenNonExistingEmployeeId_whenDeleteEmployee_thenThrowRuntimeException() {
        // Arrange
        Long id = 999L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.deleteEmployee(id));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    @DisplayName("Given null employee ID, when deleting employee, then throw RuntimeException")
    void givenNullEmployeeId_whenDeleteEmployee_thenThrowRuntimeException() {
        // Arrange
        Long id = null;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.deleteEmployee(id));
        assertEquals("Employee not found with id: null", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    @DisplayName("Given repository delete throws exception, when deleting employee, then propagate exception")
    void givenRepositoryDeleteThrowsException_whenDeleteEmployee_thenPropagateException() {
        // Arrange
        Long id = 1L;
        Employee employee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        doThrow(new RuntimeException("Delete failed")).when(employeeRepository).delete(any(Employee.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.deleteEmployee(id));
        assertEquals("Delete failed", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).delete(any(Employee.class));
    }

    // --- getActiveEmployeesCount Tests ---

    @Test
    @DisplayName("Given active employees in repository, when getting active employees count, then return correct count")
    void givenActiveEmployeesInRepository_whenGetActiveEmployeesCount_thenReturnCorrectCount() {
        // Arrange
        long expectedCount = 42L;
        when(employeeRepository.countActiveEmployees()).thenReturn(expectedCount);

        // Act
        long result = employeeService.getActiveEmployeesCount();

        // Assert
        assertEquals(expectedCount, result);
        verify(employeeRepository, times(1)).countActiveEmployees();
    }

    @Test
    @DisplayName("Given no active employees in repository, when getting active employees count, then return zero")
    void givenNoActiveEmployeesInRepository_whenGetActiveEmployeesCount_thenReturnZero() {
        // Arrange
        when(employeeRepository.countActiveEmployees()).thenReturn(0L);

        // Act
        long result = employeeService.getActiveEmployeesCount();

        // Assert
        assertEquals(0L, result);
        verify(employeeRepository, times(1)).countActiveEmployees();
    }

    // --- changeEmployeeStatus Tests ---

    @Test
    @DisplayName("Given existing employee and valid status, when changing employee status, then return updated employee DTO")
    void givenExistingEmployeeAndValidStatus_whenChangeEmployeeStatus_thenReturnUpdatedEmployeeDTO() {
        // Arrange
        Long id = 1L;
        String newStatus = "INACTIVE";
        Employee existingEmployee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        existingEmployee.setStatus("ACTIVE");
        
        Employee updatedEmployee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        updatedEmployee.setStatus("INACTIVE");
        
        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        EmployeeDTO result = employeeService.changeEmployeeStatus(id, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("INACTIVE", result.getStatus());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given non-existing employee ID, when changing employee status, then throw RuntimeException")
    void givenNonExistingEmployeeId_whenChangeEmployeeStatus_thenThrowRuntimeException() {
        // Arrange
        Long id = 999L;
        String newStatus = "INACTIVE";
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.changeEmployeeStatus(id, newStatus));
        assertEquals("Employee not found with id: 999", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given null employee ID, when changing employee status, then throw RuntimeException")
    void givenNullEmployeeId_whenChangeEmployeeStatus_thenThrowRuntimeException() {
        // Arrange
        Long id = null;
        String newStatus = "INACTIVE";
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.changeEmployeeStatus(id, newStatus));
        assertEquals("Employee not found with id: null", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given existing employee and null status, when changing employee status, then set status to null")
    void givenExistingEmployeeAndNullStatus_whenChangeEmployeeStatus_thenSetStatusToNull() {
        // Arrange
        Long id = 1L;
        String newStatus = null;
        Employee existingEmployee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        existingEmployee.setStatus("ACTIVE");
        
        Employee updatedEmployee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        updatedEmployee.setStatus(null);
        
        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        EmployeeDTO result = employeeService.changeEmployeeStatus(id, newStatus);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getStatus());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Given repository save throws exception during status change, when changing employee status, then propagate exception")
    void givenRepositorySaveThrowsExceptionDuringStatusChange_whenChangeEmployeeStatus_thenPropagateException() {
        // Arrange
        Long id = 1L;
        String newStatus = "INACTIVE";
        Employee existingEmployee = createSampleEmployee(id, "EMP001", "John", "Doe", "john.doe@example.com");
        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("Save failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.changeEmployeeStatus(id, newStatus));
        assertEquals("Save failed", exception.getMessage());
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
}