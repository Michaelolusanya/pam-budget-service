package org.imc.pam.boilerplate.tools;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeneralJavaTools {

    public boolean existsInArray(Object obj, Object[] objArr) {
        for (Object index : objArr) {
            if (index.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> jsonObjToMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = jsonArrToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonObjToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public List<Object> jsonArrToList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);

            if (value instanceof JSONArray) {
                value = jsonArrToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = jsonObjToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
