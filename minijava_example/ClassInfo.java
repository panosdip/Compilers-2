import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class ClassInfo {
    String name;
    String parent;

    Map<String, Variable> fields;
    Map<String, ArrayList<Method>> methods;

    int fieldOffset;
    int methodOffset;

    public Method getMethod(String methodName){
        ArrayList<Method> list = methods.get(methodName);

        return list.get(0);
    }


    public ClassInfo(String className, String parentName){
        this.name = className;
        this.parent = parentName;

        fields = new HashMap<>();
        methods = new HashMap<>();
    }
}
