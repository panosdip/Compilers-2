
import java.util.ArrayList;

public class Method { 
    String name;
    String returnType;
    int size;

    ArrayList<Variable> params;

    public Method(String name, int size, String returnType){
        this.name = name;
        this.size = size;
        this.returnType = returnType;

        this.params = new ArrayList<>();
    }

}
