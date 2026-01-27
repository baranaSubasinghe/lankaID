package com.lankaid.portal.repository;

import com.lankaid.portal.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    // Believe it or not, this is empty!
    // Spring Boot automatically gives us methods like .save(), .findAll(), .delete()

    boolean existsByNic(String nic);
}