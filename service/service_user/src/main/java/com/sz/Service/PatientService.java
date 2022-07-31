package com.sz.Service;

import com.sz.model.User.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> findAll(String userId);

    Patient findById(Long id);
}
