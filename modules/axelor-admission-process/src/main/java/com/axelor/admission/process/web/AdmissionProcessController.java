package com.axelor.admission.process.web;

import com.axelor.admission.db.AdmissionProcess;
import com.axelor.admission.process.service.AdmissionProcessImp;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class AdmissionProcessController {
	@Inject
	AdmissionProcessImp service;

	@Transactional
	public void admissionProccess(ActionRequest request, ActionResponse response) {

		AdmissionProcess admissionProcess = request.getContext().asType(AdmissionProcess.class);
		admissionProcess = service.admissionSeatsManage(admissionProcess);
		response.setValues(admissionProcess);
	}
}
