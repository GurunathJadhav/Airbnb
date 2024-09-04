package com.airbnb.service;

import com.airbnb.dto.BookindDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Service
public class PdfService {

    public boolean generatePdf(String filename, BookindDto dto)  {


        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.ORANGE);
            Chunk bookingId = new Chunk("Booking Confirmation For Booking Id: "+dto.getBookingId(), font);
            Chunk guestName = new Chunk("Guest Name : "+dto.getGuestName(), font);
            Chunk price = new Chunk("Price per night : "+dto.getPrice(), font);
            Chunk totalNights = new Chunk("Total Nights : "+dto.getTotalNights(), font);
            Chunk totalPrice = new Chunk("Total Price : "+dto.getTotalPrice(), font);


            document.add(bookingId);
            document.add(new Paragraph("\n"));
            document.add(guestName);
            document.add(new Paragraph("\n"));
            document.add(price);
            document.add(new Paragraph("\n"));
            document.add(totalNights);
            document.add(new Paragraph("\n"));
            document.add(totalPrice);


            document.close();
            return true;

        } catch (Exception e) {
            System.out.println("error");
            return false;

        }



    }
}
