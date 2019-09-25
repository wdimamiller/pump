package org.ddmed.pump.service;


import org.ddmed.pump.domain.Pump;


import org.ddmed.pump.model.Study;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Service
public  class PumpRestService {


    private static RestTemplate restTemplate = new RestTemplate();

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
    public static List<Study> getAllStudies(Pump pump, String dateFrom, String dateTo){

        List<Study> studies = new ArrayList<Study>();

        String uri = pump.getRestBase() +
                "/aets/" + pump.getDicomAETitle() +
                "/rs/studies?StudyDate=" + dateFrom + "-" + dateTo;

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


            /*System.out.println();
            System.out.println("PatientName: " + study.getPatientName() + "\n");
            System.out.println("StudyID: " + study.getId() + "\n");
            System.out.println("Modality: " + study.getModality() + "\n");
            System.out.println("Institution: " + study.getInstitutionName() + "\n");
            System.out.println("SERIES: " + study.getCountSeries());
            System.out.println("Referrals: " + study.getReferrerName());
            System.out.println("PatientDOB: " + study.getPatientDOB());
            System.out.println("StudyDate: " + study.getStudyDate());
            System.out.println("PatGENDER: " + study.getPatientGender());*/


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


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<Object>(headers);

        ((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(2000);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,entity, String.class );
            HttpStatus statusCode = response.getStatusCode();


            if(statusCode.value()== 200){
                return true;
            }
            else{
                return false;
            }
        }


}
