
import syntaxtree.*;
import visitor.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;

class Visitor3 extends GJDepthFirst<String, ClassInfo>{

    Map<String, ClassInfo> symbolTable;

    public Visitor3(Map<String, ClassInfo> symbolTable){
        this.symbolTable = symbolTable;
    }

    boolean lookVar(name, className){
        if(symbolTable.containsKey(className)){
            ClassInfo info = symbolTable.get(className);

            info.
        }

        return false;
    }

    @Override
    public String visit(IntegerLiteral n, ClassInfo argu){
        return "int";
    }

    @Override
    public String visit(TrueLiteral n, ClassInfo argu){
        return "true";
    }

    public String visit(FalseLiteral n, ClassInfo argu){
        return "false";
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    @Override
    public String visit(Identifier n, ClassInfo argu) {
        String id = n.f0.toString();

        if(symbolTable.containsKey(id))

        return n.f0.toString();
    }
}
