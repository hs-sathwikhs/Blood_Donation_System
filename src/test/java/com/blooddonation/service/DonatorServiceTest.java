package com.blooddonation.service;

import com.blooddonation.model.Donator;
import com.blooddonation.repository.DonatorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonatorServiceTest {

    @Mock
    private DonatorRepository donatorRepository;

    @InjectMocks
    private DonatorService donatorService;

    @Test
    public void underageDonorIsIneligible() {
        Donator d = new Donator();
        d.setAge(16);
        java.util.UUID id = java.util.UUID.randomUUID();
        when(donatorRepository.findById(id)).thenReturn(Optional.of(d));

        boolean eligible = donatorService.checkEligibility(id);
        assertFalse(eligible);
    }
}
