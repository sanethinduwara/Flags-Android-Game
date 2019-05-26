package com.saneth.flags;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FlagManager {


    private final static String FILE_NAME = "countries.json";
    private Context context;

    public FlagManager(Context context){
        this.context = context;
    }

    public JSONArray readFromJSON(){        //reading the jSON file

        JSONArray jsonArray=null;

        try {
            InputStream inputStream =context.getResources().getAssets().open(FILE_NAME);
            int size = inputStream.available();
            byte[] data = new byte[size];
            inputStream.read(data);
            inputStream.close();
            String json = new String(data, "UTF-8");
            jsonArray = new JSONArray(json);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public ArrayList<String> getFlags(){        //returns iso codes as a list

        JSONArray jsonArray = readFromJSON();

        ArrayList<String> flagList=new ArrayList<String>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    flagList.add(jsonArray.getJSONObject(i).getString("iso"));      //getting the iso code of the country and adding it to an arraylist
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return flagList;
    }

    public ArrayList<String> getCountries() {      //returns country names as a list

        JSONArray jsonArray = readFromJSON();

        ArrayList<String> cList=new ArrayList<String>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    cList.add(jsonArray.getJSONObject(i).getString("name"));        //getting the name of the country and adding it to an arraylist
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return cList;
    }

    public String getCorrectAnswer(String iso){        //returns the correct country name of a given iso code

        String correct_answer = "";
        int i =0;
        boolean found = false;
        JSONArray jsonArray = readFromJSON();

        if (jsonArray != null) {
            while (!found){
                try {
                    if(jsonArray.getJSONObject(i).getString("iso").equals(iso)){
                        found = true;
                        correct_answer = jsonArray.getJSONObject(i).getString("name").toUpperCase();        //if the given iso matches with a json objects iso code, then store name of that json object as the correct name

                    }else {i++;}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return correct_answer;
    }

}
