package com.lankaid.portal.repository;

import com.lankaid.portal.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    // Spring Boot automatically gives us methods like .save(), .findAll(), .delete()

    boolean existsByNic(String nic);

    // Finds any citizen whose NIC contains the search text
    // Example: Searching "95" finds "1995..." and "95..."
    java.util.List<Citizen> findByNicContaining(String nic);

}
