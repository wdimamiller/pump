package org.ddmed.pump.service;


import org.ddmed.pump.domain.Pump;


import org.ddmed.pump.model.Device;
import org.ddmed.pump.model.Study;
import org.json.JSONArray;
import org.json.JSONObject;


import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Service
public  class PumpRestService {


    private static RestTemplate restTemplate = new RestTemplate();

    public static List<Device> getAllDevices(Pump pump, boolean hideAdditional ){
        List<Device> devices = new ArrayList<Device>();
        String URI = pump.getRestBase() + "/aes";

        String result = restTemplate.getForObject(URI, String.class);
        if(result==null){
            return devices;
        }
        JSONArray arr = new JSONArray(result);

        int count = arr.length();
        for(int i = 0; i < count; i++) {
            Device device = new Device();

            String deviceName = arr.getJSONObject(i).getString("dicomDeviceName");
            String deviceAETitle = arr.getJSONObject(i).getString("dicomAETitle");

            if(hideAdditional && ( deviceAETitle.equals("AS_RECEIVED")
                                || deviceAETitle.equals("IOCM_EXPIRED")
                                || deviceAETitle.equals("IOCM_PAT_SAFETY")
                                || deviceAETitle.equals("IOCM_QUALITY")
                                || deviceAETitle.equals("IOCM_REGULAR_USE")
                                || deviceAETitle.equals("IOCM_WRONG_MWL")
                                || deviceAETitle.equals("SCHEDULEDSTATION"))){
                continue;
            }
            JSONArray arrNetworks = arr.getJSONObject(i).getJSONArray("dicomNetworkConnection");

            String deviceHostname = arrNetworks.getJSONObject(0).getString("dicomHostname");
            String deviceDicomPort = String.valueOf( arrNetworks.getJSONObject(0).getInt("dicomPort"));

            device.setName(deviceName);
            device.setAETitle(deviceAETitle);
            device.setHostname(deviceHostname);
            device.setDicomPort(deviceDicomPort);

            devices.add(device);
        }
        return devices;
    }

