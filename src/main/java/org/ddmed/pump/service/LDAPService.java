package org.ddmed.pump.service;

import org.ddmed.pump.domain.Pump;
import org.ddmed.pump.model.Device;
import org.ddmed.pump.model.Exporter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapAttributes;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.ldif.parser.LdifParser;
import org.springframework.ldap.support.LdapNameBuilder;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LDAPService {

    private static String USER_DN = "cn=admin,dc=dcm4che,dc=org";
    private static String PASSWORD = "secret";

    public static boolean addAETitle(Pump pump, Device device){

        return true;
    }
    public static boolean addExporter(Pump pump, Exporter exporter){

        LdapName dn = LdapNameBuilder.newInstance().add("dcmExporterID=" + exporter.getId() +",dicomDeviceName=dcm4chee-arc,cn=Devices,cn=DICOM Configuration,dc=dcm4che,dc=org").build();
        LdapAttributes record = new LdapAttributes();
        record.setName(dn);
        record.put("objectClass", "dcmExporter");
        record.put("dcmExporterID", exporter.getId());
        record.put("dcmURI", "dicom:" + exporter.getAETitleTo());
        record.put("dicomAETitle", pump.getDicomAETitle());
        record.put("dcmQueueName", exporter.getExportQueue());
        record.put("dcmSchedule", "hour=0-23 dayOfWeek=*");
        record.put("dicomDescription", exporter.getDescription());

        LdapName dnExportRule = LdapNameBuilder.newInstance().add("cn=Forward to "+ exporter.getAETitleTo() +",dicomDeviceName=dcm4chee-arc,cn=Devices,cn=DICOM Configuration,dc=dcm4che,dc=org").build();
        LdapAttributes recordExportRule = new LdapAttributes();
        recordExportRule.setName(dnExportRule);
        recordExportRule.put("objectClass","dcmExportRule");
        recordExportRule.put("cn","Forward to "+ exporter.getAETitleTo());
        recordExportRule.put("dcmEntity","Series");
        recordExportRule.put("dcmDuration","PT1M");
        recordExportRule.put("dcmExporterID",exporter.getId());


        System.out.println(record);
        System.out.println(recordExportRule);


        String URI = "ldap://" + pump.getDicomHostname()+":389/";
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(URI);
        contextSource.setUserDn(USER_DN);
        contextSource.setPassword(PASSWORD);
        contextSource.setPooled(false);
        contextSource.afterPropertiesSet();

        LdapTemplate template = new LdapTemplate(contextSource);

        try{
            template.bind(record.getName(), null, record);
            template.bind(recordExportRule.getName(), null, recordExportRule);
        }
        catch (NameAlreadyBoundException e){
            System.out.println( e.getLocalizedMessage());

            template.rebind(record.getName(), null, record);
            template.rebind(recordExportRule.getName(), null, recordExportRule);
            return false;
        }

        return true;
    }

}
