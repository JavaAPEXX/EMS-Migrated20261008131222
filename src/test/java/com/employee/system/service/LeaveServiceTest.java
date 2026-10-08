package com.employee.system.service;

import com.employee.system.dto.LeaveBalanceDTO;
import com.employee.system.dto.LeaveDTO;
import com.employee.system.entity.Employee;
import com.employee.system.entity.Holiday;
import com.employee.system.entity.Leave;
import com.employee.system.repository.EmployeeRepository;
import com.employee.system.repository.HolidayRepository;
import com.employee.system.repository.LeaveRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private LeaveService leaveService;

    private Employee employee;
    private Leave leave;
    private LeaveDTO leaveDTO;
    private List<Holiday> holidays;

    @BeforeEach
    public void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        leave = new Leave();
        leave.setId(1L);
        leave.setEmployee(employee);
        leave.setStartDate(LocalDate.of(2023, 10, 1));
        leave.setEndDate(LocalDate.of(2023, 10, 5));
        leave.setLeaveType("SICK");
        leave.setReason("Sick leave");
        leave.setDays(5);
        leave.setStatus("PENDING");

        leaveDTO = new LeaveDTO();
        leaveDTO.setId(1L);
        leaveDTO.setEmployeeId(1L);
        leaveDTO.setEmployeeName("John Doe");
        leaveDTO.setStartDate(LocalDate.of(2023, 10, 1));
        leaveDTO.setEndDate(LocalDate.of(2023, 10, 5));
        leaveDTO.setLeaveType("SICK");
        leaveDTO.setReason("Sick leave");
        leaveDTO.setDays(5);
        leaveDTO.setStatus("PENDING");

        holidays = Arrays.asList(
            new Holiday(LocalDate.of(2023, 10, 2)),
            new Holiday(LocalDate.of(2023, 10, 3))
        );
    }

    @Test
    @DisplayName("givenValidInput_whenApplyLeave_thenReturnSuccess")
    public void givenValidInput_whenApplyLeave_thenReturnSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        when(holidayRepository.findBetween(LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 5))).thenReturn(holidays);
        when(leaveRepository.save(any(Leave.class))).thenReturn(leave);

        // Act
        LeaveDTO result = leaveService.applyLeave(leaveDTO);

        // Assert
        assertEquals(leaveDTO.getId(), result.getId());
        assertEquals(leaveDTO.getEmployeeId(), result.getEmployeeId());
        assertEquals(leaveDTO.getStartDate(), result.getStartDate());
        assertEquals(leaveDTO.getEndDate(), result.getEndDate());
        assertEquals(leaveDTO.getLeaveType(), result.getLeaveType());
        assertEquals(leaveDTO.getReason(), result.getReason());
        assertEquals(leaveDTO.getDays(), result.getDays());
        assertEquals(leaveDTO.getStatus(), result.getStatus());
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }

    @Test
    @DisplayName("givenNullEmployeeId_whenApplyLeave_thenThrowRuntimeException")
    public void givenNullEmployeeId_whenApplyLeave_thenThrowRuntimeException() {
        // Arrange
        leaveDTO.setEmployeeId(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.applyLeave(leaveDTO));
    }

    @Test
    @DisplayName("givenInvalidDateRange_whenApplyLeave_thenThrowRuntimeException")
    public void givenInvalidDateRange_whenApplyLeave_thenThrowRuntimeException() {
        // Arrange
        leaveDTO.setStartDate(LocalDate.of(2023, 10, 1));
        leaveDTO.setEndDate(LocalDate.of(2023, 9, 30));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.applyLeave(leaveDTO));
    }

    @Test
    @DisplayName("givenConflictLeave_whenApplyLeave_thenThrowRuntimeException")
    public void givenConflictLeave_whenApplyLeave_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        when(holidayRepository.findBetween(LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 5))).thenReturn(holidays);
        when(leaveRepository.hasOverlappingLeave(employee, LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 5))).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.applyLeave(leaveDTO));
    }

    @Test
    @DisplayName("givenInsufficientLeaveBalance_whenApplyLeave_thenThrowRuntimeException")
    public void givenInsufficientLeaveBalance_whenApplyLeave_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        when(holidayRepository.findBetween(LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 5))).thenReturn(holidays);
        when(leaveRepository.countTakenDaysByTypeAndYear(employee, "SICK", 2023)).thenReturn(10);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.applyLeave(leaveDTO));
    }

    @Test
    @DisplayName("givenValidId_whenApproveLeave_thenReturnUpdatedLeave")
    public void givenValidId_whenApproveLeave_thenReturnUpdatedLeave() {
        // Arrange
        when(leaveRepository.findById(1L)).thenReturn(java.util.Optional.of(leave));

        // Act
        LeaveDTO result = leaveService.approveLeave(1L, true);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("APPROVED", result.getStatus());
        verify(leaveRepository, times(1)).save(any(Leave.class));
    }

    @Test
    @DisplayName("givenNonExistingId_whenApproveLeave_thenThrowRuntimeException")
    public void givenNonExistingId_whenApproveLeave_thenThrowRuntimeException() {
        // Arrange
        when(leaveRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.approveLeave(2L, true));
    }

    @Test
    @DisplayName("givenValidId_whenGetLeaveById_thenReturnLeaveDTO")
    public void givenValidId_whenGetLeaveById_thenReturnLeaveDTO() {
        // Arrange
        when(leaveRepository.findById(1L)).thenReturn(java.util.Optional.of(leave));

        // Act
        LeaveDTO result = leaveService.getLeaveById(1L);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(LocalDate.of(2023, 10, 1), result.getStartDate());
        assertEquals(LocalDate.of(2023, 10, 5), result.getEndDate());
        assertEquals("SICK", result.getLeaveType());
        assertEquals("Sick leave", result.getReason());
        assertEquals(5, result.getDays());
        assertEquals("PENDING", result.getStatus());
        verify(leaveRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("givenNonExistingId_whenGetLeaveById_thenThrowRuntimeException")
    public void givenNonExistingId_whenGetLeaveById_thenThrowRuntimeException() {
        // Arrange
        when(leaveRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.getLeaveById(2L));
    }

    @Test
    @DisplayName("givenValidEmployeeId_whenGetLeavesByEmployee_thenReturnLeaveDTOList")
    public void givenValidEmployeeId_whenGetLeavesByEmployee_thenReturnLeaveDTOList() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        when(leaveRepository.findByEmployee(employee)).thenReturn(Arrays.asList(leave));

        // Act
        List<LeaveDTO> result = leaveService.getLeavesByEmployee(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals(LocalDate.of(2023, 10, 1), result.get(0).getStartDate());
        assertEquals(LocalDate.of(2023, 10, 5), result.get(0).getEndDate());
        assertEquals("SICK", result.get(0).getLeaveType());
        assertEquals("Sick leave", result.get(0).getReason());
        assertEquals(5, result.get(0).getDays());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(leaveRepository, times(1)).findByEmployee(employee);
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenGetLeavesByEmployee_thenThrowRuntimeException")
    public void givenNonExistingEmployeeId_whenGetLeavesByEmployee_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.getLeavesByEmployee(2L));
    }

    @Test
    @DisplayName("givenValidStatus_whenGetLeavesByStatus_thenReturnLeaveDTOList")
    public void givenValidStatus_whenGetLeavesByStatus_thenReturnLeaveDTOList() {
        // Arrange
        when(leaveRepository.findByStatus("PENDING")).thenReturn(Arrays.asList(leave));

        // Act
        List<LeaveDTO> result = leaveService.getLeavesByStatus("PENDING");

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals(LocalDate.of(2023, 10, 1), result.get(0).getStartDate());
        assertEquals(LocalDate.of(2023, 10, 5), result.get(0).getEndDate());
        assertEquals("SICK", result.get(0).getLeaveType());
        assertEquals("Sick leave", result.get(0).getReason());
        assertEquals(5, result.get(0).getDays());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(leaveRepository, times(1)).findByStatus("PENDING");
    }

    @Test
    @DisplayName("givenValidYear_whenGetLeavesByYear_thenReturnLeaveDTOList")
    public void givenValidYear_whenGetLeavesByYear_thenReturnLeaveDTOList() {
        // Arrange
        when(leaveRepository.findByYear(2023)).thenReturn(Arrays.asList(leave));

        // Act
        List<LeaveDTO> result = leaveService.getLeavesByYear(2023);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals(LocalDate.of(2023, 10, 1), result.get(0).getStartDate());
        assertEquals(LocalDate.of(2023, 10, 5), result.get(0).getEndDate());
        assertEquals("SICK", result.get(0).getLeaveType());
        assertEquals("Sick leave", result.get(0).getReason());
        assertEquals(5, result.get(0).getDays());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(leaveRepository, times(1)).findByYear(2023);
    }

    @Test
    @DisplayName("givenValidEmployeeId_whenGetLeaveBalance_thenReturnLeaveBalanceDTO")
    public void givenValidEmployeeId_whenGetLeaveBalance_thenReturnLeaveBalanceDTO() {
        // Arrange
        Map<String, Integer> leaveBalance = new HashMap<>();
        leaveBalance.put("SICK", 10);
        leaveBalance.put("ANNUAL", 20);
        when(leaveRepository.getLeaveBalance(employee)).thenReturn(leaveBalance);

        // Act
        Map<String, Integer> result = leaveService.getLeaveBalance(1L);

        // Assert
        assertEquals(10, result.get("SICK"));
        assertEquals(20, result.get("ANNUAL"));
        verify(leaveRepository, times(1)).getLeaveBalance(employee);
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenGetLeaveBalance_thenThrowRuntimeException")
    public void givenNonExistingEmployeeId_whenGetLeaveBalance_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.getLeaveBalance(2L));
    }

    @Test
    @DisplayName("givenValidEmployeeId_whenGetLeaveHistory_thenReturnLeaveDTOList")
    public void givenValidEmployeeId_whenGetLeaveHistory_thenReturnLeaveDTOList() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));
        when(leaveRepository.findByEmployeeOrderByStartDateDesc(employee)).thenReturn(Arrays.asList(leave));

        // Act
        List<LeaveDTO> result = leaveService.getLeaveHistory(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1L, result.get(0).getEmployeeId());
        assertEquals(LocalDate.of(2023, 10, 1), result.get(0).getStartDate());
        assertEquals(LocalDate.of(2023, 10, 5), result.get(0).getEndDate());
        assertEquals("SICK", result.get(0).getLeaveType());
        assertEquals("Sick leave", result.get(0).getReason());
        assertEquals(5, result.get(0).getDays());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(leaveRepository, times(1)).findByEmployeeOrderByStartDateDesc(employee);
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenGetLeaveHistory_thenThrowRuntimeException")
    public void givenNonExistingEmployeeId_whenGetLeaveHistory_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.getLeaveHistory(2L));
    }

    @Test
    @DisplayName("givenValidEmployeeId_whenGetLeaveSummary_thenReturnLeaveSummaryDTO")
    public void givenValidEmployeeId_whenGetLeaveSummary_thenReturnLeaveSummaryDTO() {
        // Arrange
        Map<String, Integer> leaveSummary = new HashMap<>();
        leaveSummary.put("SICK", 5);
        leaveSummary.put("ANNUAL", 10);
        when(leaveRepository.getLeaveSummary(employee)).thenReturn(leaveSummary);

        // Act
        Map<String, Integer> result = leaveService.getLeaveSummary(1L);

        // Assert
        assertEquals(5, result.get("SICK"));
        assertEquals(10, result.get("ANNUAL"));
        verify(leaveRepository, times(1)).getLeaveSummary(employee);
    }

    @Test
    @DisplayName("givenNonExistingEmployeeId_whenGetLeaveSummary_thenThrowRuntimeException")
    public void givenNonExistingEmployeeId_whenGetLeaveSummary_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> leaveService.getLeaveSummary(2L));
    }
}