import syntaxtree.*;
import visitor.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;


// This visitor checks only the classes.
class MyVisitor extends GJDepthFirst<String, ClassInfo>{

    Map<String, ClassInfo> symbolTable;

    private boolean hasCycle(String className){
        ClassInfo current = symbolTable.get(className);

        if(current == null){
            return false;
        }

        while(current.parent != null){
            if(current.parent.equals(className)){
                return true;
            }

            current = symbolTable.get(current.parent);

            if(current == null)
                return false;
        }

        return false;
    }

    public MyVisitor() {
        symbolTable = new HashMap<>();
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "String"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n, ClassInfo argu) throws Exception {
        String classname = n.f1.accept(this, null);
        // System.out.println("Class: " + classname);

        // ClassInfo info = new ClassInfo(classname, null, 0, 0);
        // symbolTable.put(classname, info);

        // super.visit(n, info);

        // System.out.println();

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, ClassInfo argu) throws Exception {
        n.f0.accept(this, argu);
        
        String classname = n.f1.accept(this, argu);
        System.out.println("Class: " + classname);

        
        ClassInfo info = new ClassInfo(classname, null);

        if(symbolTable.containsKey(classname)){
            throw new Exception("Duplicate class: " + classname);
        }
        
        symbolTable.put(classname, info);


        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, ClassInfo argu) throws Exception {
        n.f0.accept(this, argu);

        String classname = n.f1.accept(this, null);
        System.out.println("Class: " + classname);

        String parentname = n.f3.accept(this, null);

        if(!symbolTable.containsKey(parentname)){
            throw new Exception(
                "Class " + classname +
                " extends unknown class " + parentname
            );
        }

        ClassInfo info = new ClassInfo(classname, parentname);

        if(symbolTable.containsKey(classname)){
            throw new Exception("Duplicate class: " + classname);
        }

        if(hasCycle(classname)){
            throw new Exception("Cycle " + classname);
        }

        symbolTable.put(classname, info);


        return null;
    }

    @Override
    public String visit(Identifier n, ClassInfo argu) {
        return n.f0.toString();
    }
}