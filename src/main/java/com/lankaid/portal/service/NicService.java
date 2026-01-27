package com.lankaid.portal.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class NicService {

    public String validateNic(String nic) {
        int year = 0;
        int days = 0;
        String gender = "";

        try {
            // 1. Extract Year and Days
            if (nic.length() == 10) {
                year = 1900 + Integer.parseInt(nic.substring(0, 2));
                days = Integer.parseInt(nic.substring(2, 5));
            }
            else if (nic.length() == 12) {
                year = Integer.parseInt(nic.substring(0, 4));
                days = Integer.parseInt(nic.substring(4, 7));
            }
            else {
                return "Invalid NIC: Wrong Length";
            }
        } catch (NumberFormatException e) {
            return "Invalid NIC: Contains letters where numbers should be.";
        }

        // 2. Determine Gender
        if (days > 500) {
            gender = "Female";
            days = days - 500;
        } else {
            gender = "Male";
        }

        // 3. Validation
        if (days < 1 || days > 366) {
            return "Invalid NIC: Date numbers are wrong.";
        }

        // --- THE FIX START ---
        // Sri Lankan NICs treat Feb as having 29 days for EVERY year.
        // If it is NOT a leap year, and the day is after Feb 28 (Day 59),
        // we must subtract 1 to match the real calendar.

        boolean isLeapYear = java.time.Year.of(year).isLeap();

        // If it's NOT a leap year AND the day count is past Feb (Day > 59)
        if (!isLeapYear && days > 59) {
            days = days - 1;
        }
        // --- THE FIX END ---

        // 4. Calculate Date
        try {
            LocalDate birthDate = LocalDate.ofYearDay(year, days);

            return "Identity Verified! \n" +
                    "Year: " + year + " \n" +
                    "Birthday: " + birthDate.format(DateTimeFormatter.ofPattern("MMMM dd")) + " \n" +
                    "Gender: " + gender;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}