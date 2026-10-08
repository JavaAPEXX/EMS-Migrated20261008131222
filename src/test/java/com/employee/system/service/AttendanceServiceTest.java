package com.employee.system.service;

import com.employee.system.dto.AttendanceDTO;
import com.employee.system.entity.Attendance;
import com.employee.system.entity.Employee;
import com.employee.system.repository.AttendanceRepository;
import com.employee.system.repository.EmployeeRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Employee employee;
    private Attendance attendance;
    private AttendanceDTO attendanceDTO;
    private LocalDate testDate;
    private LocalDateTime testCheckInTime;
    private LocalDateTime testCheckOutTime;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        attendance = new Attendance();
        attendance.setId(1L);
        attendance.setEmployee(employee);
        testDate = LocalDate.of(2023, 10, 15);
        attendance.setAttendanceDate(testDate);
        attendance.setStatus("PRESENT");
        testCheckInTime = LocalDateTime.of(2023, 10, 15, 9, 0);
        testCheckOutTime = LocalDateTime.of(2023, 10, 15, 17, 0);
        attendance.setCheckInTime(testCheckInTime);
        attendance.setCheckOutTime(testCheckOutTime);
        attendance.setRemarks("On time");
        attendance.setCreatedAt(LocalDateTime.now());
        attendance.setUpdatedAt(LocalDateTime.now());

        attendanceDTO = new AttendanceDTO();
        attendanceDTO.setId(1L);
        attendanceDTO.setEmployeeId(1L);
        attendanceDTO.setEmployeeName("John Doe");
        attendanceDTO.setAttendanceDate(testDate);
        attendanceDTO.setStatus("PRESENT");
        attendanceDTO.setCheckInTime(testCheckInTime);
        attendanceDTO.setCheckOutTime(testCheckOutTime);
        attendanceDTO.setRemarks("On time");
        attendanceDTO.setCreatedAt(attendance.getCreatedAt());
        attendanceDTO.setUpdatedAt(attendance.getUpdatedAt());
    }

    @Test
    @DisplayName("Given valid attendance DTO and existing employee, when marking attendance, then return saved attendance DTO")
    void givenValidAttendanceDTOAndExistingEmployee_whenMarkAttendance_thenReturnSavedAttendanceDTO() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> {
            Attendance att = invocation.getArgument(0);
            att.setId(1L);
            return att;
        });

        // Act
        AttendanceDTO result = attendanceService.markAttendance(attendanceDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals("John Doe", result.getEmployeeName());
        assertEquals(testDate, result.getAttendanceDate());
        assertEquals("PRESENT", result.getStatus());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Given non-existing employee ID, when marking attendance, then throw RuntimeException")
    void givenNonExistingEmployeeId_whenMarkAttendance_thenThrowRuntimeException() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.markAttendance(attendanceDTO);
        });
        assertTrue(exception.getMessage().contains("Employee not found with id: 999"));
        verify(employeeRepository