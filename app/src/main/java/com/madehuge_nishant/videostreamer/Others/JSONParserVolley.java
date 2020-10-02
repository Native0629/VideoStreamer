package com.madehuge_nishant.videostreamer.Others;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONParserVolley
{
    static String json;
    static JSONObject jsonObject=null;

    public JSONParserVolley(String json) {
        this.json = json;
    }

    public JSONObject JSONParseVolley(){

        try{
            jsonObject = new JSONObject(json);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}