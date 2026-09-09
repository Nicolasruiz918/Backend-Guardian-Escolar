package com.guardianescolar.api.modules.students.controller;

import com.guardianescolar.api.modules.students.dto.StudentRequest;
import com.guardianescolar.api.modules.students.dto.StudentResponse;
import com.guardianescolar.api.modules.students.service.StudentService;
import com.guardianescolar.api.shared.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<StudentResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return service.list(principal.id());
    }

    @GetMapping("/{studentId}")
    public StudentResponse get(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId) {
        return service.get(principal.id(), studentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody StudentRequest request) {
        return service.create(principal.id(), request);
    }

    @PatchMapping("/{studentId}")
    public StudentResponse update(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId,
            @Valid @RequestBody StudentRequest request) {
        return service.update(principal.id(), studentId, request);
    }

    @DeleteMapping("/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID studentId) {
        service.delete(principal.id(), studentId);
    }
}
