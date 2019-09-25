package org.ddmed.pump.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "PUMP")
public class Pump {

    @Id
    @GeneratedValue
    @Column(name = "PUMP_ID")
    private long id;
    @Column(name = "PUMP_NAME")
    private String name;
    @Column(name = "PUMP_DICOM_HOSTNAME")
    private String dicomHostname;
    @Column(name = "PUMP_HTTP_PORT")
    private String httpPort;
    @Column(name = "PUMP_AE_TITLE")
    private String dicomAETitle;
    @Column(name = "PUMP_DICOM_PORT")
    private String dicomPort;
    @Column(name = "PUMP_HTTP_PROTOCOL")
    private String httpProtocol;
    @Column(name = "PUMP_WEB_URI")
    private String webUri;
    @Column(name = "PUMP_DEFAULT")
    private boolean useByDefault;


    public String getRestBase(){
        return this.httpProtocol +
                "://" + this.dicomHostname +
                ":" + this.httpPort +
                "/" + this.webUri;
    }

    public Pump(){

    }
    public Pump(String name, String dicomHostname, String httpPort){
        this.setName(name);
        this.setDicomHostname(dicomHostname);
        this.setHttpPort(httpPort);
        this.setDicomAETitle("DCM4CHEE");
        this.setWebUri("dcm4chee-arc");
        this.setHttpProtocol("http");
        this.setUseByDefault(false);
    }
    public void copy(Pump pump){
        this.setId(pump.getId());
        this.setName(pump.getName());
        this.setDicomHostname(pump.getDicomHostname());
        this.setDicomAETitle(pump.getDicomAETitle());
        this.setDicomPort(pump.getDicomPort());
        this.setUseByDefault(pump.isUseByDefault());
        this.setWebUri(pump.getWebUri());
        this.setHttpPort(pump.getHttpPort());
        this.setHttpProtocol(pump.getHttpProtocol());
    }
}
