package org.ddmed.pump.model;

import lombok.Data;

@Data
public class Pump {

    private long id;
    private String name;
    private String dicomHostname;
    private String httpPort;
    private String dicomAETitle;
    private String dicomPort;
    private String httpProtocol;
    private String webUri;

    public String getRestBase(){
        return this.httpProtocol +
                "://" + this.dicomHostname +
                ":" + this.httpPort +
                "/" + this.webUri;
    }
}
