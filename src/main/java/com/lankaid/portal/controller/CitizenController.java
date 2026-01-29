package com.lankaid.portal.controller;


import java.util.List;
import com.lankaid.portal.entity.Citizen;
import com.lankaid.portal.repository.CitizenRepository;
import com.lankaid.portal.service.NicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;


@RestController
public class CitizenController {

    //pdf service injection
    @Autowired
    private com.lankaid.portal.service.PdfService pdfService;

    @Autowired
    private NicService nicService;

    @Autowired
    private CitizenRepository citizenRepository; // Inject the Database Manager

    @GetMapping("/check-nic")
    public String checkIdentity(@RequestParam String nic, @RequestParam(required = false) String requestType) {

        // 1. DUPLICATE CHECK
        if (citizenRepository.existsByNic(nic)) {
            return "Error: This NIC is already registered in the system!";
        }

        // 2. LOGIC
        String result = nicService.validateNic(nic);

        // 3. SAVE REQUEST
        if (!result.startsWith("Invalid") && !result.startsWith("Error")) {
            Citizen citizen = new Citizen();
            citizen.setNic(nic);
            citizen.setVerificationStatus(result);

            // Save the specific service they asked for (Default to "General Verification" if empty)
            citizen.setRequestType(requestType != null ? requestType : "General Verification");

            if (result.contains("Female")) citizen.setGender("Female");
            else citizen.setGender("Male");

            citizenRepository.save(citizen);

            return result + "\n\n[System]: Request for '" + citizen.getRequestType() + "' submitted successfully!";
        }

        return result;
    }

    @GetMapping("/citizens")
    public java.util.List<Citizen> getAllCitizens(@RequestParam(required = false) String query) {
        // If the user typed something in the search box...
        if (query != null && !query.isEmpty()) {
            return citizenRepository.findByNicContaining(query);
        }
        // Otherwise, show everyone
        return citizenRepository.findAll();
    }

    // This link listens for DELETE commands
    @org.springframework.web.bind.annotation.DeleteMapping("/citizens/{id}")
    public String deleteCitizen(@org.springframework.web.bind.annotation.PathVariable Long id) {
        citizenRepository.deleteById(id);
        return "Deleted successfully";
    }

    // Import this: import org.springframework.web.bind.annotation.PutMapping;

    @org.springframework.web.bind.annotation.PutMapping("/citizens/{id}/status")
    public Citizen updateStatus(@org.springframework.web.bind.annotation.PathVariable Long id,
                                @RequestParam String newStatus) {
        // 1. Find the citizen
        Citizen citizen = citizenRepository.findById(id).orElseThrow();

        // 2. Update the status
        citizen.setStatus(newStatus);

        // 3. Save updates
        return citizenRepository.save(citizen);
    }

    @GetMapping("/citizens/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@org.springframework.web.bind.annotation.PathVariable Long id) {

        Citizen citizen = citizenRepository.findById(id).orElseThrow();

        // Generate PDF
        byte[] pdfBytes = pdfService.generateCertificate(citizen);

        // Send to browser as a download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate_" + citizen.getNic() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}