package edu.netcracker.project.logistic.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import edu.netcracker.project.logistic.model.Office;
import edu.netcracker.project.logistic.model.Person;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratePdfReport {


    public static ByteArrayInputStream managerReport(ArrayList<Person> employees, ArrayList<Office> offices, List listEmployees, List listOffices, List listOrders, String firstName, String lastName) {

        Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfPTable tableEmployees = new PdfPTable(6);
            tableEmployees.setWidthPercentage(100.0f);
            tableEmployees.setPaddingTop(10);
            tableEmployees.setWidths(new int[]{3, 3, 7, 7, 10, 7});


            Font headFont = FontFactory.getFont(FontFactory.TIMES);
            headFont.setColor(BaseColor.WHITE);

            PdfPCell hcell;


            hcell = new PdfPCell(new Phrase("First name", headFont));
            hcell.setBackgroundColor(BaseColor.DARK_GRAY);
            hcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableEmployees.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Last name", headFont));
            hcell.setBackgroundColor(BaseColor.DARK_GRAY);
            hcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableEmployees.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Email", headFont));
            hcell.setBackgroundColor(BaseColor.DARK_GRAY);
            hcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableEmployees.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Telephone", headFont));
            hcell.setBackgroundColor(BaseColor.DARK_GRAY);
            hcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableEmployees.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Roles", headFont));
            hcell.setBackgroundColor(BaseColor.DARK_GRAY);
            hcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableEmployees.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Amount handled order", headFont));
            hcell.setBackgroundColor(BaseColor.DARK_GRAY);
            hcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableEmployees.addCell(hcell);


            for (Person employee : employees) {

                PdfPCell cell;

                cell = new PdfPCell(new Phrase(employee.getContact().getFirstName()));
                cell.setUseAscender(true);
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                tableEmployees.addCell(cell);

                cell = new PdfPCell(new Phrase(employee.getContact().getLastName()));
                cell.setUseAscender(true);
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                tableEmployees.addCell(cell);


                cell = new PdfPCell(new Phrase(employee.getContact().getEmail()));
                cell.setUseAscender(true);
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                tableEmployees.addCell(cell);

                cell = new PdfPCell(new Phrase(employee.getContact().getPhoneNumber()));
                cell.setUseAscender(true);
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                tableEmployees.addCell(cell);

                cell = new PdfPCell(new Phrase(employee.getRoles().toString()));
                cell.setUseAscender(true);
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                tableEmployees.addCell(cell);

                cell = new PdfPCell(new Phrase(employee.getId().toString()));
                cell.setUseAscender(true);
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                tableEmployees.addCell(cell);

            }

            PdfPTable tableOffices = new PdfPTable(3);
            tableOffices.setWidthPercentage(100.0f);
            tableOffices.setPaddingTop(10);
            tableOffices.setWidths(new int[]{2, 10, 10});

            hcell = new PdfPCell(new Phrase("Id", headFont));
            hcell.setBackgroundColor(BaseColor.DARK_GRAY);
            hcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableOffices.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Name storage", headFont));
            hcell.setBackgroundColor(BaseColor.DARK_GRAY);
            hcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableOffices.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Name address", headFont));
            hcell.setBackgroundColor(BaseColor.DARK_GRAY);
            hcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableOffices.addCell(hcell);


            for (Office office : offices) {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(office.getOfficeId().toString()));
                cell.setUseAscender(true);
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                tableOffices.addCell(cell);

                cell = new PdfPCell(new Phrase(office.getName()));
                cell.setUseAscender(true);
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                tableOffices.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(office.getAddress())));
                cell.setUseAscender(true);
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                tableOffices.addCell(cell);
            }


            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Generated Manager  -  " + firstName + " " + lastName + " at  " + LocalDate.now()));
            document.add(new Paragraph("  "));
            document.add(new Paragraph("Employees table"));
            document.add(new Paragraph("  "));
            document.add(tableEmployees);
            document.add(new Paragraph("  "));
            document.add(new Paragraph("Statistics employees"));
            document.add(listEmployees);
            document.add(new Paragraph("  "));
            document.add(new Paragraph("Offices table"));
            document.add(new Paragraph("  "));
            document.add(tableOffices);
            document.add(new Paragraph("  "));
            document.add(new Paragraph("Statistics offices"));
            document.add(listOffices);
            document.add(new Paragraph("  "));
            document.add(new Paragraph("Statistics orders"));
            document.add(new Paragraph("  "));
            document.add(listOrders);

            document.close();

        } catch (DocumentException ex) {

            Logger.getLogger(GeneratePdfReport.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
