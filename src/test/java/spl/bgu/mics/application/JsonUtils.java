package spl.bgu.mics.application;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static Map<String, List<StampedDetectedObjects>> parseCameraData(String filePath) throws Exception {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType();
            return new Gson().fromJson(reader, type);
        }
    }
}
