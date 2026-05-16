
import java.util.ArrayList;
import java.util.HashMap;

public class Method { 
    String name;
    String returnType;
    int size;

    ArrayList<Variable> params;
    HashMap<String, Variable> locals;
    
    public Method(String name, int size, String returnType){
        this.name = name;
        this.size = size;
        this.returnType = returnType;

        this.params = new ArrayList<>();
        this.locals = new HashMap<>();
    }

}
