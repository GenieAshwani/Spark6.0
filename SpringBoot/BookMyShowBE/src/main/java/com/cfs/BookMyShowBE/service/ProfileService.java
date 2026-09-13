package com.cfs.BookMyShowBE.service;


import com.cfs.BookMyShowBE.dto.CreateProfileRequest;
import com.cfs.BookMyShowBE.dto.ProfileResponse;
import com.cfs.BookMyShowBE.entity.Customer;
import com.cfs.BookMyShowBE.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ProfileService {

    private final CustomerRepository customerRepository;


    public ProfileService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public ProfileResponse create(CreateProfileRequest request)
    {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String phone= normalizePhone(request.phone());
        if (customerRepository.existsByEmail(email))
        {
            throw new ProfileConflictException("An account already exists with this email");
        }

        if(customerRepository.existsByPhone(phone))
        {
            throw new ProfileConflictException("An account already exists with this phone number");
        }
        return ProfileResponse.from(customerRepository.save(new Customer(request.name().trim(),email,phone)));
    }

    public ProfileResponse login(String identifier)
    {
        String value=identifier == null ? "" : identifier.trim();
        String phone =value.replaceAll("\\D","");
        Customer customer=value.contains("@")
                ? customerRepository.findByEmail(value.toLowerCase(Locale.ROOT)).orElse(null)
                : customerRepository.findByPhone(phone).orElse(null);

        if(customer==null)
        {
            throw new ResourceNotFoundException("NO profile found for this phone number or email");
        }

        return ProfileResponse.from(customer);
    }

    public String normalizePhone(String phone)
    {
        String normalize=phone==null ? "" : phone.replaceAll("\\D","");
        if(normalize.length()<10 || normalize.length()>15)
        {
            throw  new IllegalArgumentException("Enter valid Phone number");
        }
        return normalize;
    }
}
