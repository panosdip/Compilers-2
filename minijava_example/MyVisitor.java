import syntaxtree.*;
import visitor.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;



class MyVisitor extends GJDepthFirst<String, MyVisitor.ClassInfo>{

    Map<String, ClassInfo> symbolTable;

    Map<String, Integer> typeSizes;

    public MyVisitor() {
        symbolTable = new HashMap<>();
        typeSizes = new HashMap<>();

        typeSizes.put("int", 4);
        typeSizes.put("boolean", 1);
    }

    class Variable{
        String name;
        int size;

        public Variable(String name, int size){
            this.name = name;
            this.size = size;
        }
    }

    class Method{
        String name;
        int size;

        ArrayList<String> params;

        public Method(String name, int size){
            this.name = name;
            this.size = size;

            this.params = new ArrayList<>();
        }
    }

    class ClassInfo{
    String name;
    String parent;

    Map<String, Variable> fields;
    Map<String, Method> methods;

    int fieldOffset;
    int methodOffset;

    public ClassInfo(String className, String parentName, int fieldOffset, int methodOffset){
        this.name = className;
        this.parent = parentName;
        this.fieldOffset = fieldOffset;
        this.methodOffset = methodOffset;

        fields = new HashMap<>();
        methods = new HashMap<>();
    }
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

        
        ClassInfo info = new ClassInfo(classname, null, 0, 0);
        symbolTable.put(classname, info);

        n.f2.accept(this, argu);
        System.out.println("Variables: ");
        n.f3.accept(this, info);
        System.out.println("Methods: ");
        n.f4.accept(this, info);
        n.f5.accept(this, argu);

        System.out.println();

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

        ClassInfo info = new ClassInfo(classname, parentname, 0, 0);

        symbolTable.put(classname, info);

        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        System.out.println("Variables: ");
        n.f5.accept(this, info);
        System.out.println("Methods: ");
        n.f6.accept(this, info);
        n.f7.accept(this, argu);

        System.out.println();

        return null;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, ClassInfo info) throws Exception {
        String _ret=null;
        String type = n.f0.accept(this, null);
        String var = n.f1.accept(this, null);

        int varSize = typeSizes.getOrDefault(type, 8);

        String classname = info.name;
        int offset = info.fieldOffset;

        Variable varInfo = new Variable(var, varSize);


        System.out.println(classname + "." + varInfo.name + " " + offset);

        info.fieldOffset += varInfo.size;

        info.fields.put(var, varInfo);
        // super.visit(info);
        
        return _ret;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, ClassInfo info) throws Exception {
        String argumentList = n.f4.present() ? n.f4.accept(this, null) : "";

        String type = n.f1.accept(this, null);
        String name = n.f2.accept(this, null);

        int methodSize = typeSizes.getOrDefault("pointer", 8);

        String classname = info.name;
        int offset = info.methodOffset;

        Method methodInfo = new Method(name, methodSize);

        System.out.println(classname + "." + name + " " + offset);
        System.out.println(argumentList);

        info.methodOffset += methodSize;


        // super.visit(n, argu);
        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, ClassInfo argu) throws Exception {
        String ret = n.f0.accept(this, null);

        if (n.f1 != null) {
            ret += n.f1.accept(this, null);
        }

        return ret;
    }

    /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
    public String visit(FormalParameterTerm n, ClassInfo argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
    * f0 -> ( FormalParameterTerm() )*
    */
    @Override
    public String visit(FormalParameterTail n, ClassInfo argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, ClassInfo argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        return type + " " + name;
    }

    @Override
    public String visit(ArrayType n, ClassInfo argu) {
        return "int[]";
    }

    @Override
    public String visit(BooleanType n, ClassInfo argu) {
        return "boolean";
    }

    @Override
    public String visit(IntegerType n, ClassInfo argu) {
        return "int";
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    @Override
    public String visit(Identifier n, ClassInfo argu) {
        return n.f0.toString();
    }
}