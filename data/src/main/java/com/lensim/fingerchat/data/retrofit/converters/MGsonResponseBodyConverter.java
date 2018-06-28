package com.lensim.fingerchat.data.retrofit.converters;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Converter;

public class MGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    MGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            JSONObject object = new JSONObject(value.string());
            //{"GetEmpSignInResult":"{\"retCode\":2,\"retMsg\":\"查询成功但无满足条件的结果数据\",\"retData\":null}"}
            ByteArrayInputStream inputStream;
            if (object.toString().contains("\"{\\\"retCode")) {
                String[] split = object.toString().split("\"");
                String string = split[1];
                Log.e("string","" + string);
                String results = object.getString(string);
                Log.e("results","" + results);
                inputStream = new ByteArrayInputStream(results.getBytes());
            } else {
                inputStream = new ByteArrayInputStream(object.toString().getBytes());
            }
            //字节流转换成字符流
            Reader reader = new InputStreamReader(inputStream);

            JsonReader jsonReader = gson.newJsonReader(reader);
            //如果你不能轻易找出json格式错误的位置，你也可以设置GSON解析模式为lenient模式:
            jsonReader.setLenient(true);
            return adapter.read(jsonReader);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            value.close();
        }
        return null;

    }
}