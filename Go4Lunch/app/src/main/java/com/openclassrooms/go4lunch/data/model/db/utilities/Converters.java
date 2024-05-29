package com.openclassrooms.go4lunch.data.model.db.utilities;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class Converters for String in Place entity
 */
public class Converters {

    @TypeConverter
    public List<String> toList(String string) {
        if(string != null) {
            return new ArrayList<>(Arrays.asList(string.split(";")));
        }
        return null;
    }

    @TypeConverter
    public String fromList(List<String> list) {
        StringBuilder string = new StringBuilder();
        if(list != null) {
            if(list.size() == 1 ) {
                string = new StringBuilder(list.get(0));
            } else {
                for(String s : list) {
                    string.append(s).append(";");
                }
            }
            return string.toString();
        }
        return null;
    }
}
