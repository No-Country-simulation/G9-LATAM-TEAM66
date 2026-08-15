package com.team66.backend.repository;


import com.team66.backend.model.RegistroConsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroConsumoReposotory extends JpaRepository<RegistroConsumo, Long> {
}
