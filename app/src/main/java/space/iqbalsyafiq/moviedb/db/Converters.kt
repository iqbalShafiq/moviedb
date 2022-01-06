package space.iqbalsyafiq.moviedb.db

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun intToJson(value: List<Int>?) = Gson().toJson(value)!!

    @TypeConverter
    fun jsonToInt(value: String) = Gson().fromJson(value, Array<Int>::class.java).toList()

    @TypeConverter
    fun stringToJson(value: List<String>?) = Gson().toJson(value)!!

    @TypeConverter
    fun jsonToString(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
}