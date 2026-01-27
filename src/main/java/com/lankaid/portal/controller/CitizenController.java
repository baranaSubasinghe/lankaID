package com.lankaid.portal.controller;

import com.lankaid.portal.entity.Citizen;
import com.lankaid.portal.repository.CitizenRepository;
import com.lankaid.portal.service.NicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CitizenController {

    @Autowired
    private NicService nicService;

    @Autowired
    private CitizenRepository citizenRepository; // Inject the Database Manager

    @GetMapping("/check-nic")
    public String checkIdentity(@RequestParam String nic) {

        // 1. Do the Logic
        String result = nicService.validateNic(nic);

        // 2. If valid, SAVE to Database
        if (!result.startsWith("Invalid")) {
            Citizen citizen = new Citizen();
            citizen.setNic(nic);
            citizen.setVerificationStatus(result);

            // Extract gender/year crudely for now (or improve service later)
            if (result.contains("Female")) citizen.setGender("Female");
            else citizen.setGender("Male");

            // Save to DB!
            citizenRepository.save(citizen);

            return result + "\n\n[System]: Citizen saved to Database successfully!";
        }

        return result;
    }
}