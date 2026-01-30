package com.lankaid.portal.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lankaid.portal.entity.Citizen;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
//for QR
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
@Service
public class PdfService {

    public byte[] generateCertificate(Citizen citizen) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 1. ADD THE SRI LANKAN EMBLEM (From Local File)
            try {

                Image emblem = Image.getInstance(getClass().getResource("/logo.png"));

                emblem.scaleToFit(100, 100); // Adjust size (Width, Height)
                emblem.setAlignment(Element.ALIGN_CENTER);
                document.add(emblem);
            } catch (Exception e) {
                // Fallback text if image fails
                Paragraph missingLogo = new Paragraph("[Official Emblem]", FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
                missingLogo.setAlignment(Element.ALIGN_CENTER);
                document.add(missingLogo);
            }

            // add official header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph header = new Paragraph("\nLANKA-ID DIGITAL CITIZEN PORTAL", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph subHeader = new Paragraph("GOVERNMENT OF SRI LANKA", subHeaderFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(subHeader);

            document.add(new Paragraph("\n")); // Space

            // draw a line
            LineSeparator line = new LineSeparator();
            line.setLineColor(Color.GRAY);
            document.add(line);

            document.add(new Paragraph("\n"));

            // add date and ref number
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

            Paragraph datePara = new Paragraph("Date: " + todayDate, smallFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            document.add(datePara);

            Paragraph refPara = new Paragraph("Ref No: " + citizen.getId() + "/LKA/2026", smallFont);
            refPara.setAlignment(Element.ALIGN_LEFT);
            document.add(refPara);

            document.add(new Paragraph("\n\n")); // Big Space

            // the formal body content
            Font subjectFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.UNDERLINE);
            Paragraph subject = new Paragraph("SUBJECT: OFFICIAL VERIFICATION OF IDENTITY", subjectFont);
            subject.setAlignment(Element.ALIGN_CENTER);
            document.add(subject);

            document.add(new Paragraph("\n"));

            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            String bodyText = "To Whom It May Concern,\n\n" +
                    "This is to certify that the details of the citizen holding the National Identity Card (NIC) number " +
                    citizen.getNic() + " have been verified against the National Database. \n\n" +
                    "The verified details are as follows:";

            Paragraph body = new Paragraph(bodyText, bodyFont);
            body.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(body);

            document.add(new Paragraph("\n"));

            //citizen detail box
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            document.add(new Paragraph("     NIC Number:       " + citizen.getNic(), boldFont));
            document.add(new Paragraph("     Gender:               " + citizen.getGender(), bodyFont));
            document.add(new Paragraph("     Service Type:      " + citizen.getRequestType(), bodyFont));
            document.add(new Paragraph("     Current Status:   " + citizen.getStatus(), boldFont));

            document.add(new Paragraph("\n\n\n\n"));


            Paragraph systemNote = new Paragraph("This is a computer-generated document. No signature is required.",
                    FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 8, Color.GRAY));
            systemNote.setAlignment(Element.ALIGN_CENTER);
            document.add(systemNote);
            document.add(new Paragraph("\n"));

            // Generate QR linking to a fake "verification" URL

            String verificationLink = "Official Verification: " + citizen.getNic();

            Image qrCode = generateQrCode(verificationLink);
            if (qrCode != null) {
                document.add(qrCode);
            }

            Paragraph scanText = new Paragraph("Scan to Verify", FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY));
            scanText.setAlignment(Element.ALIGN_CENTER);
            document.add(scanText);


            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    // Helper function to create QR Code Image
    private Image generateQrCode(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

            // Convert to byte array
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

            // Convert to iText Image
            Image qrImage = Image.getInstance(pngOutputStream.toByteArray());
            qrImage.scaleToFit(100, 100);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            return qrImage;
        } catch (Exception e) {
            return null;
        }
    }
}