    public static int exportStudies(Pump pump, List<Study> studies, Device device){

        for (Study study:studies) {

            String URI ="http://"
                    + pump.getDicomHostname() + ":8080/"
                    + pump.getWebUri()
                    + "/aets/AS_RECEIVED/rs/studies/" + study.getId() + "/export/dicom:" + device.getAETitle() +"?";

            System.out.println(URI);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<JSONObject> entity = new HttpEntity<>(null , headers);

            try {
                ResponseEntity<String> answer = restTemplate.postForEntity(URI, entity, String.class);
                System.out.println(answer);
                return answer.getStatusCodeValue();

            } catch (HttpStatusCodeException e) {
                System.out.println("HERE 500 1");
                return 500;
            } catch (RuntimeException e) {
                System.out.println("HERE 500 2");
                return 500;
            }

        }


    return 0;
    }
    public static int addDevice(Pump pump, String deviceName, String deviceAETitle, String deviceHostname, String deviceDicomPort){

        /*TODO
        *  Make it work with JSON
        */
        /*
        JSONObject jsonDevice = new JSONObject();
        jsonDevice.put("dicomDeviceName", deviceName);
        jsonDevice.put("dicomVendorData", false);
        jsonDevice.put("dicomInstalled", true);
        JSONObject dicomNetworkConnection = new JSONObject();
        dicomNetworkConnection.put("cn", deviceName + "NETWORK");
        dicomNetworkConnection.put("dicomHostname", deviceHostname);
        dicomNetworkConnection.put("dicomPort",Integer.parseInt(deviceDicomPort));
        dicomNetworkConnection.put("dcmNetworkConnection", new JSONObject());
        JSONArray arrDicomNetworkConnection = new JSONArray();
        arrDicomNetworkConnection.put(dicomNetworkConnection);
        jsonDevice.put("dicomNetworkConnection", arrDicomNetworkConnection );
        JSONObject dicomNetworkAE = new JSONObject();
        dicomNetworkAE.put("dicomAETitle", deviceAETitle);
        dicomNetworkAE.put("dicomDescription", "Added by PUMP Web Interface");
        dicomNetworkAE.put("dicomAssociationInitiator",true);
        dicomNetworkAE.put("dicomAssociationAcceptor", true);
        JSONArray arrDicomNetworkConnectionReference = new JSONArray();
        arrDicomNetworkConnectionReference.put("/dicomNetworkConnection/0");
        dicomNetworkAE.put("dicomNetworkConnectionReference", arrDicomNetworkConnectionReference);
        dicomNetworkAE.put("dicomTransferCapability", new JSONArray());
        dicomNetworkAE.put("dcmNetworkAE", new JSONObject());
        jsonDevice.put("dicomNetworkAE", dicomNetworkAE );
        jsonDevice.put("dcmDevice", new JSONObject());
        String jsonParameter = jsonDevice.toString();
        */
        String parameter = "{\n" +
                "  \"dicomDeviceName\": \"" + deviceName + "\",\n" +
                "  \"dicomVendorData\": false,\n" +
                "  \"dicomInstalled\": true,\n" +
                "  \"dicomNetworkConnection\": [\n" +
                "    {\n" +
                "      \"cn\": \"" + deviceName  + "NETWORK\",\n" +
                "      \"dicomHostname\": \"192.168.2.2\",\n" +
                "      \"dicomPort\": " + deviceDicomPort +  ",\n" +
                "      \"dcmNetworkConnection\": {}\n" +
                "    }\n" +
                "  ],\n" +
                "  \"dicomNetworkAE\": [\n" +
                "    {\n" +
                "      \"dicomAETitle\": \"" + deviceAETitle + "\",\n" +
                "      \"dicomDescription\": \"Added by PUMP Web Interface\",\n" +
                "      \"dicomAssociationInitiator\": true,\n" +
                "      \"dicomAssociationAcceptor\": true,\n" +
                "      \"dicomNetworkConnectionReference\": [\n" +
                "        \"/dicomNetworkConnection/0\"\n" +
                "      ],\n" +
                "      \"dicomTransferCapability\": [],\n" +
                "      \"dcmNetworkAE\": {}\n" +
                "    }\n" +
                "  ],\n" +
                "  \"dcmDevice\": {}\n" +
                "}";


        String URI = pump.getRestBase() + "/devices/" + deviceName;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<String> entity = new HttpEntity<>(parameter , headers);

        try {
            ResponseEntity<String> answer = restTemplate.postForEntity(URI, entity, String.class);
            return answer.getStatusCodeValue();

        } catch (HttpStatusCodeException e) {
            return 500;
        } catch (RuntimeException e) {
            return 500;
        }

    }
    public static List<String> getModalities(Pump pump){
        List<String> modalities = new ArrayList<String>();

        String uri = pump.getRestBase() +  "/modalities";

        String result = restTemplate.getForObject(uri, String.class);
        JSONObject jsonMod = new JSONObject(result);

        JSONArray arr = jsonMod.getJSONArray("Modalities");
        for(int i = 0; i < arr.length(); i++){
             modalities.add(arr.getString(i));
        }

        return modalities;

    }
    public static List<Study> getAllStudies(Pump pump, String dateFrom, String dateTo, String searchName, String searchDOB, String searchPatientID, List<String> searchModalities){

        String  modalitiesInStudy = "";
        if(searchModalities!=null){
            for (String mod: searchModalities) {
                modalitiesInStudy = modalitiesInStudy + "," + mod;
            }
        }
        List<Study> studies = new ArrayList<Study>();


        String uri = pump.getRestBase() +
                "/aets/" + pump.getDicomAETitle() +
                "/rs/studies?" +
                "StudyDate=" + dateFrom + "-" + dateTo +
                "&PatientID=" + searchPatientID +
                "&PatientName=" + searchName + "*" +
                "&ModalitiesInStudy=" + modalitiesInStudy +
                "&PatientBirthDate=" + searchDOB;

        String result = restTemplate.getForObject(uri, String.class);
        if(result==null){
            return studies;
        }
        JSONArray arr = new JSONArray(result);


        int count = arr.length();
        for(int i = 0; i < count; i++)
        {
            Study study = new Study();

            //StudyID
            String studyID = arr.getJSONObject(i)
                    .getJSONObject("0020000D")
                    .getJSONArray("Value").getString(0);
            study.setId(studyID);

            //PatientName
            String patientName = arr.getJSONObject(i)
                    .getJSONObject("00100010")
                    .getJSONArray("Value").getJSONObject(0).getString("Alphabetic");

            patientName = patientName.replace('^',' ');
            patientName = patientName.replace(',',' ');
            study.setPatientName(patientName);

            //Refferals
            String refferalName = arr.getJSONObject(i)
                    .getJSONObject("00080090")
                    .getJSONArray("Value").getJSONObject(0).getString("Alphabetic");

            refferalName = refferalName.replace('^',' ');
            refferalName = refferalName.replace(',',' ');
            study.setReferrerName(refferalName);


            //Modality
            String modality = arr.getJSONObject(i)
                    .getJSONObject("00080061")
                    .getJSONArray("Value").getString(0);
            study.setModality(modality);

            //PatientDOB
            String patientDOB = arr.getJSONObject(i)
                    .getJSONObject("00100030")
                    .getJSONArray("Value").getString(0);
            study.setPatientDOB(patientDOB);

            //StudyDate
            String studyDate = arr.getJSONObject(i)
                    .getJSONObject("00080020")
                    .getJSONArray("Value").getString(0);
            study.setStudyDate(studyDate);

            //PatientID
            String patientID = arr.getJSONObject(i)
                    .getJSONObject("00100020")
                    .getJSONArray("Value").getString(0);
            study.setPatientID(patientID);

            //PatientGENDER
            String patientSex = arr.getJSONObject(i)
                    .getJSONObject("00100040")
                    .getJSONArray("Value").getString(0);
            study.setPatientGender(patientSex);



            //GET ANOTHER REQUEST FOR METADATA
            String uriStudy = pump.getRestBase() +
                    "/aets/" + pump.getDicomAETitle() +
                    "/rs/studies/" + studyID + "/metadata";
            //System.out.println(uriStudy);
            String resultMetadata = restTemplate.getForObject(uriStudy, String.class);
            int countSeries = 0;
            JSONArray series = new JSONArray(resultMetadata);
            countSeries = series.length();
            //System.out.println("SERIES: " + countSeries);
            for(int j = 0; j < countSeries; j++)
            {
                if(series.getJSONObject(j).has("00080080")){
                    String institutionName = series.getJSONObject(j)
                            .getJSONObject("00080080")
                            .getJSONArray("Value").getString(0);
                    study.setInstitutionName(institutionName);
                }

            }

            //COUNT Series
            study.setCountSeries(countSeries);

            studies.add(study);
        }


        return studies;

    }
    public static boolean serverListening(String host, int port)
    {
        Socket s = null;
        try
        {
            s = new Socket(host, port);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            if(s != null)
                try {s.close();}
                catch(Exception e){}
        }
    }



    public static boolean isWorked (Pump pump){

        final String uri = pump.getRestBase() +
                "/ctrl/status";


        if(pump == null){
            System.out.println("IT IS NULL");
            return false;
        }
        else {

            RestTemplate restTemplate = new RestTemplate();


            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
            SimpleClientHttpRequestFactory rf = (SimpleClientHttpRequestFactory) restTemplate
                    .getRequestFactory();
            rf.setReadTimeout(2000);
            rf.setConnectTimeout(2000);

            System.out.println(uri);
            String result;
            try {
                String httpResult = restTemplate.getForObject(uri,
                        String.class);
                System.out.println(httpResult);
            } catch (HttpStatusCodeException e) {
                System.out.println("HTTP ERROR");
                return false;
            } catch (RuntimeException e) {
                System.out.println("RUNTIME ERROR");
                return false;
            }
        }
        return true;
    }

}
