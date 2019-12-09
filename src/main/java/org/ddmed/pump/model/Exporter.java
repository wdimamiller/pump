package org.ddmed.pump.model;

import lombok.Data;

import java.sql.Time;

@Data
public class Exporter {

    private String id;
    private String AETitleFrom;
    private String AETitleTo;
    private String description;
    private String exportQueue;
    private int exportPriority;
    private Time timeFrom;
    private Time timeTo;
    private int timeout;

}
