package com.employee.system.service;

import com.employee.system.dto.DocumentDTO;
import com.employee.system.entity.Document;
import com.employee.system.entity.Employee;
import com.employee.system.repository.DocumentRepository;
import com.employee.system.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DocumentService documentService;

    @TempDir
    Path tempDir;

    private Employee testEmployee;
    private Document testDocument;
    private MultipartFile testFile;

    @BeforeEach
    void setUp() throws Exception {
        // Create a temporary uploads directory and inject it into the service via reflection
        Path uploadsDir = tempDir.resolve("uploads");
        Files.createDirectories(uploadsDir);
        
        // Use reflection to set the private field 'uploadsDir' to our temp directory
        java.lang.reflect.Field field = DocumentService.class.getDeclaredField("uploadsDir");
        field.setAccessible(true);
        field.set(documentService, uploadsDir);

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setName("John Doe");

        testDocument = new Document();
        testDocument.setId(100L);
        testDocument.setEmployee(testEmployee);
        testDocument.setDocType("CONTRACT");
        testDocument.setOriginalFileName("contract.pdf");
        testDocument.setStoredFileName("uuid_contract.pdf");
        testDocument.setContentType("application/pdf");
        testDocument.setSize(1024L);
        testDocument.setCreatedAt(LocalDate.now());
        testDocument.setUpdatedAt(LocalDate.now());

        // Create a mock MultipartFile
        testFile = mock(MultipartFile.class);
        when(testFile.getOriginalFilename()).thenReturn("contract.pdf");
        when(testFile.getContentType()).thenReturn("application/pdf");
        when(testFile.getSize()).thenReturn(1024L);
        when(testFile.getInputStream()).thenReturn(new ByteArrayInputStream("file content".getBytes()));
    }

    @Test
    @DisplayName("Given valid file and existing employee, when uploading document, then return saved DocumentDTO")
    void givenValidFileAndExistingEmployee_whenUploadDocument_thenReturnSavedDocumentDTO() throws IOException {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document doc = invocation.getArgument(0);
            doc.setId(100L);
            return doc;
        });

        // Act
        DocumentDTO result = documentService.uploadDocument(testFile, 1L, "CONTRACT");

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1L, result.getEmployeeId());
        assertEquals("CONTRACT", result.getDocType());
        assertEquals("contract.pdf", result.getOriginalFileName());
        assertEquals("application/pdf", result.getContentType());
        assertEquals(1024L, result.getSize());
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    @DisplayName("Given non-existing employee, when uploading document, then throw RuntimeException")
    void givenNonExistingEmployee_whenUploadDocument_thenThrowRuntimeException() throws IOException {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            documentService.uploadDocument(testFile, 999L, "CONTRACT");
        });
        assertTrue(exception.getMessage().contains("Employee not found