package com.careerx.serviceImpl;

import com.careerx.entities.AssessmentSession;
import com.careerx.services.PdfService;
import com.fasterxml.jackson.databind.JsonNode;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generateAssessmentReportPdf(AssessmentSession session, JsonNode recommendation , String studentname) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Font.BOLD, java.awt.Color.DARK_GRAY);
            Paragraph title = new Paragraph("Career Assessment Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Student Name: " + studentname)); // We should probably get name from
                                                                                 // profile
            document.add(new Paragraph("Date: " + session.getCreatedAt().toString()));
            if (session.getScore() != null) {
                document.add(new Paragraph("Aptitude Score: " + String.format("%.1f", session.getScore()) + "%"));
            }
            document.add(new Paragraph(" "));

            if (recommendation != null) {
                document.add(new Paragraph("AI Recommendation Summary:",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                document.add(new Paragraph("Recommended Career: " + recommendation.path("recommendedCareer").asText()));
                document.add(new Paragraph("AI Fit Score: " + recommendation.path("skillScore").asInt() + "/100"));
                document.add(new Paragraph(" "));

                document.add(new Paragraph("Strengths:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                JsonNode strengths = recommendation.path("strengths");
                if (strengths.isArray()) {
                    for (JsonNode s : strengths) {
                        document.add(new Paragraph(" • " + s.asText()));
                    }
                }

                document.add(new Paragraph(" "));
                document.add(
                        new Paragraph("Areas for Improvement:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                JsonNode weaknesses = recommendation.path("weaknesses");
                if (weaknesses.isArray()) {
                    for (JsonNode w : weaknesses) {
                        document.add(new Paragraph(" • " + w.asText()));
                    }
                }
            }

            document.add(new Paragraph(" "));
            document.add(
                    new Paragraph("Career Roadmap Suggestion:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph("View your personalized step-by-step roadmap on your CareerX dashboard."));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Best of luck with your career journey!",
                    FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10)));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }

        return out.toByteArray();
    }
}
