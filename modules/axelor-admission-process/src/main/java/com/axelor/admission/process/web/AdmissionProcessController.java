package com.axelor.admission.process.web;

import com.axelor.admission.db.AdmissionEntry;
import com.axelor.admission.db.AdmissionProcess;
import com.axelor.admission.db.CollegeEntry;
import com.axelor.admission.db.FacultyEntry;
import com.axelor.admission.db.repo.AdmissionEntryRepository;
import com.axelor.admission.db.repo.FacultyEntryRepository;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AdmissionProcessController {
  @Inject AdmissionEntryRepository repo;
  @Inject FacultyEntryRepository facultyEntryRepository;

  @Transactional
  public void admissionProccess(ActionRequest request, ActionResponse response) {

    List<AdmissionEntry> entry = Beans.get(AdmissionEntryRepository.class).all().fetch();

    AdmissionProcess admissionProcess = request.getContext().asType(AdmissionProcess.class);
    LocalDate start = admissionProcess.getFromDate();
    LocalDate end = admissionProcess.getToDate();
    List<AdmissionEntry> admissionEntriesList =
        entry
            .stream()
            .sorted(Comparator.comparing(AdmissionEntry::getMerit).reversed())
            .collect(Collectors.toList());

    for (AdmissionEntry admissionEntry : admissionEntriesList) {

      if (admissionEntry.getRegistrationDate().isAfter(start)
          && admissionEntry.getRegistrationDate().isBefore(end)) {
        System.out.println(admissionEntry.getRegistrationDate());

        if (admissionEntry.getStatus() == 2) {
          List<CollegeEntry> collegeEntries = admissionEntry.getCollegesList();
          collegeEntries.sort(Comparator.comparing(CollegeEntry::getSequence));
          for (CollegeEntry collegeEntry : collegeEntries) {
            List<FacultyEntry> facultyList = collegeEntry.getCollege().getFaculitiesList();
            for (FacultyEntry facultyEntry : facultyList) {
              if (facultyEntry.getSeats() != 0) {
                Integer value = facultyEntry.getSeats() - 1;
                facultyEntry.setSeats(value);
                facultyEntryRepository.save(facultyEntry);
                admissionEntry.setStatus(3);
                repo.save(admissionEntry);
                break;
              } else {
                admissionEntry.setStatus(4);
                repo.save(admissionEntry);
              }
            }
          }
        }

      } else {

      }
    }
  }
}
