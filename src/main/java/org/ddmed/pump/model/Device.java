package org.ddmed.pump.model;

import lombok.Data;

@Data
public class Device {

    private String name;
    private String AETitle;
    private String hostname;
    private String dicomPort;

}
