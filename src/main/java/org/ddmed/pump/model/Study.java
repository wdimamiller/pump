package org.ddmed.pump.model;

import lombok.Data;


import java.util.Date;

@Data
public class Study {

    private String id;
    private String studyDate;
    private String patientName;

    private int countSeries;
    private String institutionName;

    private String patientDOB;
    private String patientGender;

    private String modality;
    private String procedure;
    private String patientID;
    private String referrerName;





}
