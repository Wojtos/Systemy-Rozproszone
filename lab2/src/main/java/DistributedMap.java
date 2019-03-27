import java.util.HashMap;
import java.util.Map;

public class DistributedMap implements SimpleStringMap {
    private Map<String, Integer> map = new HashMap<String, Integer>();

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public Integer get(String key) {
        return map.get(key);
    }

    public void put(String key, Integer value) {
        map.put(key, value);
    }

    public Integer remove(String key) {
        return map.remove(key);
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public void setMap(Map<String, Integer> map) {
        this.map = map;
    }
}
