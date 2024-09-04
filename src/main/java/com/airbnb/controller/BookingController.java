package com.airbnb.controller;

import com.airbnb.dto.BookindDto;
import com.airbnb.entity.Booking;
import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.BookingRepository;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.service.BucketService;
import com.airbnb.service.PdfService;

import com.airbnb.service.SmsService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


@RestController
@RequestMapping("api/v1/booking")
public class BookingController {
    
    private BookingRepository bookingRepository;
    private PropertyRepository propertyRepository;
    private PdfService pdfService;
    private BucketService bucketService;
    private SmsService smsService;

    public BookingController(BookingRepository bookingRepository, PropertyRepository propertyRepository, PdfService pdfService, BucketService bucketService, SmsService smsService) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.pdfService = pdfService;
        this.bucketService = bucketService;
        this.smsService = smsService;
        ;
    }
    
    @PostMapping("/createBookings/{propertyId}")
    public ResponseEntity<?> createBooking(
            @RequestBody Booking booking,
            @AuthenticationPrincipal PropertyUser user,
            @PathVariable long propertyId
            ) throws IOException {
        Property property = propertyRepository.findById(propertyId).get();
        booking.setProperty(property);
        Integer nightPrice = property.getNightPrice();
        Integer totalNights = booking.getTotalNights();
        Integer totalPrice = nightPrice * totalNights;
        booking.setTotalPrice(totalPrice);
        booking.setPropertyUser(user);
        Booking createdBooking = bookingRepository.save(booking);
        BookindDto dto=new BookindDto();
        dto.setGuestName(createdBooking.getGuestName());
        dto.setPrice(nightPrice);
        dto.setTotalPrice(createdBooking.getTotalPrice());
        dto.setBookingId(createdBooking.getId());
        dto.setTotalNights(createdBooking.getTotalNights());
        boolean b = pdfService.generatePdf("C://Users//User//Downloads//Booking Confirmation " + createdBooking.getId()+".pdf", dto);
       if(b) {
           MultipartFile file = BookingController.convert("C://Users//User//Downloads//Booking Confirmation " + createdBooking.getId() + ".pdf");
           String url = bucketService.uploadFile(file, "myairbnbbucket1");
           smsService.sendSms("+918296834362","Your Booking is confirmed. Click here for more information "+url);

           return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);

       }else{
           return new ResponseEntity<>("Something went wrong", HttpStatus.CREATED);
       }

    }


   public static MultipartFile convert(String filePath) throws IOException {
       File file = new File(filePath);
       byte[] fileContent = Files.readAllBytes(file.toPath());
       Resource resource = new ByteArrayResource(fileContent);
       MultipartFile multipartFile = new MultipartFile() {
           @Override
           public String getName() {
               return file.getName();
           }

           @Override
           public String getOriginalFilename() {
               return file.getName();
           }

           @Override
           public String getContentType() {
               return null;
           }

           @Override
           public boolean isEmpty() {
               return fileContent.length == 0;
           }

           @Override
           public long getSize() {
               return fileContent.length;
           }

           @Override
           public byte[] getBytes() throws IOException {
               return fileContent;
           }

           @Override
           public InputStream getInputStream() throws IOException {
               return resource.getInputStream();
           }

           @Override
           public void transferTo(File dest) throws IOException, IllegalStateException {
               Files.write(dest.toPath(), fileContent);
           }
       };
       return multipartFile;

   }
   }
