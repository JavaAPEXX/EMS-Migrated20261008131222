package com.employee.system.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AttendanceSummary}.
 * Since AttendanceSummary is a Lombok-generated POJO with @Data and @Builder,
 * we test the generated methods: builder, getters, setters, equals, hashCode, toString.
 */
class AttendanceSummaryTest {

    @Test
    @DisplayName("Given valid fields when building AttendanceSummary then all fields are set correctly")
    void givenValidFields_whenBuildingAttendanceSummary_thenAllFieldsAreSetCorrectly() {
        // Arrange
        Long employeeId = 1001L;
        long totalDays = 30L;
        long presentDays = 25L;
        long absentDays = 2L;
        long lateDays = 1L;
        long halfDays = 1L;
        long leaveDays = 1L;

        // Act
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(employeeId)
                .totalDays(totalDays)
                .presentDays(presentDays)
                .absentDays(absentDays)
                .lateDays(lateDays)
                .halfDays(halfDays)
                .leaveDays(leaveDays)
                .build();

        // Assert
        assertNotNull(summary);
        assertEquals(employeeId, summary.getEmployeeId());
        assertEquals(totalDays, summary.getTotalDays());
        assertEquals(presentDays, summary.getPresentDays());
        assertEquals(absentDays, summary.getAbsentDays());
        assertEquals(lateDays, summary.getLateDays());
        assertEquals(halfDays, summary.getHalfDays());
        assertEquals(leaveDays, summary.getLeaveDays());
    }

    @Test
    @DisplayName("Given null employeeId when building AttendanceSummary then employeeId is null")
    void givenNullEmployeeId_whenBuildingAttendanceSummary_thenEmployeeIdIsNull() {
        // Arrange
        long totalDays = 0L;

        // Act
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(null)
                .totalDays(totalDays)
                .build();

        // Assert
        assertNotNull(summary);
        assertNull(summary.getEmployeeId());
        assertEquals(totalDays, summary.getTotalDays());
    }

    @Test
    @DisplayName("Given zero values for all fields when building AttendanceSummary then all fields are zero")
    void givenZeroValuesForAllFields_whenBuildingAttendanceSummary_thenAllFieldsAreZero() {
        // Arrange
        Long employeeId = 0L;

        // Act
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(employeeId)
                .totalDays(0L)
                .presentDays(0L)
                .absentDays(0L)
                .lateDays(0L)
                .halfDays(0L)
                .leaveDays(0L)
                .build();

        // Assert
        assertNotNull(summary);
        assertEquals(employeeId, summary.getEmployeeId());
        assertEquals(0L, summary.getTotalDays());
        assertEquals(0L, summary.getPresentDays());
        assertEquals(0L, summary.getAbsentDays());
        assertEquals(0L, summary.getLateDays());
        assertEquals(0L, summary.getHalfDays());
        assertEquals(0L, summary.getLeaveDays());
    }

    @Test
    @DisplayName("Given large boundary values when building AttendanceSummary then fields hold max long values")
    void givenLargeBoundaryValues_whenBuildingAttendanceSummary_thenFieldsHoldMaxLongValues() {
        // Arrange
        Long employeeId = Long.MAX_VALUE;
        long maxDays = Long.MAX_VALUE;

        // Act
        AttendanceSummary summary = AttendanceSummary.builder()
                .employeeId(employeeId)
                .totalDays(maxDays)
                .presentDays(maxDays)
                .absentDays(maxDays)
                .lateDays(maxDays)
                .halfDays(maxDays)
                .leaveDays(maxDays)
                .build();

        // Assert
        assertNotNull(summary);
        assertEquals(employeeId, summary.getEmployeeId());
        assertEquals(maxDays, summary.getTotalDays());
        assertEquals(maxDays, summary.getPresentDays());
        assertEquals(maxDays, summary.getAbsentDays());
        assertEquals(maxDays, summary.getLateDays());
        assertEquals