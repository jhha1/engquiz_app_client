package kr.jhha.engquiz.data.remote;

import java.util.HashMap;
import java.util.Map;

import kr.jhha.engquiz.util.JsonHelper;
import kr.jhha.engquiz.util.exception.system.MyIllegalArgumentException;

/**
 * Created by thyone on 2017-04-07.
 */

public class ObjectBundle {
    private Map<String, Object> objectMap = new HashMap<>();

    public ObjectBundle(){}
    public ObjectBundle(String json){
        this.objectMap = JsonHelper.json2map(json);
    }

    public Integer getInt(String key){
        if( ! objectMap.containsKey(key) ) {
            throw new MyIllegalArgumentException("No Exist Key:" + key);
        }

        Object value = this.objectMap.get(key);
        if( !(value instanceof Integer) ){
            throw new MyIllegalArgumentException("Can't Convert Value to Integer:"+value.getClass().getComponentType().getName());
        }
        return (Integer)this.objectMap.get(key);
    }
    public String getString(String key){
        if( ! objectMap.containsKey(key) ) {
            throw new MyIllegalArgumentException("No Exist Key:" + key);
        }

        Object value = this.objectMap.get(key);
        if( !(value instanceof String) ){
            throw new MyIllegalArgumentException("Can't Convert Value to String:"+value.getClass().getComponentType().getName());
        }
        return (String)this.objectMap.get(key);
    }
    public ObjectBundle getObjectBundle(String key){
        if( ! objectMap.containsKey(key) ) {
            throw new MyIllegalArgumentException("No Exist Key:" + key);
        }

        Object value = this.objectMap.get(key);
        if( !(value instanceof ObjectBundle) ){
            throw new MyIllegalArgumentException("Can't Convert Value to ObjectBundle:"+value.getClass().getComponentType().getName());
        }
        return (ObjectBundle)this.objectMap.get(key);
    }

    public void setInt(String key, Integer value){
        this.objectMap.put(key, value);
    }
    public void setString(String key, String value){
        this.objectMap.put(key, value);
    }
    public void setObjectBundle(String key, ObjectBundle value){
        this.objectMap.put(key, value);
    }
}
