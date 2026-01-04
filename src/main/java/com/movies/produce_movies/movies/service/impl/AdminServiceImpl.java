package com.movies.produce_movies.movies.service.impl;

import com.movies.produce_movies.entity.Admin;
import com.movies.produce_movies.movies.service.AdminService;
import com.movies.produce_movies.repository.AdminRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    public Admin createAdmin(Admin admin) {
        admin.setId(null);
        return adminRepository.save(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }
}


