package com.movies.produce_movies.movies.service;

import com.movies.produce_movies.entity.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    Admin createAdmin(Admin admin);

    Optional<Admin> getAdminById(Long id);

    List<Admin> getAllAdmins();

    void deleteAdmin(Long id);
}


