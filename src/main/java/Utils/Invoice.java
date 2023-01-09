package Utils;

import CarModel.Car;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Invoice {
    long time; // timestamp wygenerowania faktury, System.currentTimeMillis()
    String title; // tytuł/numer faktury
    String seller; // sprzedawca
    String buyer; // nabywca
    ArrayList<Car> list; // ArrayList samochodów do wyświetlenia na fakturze

    public Invoice(String title, String seller, String buyer, ArrayList<Car> list) {
        this.time =  System.currentTimeMillis();
        this.title = title;
        this.seller = seller;
        this.buyer = buyer;
        this.list = list;
    }
    public String generate(){
        try {
            Document document = new Document();
            String path = String.format("faktury/%s.pdf", Helpers.randomUUID());
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            Font fontRed = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.RED);
            Font fontBig = FontFactory.getFont(FontFactory.COURIER, 24, BaseColor.BLACK);
            Font fontSmall = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);

            document.add( new Paragraph("FAKTURA VAT/"+CustomDate.now(), fontBig));

            document.add( new Paragraph("Sprzedawca: "+seller, font));
            document.add( new Paragraph("Nabywca:  "+buyer, font));

            document.add( new Paragraph("Tytuł:  "+title, fontRed));

            PdfPTable table = new PdfPTable(5);
            int i = 0;
            int prizeSum = 0;
            for(Car car : list){
                i++;
                double prize = Helpers.randomPrize();
                double vat = Helpers.randomVat();
                double value = prize + prize*vat/100;
                prizeSum += value;
                PdfPCell c = new PdfPCell(new Phrase(""+i, font));table.addCell(c);
                c = new PdfPCell(new Phrase(""+car.getYear(), font));table.addCell(c);
                c = new PdfPCell(new Phrase(""+prize, font));table.addCell(c);
                c = new PdfPCell(new Phrase(vat+"%", font));table.addCell(c);
                c = new PdfPCell(new Phrase(""+value, font));table.addCell(c);
            }
            document.add(table);
            document.add( new Paragraph("DO ZAPŁATY: "+prizeSum, fontBig));
            document.close();
            return path;
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
