package com.movies.produce_movies.controller;

import com.movies.produce_movies.entity.Admin;
import com.movies.produce_movies.movies.service.AdminService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<Admin> createAdmin(@Valid @RequestBody Admin admin) {
        Admin created = adminService.createAdmin(admin);
        return ResponseEntity.created(URI.create("/api/admins/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdmin(@PathVariable Long id) {
        return adminService.getAdminById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}


