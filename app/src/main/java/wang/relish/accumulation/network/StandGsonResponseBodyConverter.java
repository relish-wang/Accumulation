package wang.relish.accumulation.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by xf on 2016/9/27
 */

class StandGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final String TAG = "FC_Stand";

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    StandGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String data = value.string();
        JsonReader jsonReader = gson.newJsonReader(new StringReader(data));
        try {
            return adapter.read(jsonReader);
        } catch (Exception e) {
            Log.w(TAG,e.getMessage(),e);
            return handleDataTypeNotMatchCase(data);
        } finally {
            value.close();
        }
    }

    private T handleDataTypeNotMatchCase(String response) throws IOException {
        try {
            JSONObject object = new JSONObject(response);
            object.remove("data");
            response = object.toString();
        } catch (Exception e) {
            Log.w(TAG,e.getMessage(),e);
        }

        JsonReader jsonReader = gson.newJsonReader(new StringReader(response));
        return adapter.read(jsonReader);
    }
}
