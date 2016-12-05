package com.example.jacksonmeyer.sidekick.services;

import android.util.Log;

import com.example.jacksonmeyer.sidekick.Constants;
import com.example.jacksonmeyer.sidekick.models.Doctor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DoctorService {

    private static final String TAG ="doctors Service";

    public static void findDoctors(String name, Callback callback) {


        OkHttpClient client = new OkHttpClient.Builder().build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.BETTER_DOCTOR_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(Constants.BETTER_DOCTOR_NAME_QUERY_PARAMETER, name);
        urlBuilder.addQueryParameter(Constants.API_QUERY_PARAMETER, Constants.APIKEY);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d("url", url);
        Call call = client.newCall(request);
        call.enqueue(callback);

    }

    public ArrayList<Doctor> processResults(Response response) {
        ArrayList<Doctor> doctors = new ArrayList<>();

        try {
            String jsonData = response.body().string();
            if (response.isSuccessful()) {
                JSONObject results = new JSONObject(jsonData);
                JSONArray doctorsResults = results.getJSONArray("data");
                for (int i = 0; i < doctorsResults.length(); i++) {
                    JSONObject doctor = doctorsResults.getJSONObject(i);
                    JSONArray practices = doctor.getJSONArray("practices");
                    for(int j = 0; j < practices.length(); j++) {
                        JSONObject addressJSON = practices.getJSONObject(j).getJSONObject("visit_address");
                        String address = "";
                        String city = addressJSON.getString("city");
                        String state = addressJSON.getString("state");
                        String street = addressJSON.getString("street");
                        String zip = addressJSON.getString("zip");
                        String Address = address + street + "\n" + city + "\n" + state + "\n" + zip;


                    JSONObject profile = doctor.getJSONObject("profile");
                    String firstName = profile.getString("first_name");
                    String lastName = profile.getString("last_name");
                    String imageUrl = profile.getString("image_url");
                    String bio = profile.getString("bio");
                    String gender = profile.getString("gender");
                        String Name = firstName + lastName;

                    Doctor doctorConstructor = new Doctor(Name, imageUrl, bio, gender, Address);
                    doctors.add(doctorConstructor);
                }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "objects: " + doctors);
        return doctors;
    }
}