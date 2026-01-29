package com.lankaid.portal.controller;

import java.util.List;
import com.lankaid.portal.entity.Citizen;
import com.lankaid.portal.repository.CitizenRepository;
import com.lankaid.portal.service.NicService;
import com.lankaid.portal.service.EmailService; // Import Email Service
import com.lankaid.portal.service.PdfService;   // Import PDF Service

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

@RestController
public class CitizenController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private NicService nicService;

    @Autowired
    private EmailService emailService; // Inject the Email Service

    @Autowired
    private CitizenRepository citizenRepository;

    // --- 1. CREATE REQUEST (Home Page) ---
    @GetMapping("/check-nic")
    public String checkIdentity(@RequestParam String nic,
                                @RequestParam(required = false) String requestType,
                                @RequestParam String email) { // NEW PARAMETER

        // 1. DUPLICATE CHECK
        if (citizenRepository.existsByNic(nic)) {
            return "Error: This NIC is already registered in the system!";
        }

        // 2. LOGIC CHECK
        String result = nicService.validateNic(nic);

        // 3. SAVE DATA
        if (!result.startsWith("Invalid") && !result.startsWith("Error")) {
            Citizen citizen = new Citizen();
            citizen.setNic(nic);
            citizen.setVerificationStatus(result);
            citizen.setRequestType(requestType != null ? requestType : "General Verification");
            citizen.setEmail(email); // SAVE THE EMAIL!

            if (result.contains("Female")) citizen.setGender("Female");
            else citizen.setGender("Male");

            citizenRepository.save(citizen);

            return result + "\n\n[System]: Request submitted! Confirmation will be sent to: " + email;
        }

        return result;
    }

    // --- 2. VIEW ALL / SEARCH (Dashboard) ---
    @GetMapping("/citizens")
    public List<Citizen> getAllCitizens(@RequestParam(required = false) String query) {
        if (query != null && !query.isEmpty()) {
            return citizenRepository.findByNicContaining(query);
        }
        return citizenRepository.findAll();
    }

    // --- 3. DELETE (Dashboard) ---
    @DeleteMapping("/citizens/{id}")
    public String deleteCitizen(@PathVariable Long id) {
        citizenRepository.deleteById(id);
        return "Deleted successfully";
    }

    // --- 4. UPDATE STATUS & SEND EMAIL (Dashboard) ---
    @PutMapping("/citizens/{id}/status")
    public Citizen updateStatus(@PathVariable Long id, @RequestParam String newStatus) {

        Citizen citizen = citizenRepository.findById(id).orElseThrow();
        citizen.setStatus(newStatus);

        Citizen savedCitizen = citizenRepository.save(citizen);

        // --- REAL EMAIL LOGIC ---
        // Retrieve the email stored in the database for this specific user
        String citizenEmail = citizen.getEmail();

        if (citizenEmail != null && !citizenEmail.isEmpty()) {
            if ("Approved".equals(newStatus)) {
                emailService.sendApprovalEmail(citizenEmail, citizen.getNic(), citizen.getRequestType());
            } else if ("Rejected".equals(newStatus)) {
                emailService.sendRejectionEmail(citizenEmail, citizen.getNic());
            }
        } else {
            System.out.println("⚠️ No email found for user " + citizen.getNic());
        }

        return savedCitizen;
    }

    // --- 5. DOWNLOAD PDF (Dashboard) ---
    @GetMapping("/citizens/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {

        Citizen citizen = citizenRepository.findById(id).orElseThrow();

        byte[] pdfBytes = pdfService.generateCertificate(citizen);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate_" + citizen.getNic() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}