package com.blooddonation.service;

import com.blooddonation.model.BloodRequest;
import com.blooddonation.model.Hospital;
import com.blooddonation.patterns.BloodRequestFactory;
import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    public BloodRequest createBloodRequest(java.util.UUID hospitalId, String bloodType, double quantity, String severity) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
                
        BloodRequest request = BloodRequestFactory.createRequest(hospital, bloodType, quantity, severity);
        
        return bloodRequestRepository.save(request);
    }
    
}
