package com.cfs.BookMyShowBE.controller;

import com.cfs.BookMyShowBE.dto.BookingResponse;
import com.cfs.BookMyShowBE.dto.CreateProfileRequest;
import com.cfs.BookMyShowBE.dto.ProfileResponse;
import com.cfs.BookMyShowBE.service.BookingService;
import com.cfs.BookMyShowBE.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final BookingService bookingService;

    public ProfileController(ProfileService profileService, BookingService bookingService) {
        this.profileService = profileService;
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse create(@Valid @RequestBody CreateProfileRequest request)
    {
        return profileService.create(request);
    }

    @GetMapping("/login")
    public ProfileResponse login(@RequestParam String identifier)
    {
        return profileService.login(identifier);
    }

    @GetMapping("/{profileId}/bookings")
    public List<BookingResponse> bookings(@PathVariable long profileId)
    {
        return bookingService.findByProfileId(profileId);
    }
}
