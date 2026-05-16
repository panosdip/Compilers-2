
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import syntaxtree.*;
import visitor.*;



class Visitor2 extends GJDepthFirst<String, ClassInfo>{

    Map<String, ClassInfo> symbolTable;

    Map<String, Integer> typeSizes;

    private boolean isSubType(String child, String parent){
        if(child.equals(parent)){
            return true;
        }

        ClassInfo info = symbolTable.get(child);

        while(info != null && info.parent != null){
            if(info.parent.equals(parent)){
                return true;
            }

            info = symbolTable.get(info.parent);
        }

        return false;
    }

    private boolean checkParamsOverload(ArrayList<Variable> list1, ArrayList<Variable> list2){
        int size = list1.size();

        for(int i = 0; i < size; i++){
            Variable var1 = list1.get(i);
            Variable var2 = list2.get(i);
            
            // They differ so overloading is allowed.
            if(!isSubType(var1.type, var2.type)){
                return true;
            }
            
        }

        return false;

    }

    private boolean typeAcceptable(String type){
        if(type.equals("int") || type.equals("int[]") || type.equals("boolean")){
            return true;
        }

        if(symbolTable != null){
            if(symbolTable.containsKey(type)){
                return true;
            }
        }

        return false;
    }

    private Method findMethodParent(ClassInfo info, Method method){
        String parent = info.parent;
        
        // Do this for all the parents e.g.(A extends B, B extends C, search B and then A).
        while(parent != null){
            ClassInfo p = symbolTable.get(parent);

            // Check for all possible methods that are saved with the same name.
            if(p != null && p.methods.containsKey(method.name)){
                for(Method candidate : p.methods.get(method.name)){
                    
                    // First check the params.
                    if(candidate.params.size() != method.params.size()){
                        continue;
                    }

                    boolean theyMatch = true;

                    for(int i = 0; i < candidate.params.size(); i++){
                        if(!candidate.params.get(i).type.equals(method.params.get(i).type)){
                            theyMatch = false;

                            break;
                        }
                    }

                    if(theyMatch){
                        return candidate;
                    }
                }
            }

            parent = p.parent;
        }

        return null;
    }

    public Visitor2(Map<String, ClassInfo> symbolTable){
        this.symbolTable = symbolTable;
        typeSizes = new HashMap<>();

        typeSizes.put("int", 4);
        typeSizes.put("boolean", 1);
        typeSizes.put("int[]", 8);
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

        ClassInfo info = new ClassInfo(classname, null);
        symbolTable.put(classname, info);

        Method mainMethod = new Method("main", 8, "void");

        info.methods.computeIfAbsent("main", k -> new ArrayList<>()).add(mainMethod);

        String paramName = n.f11.accept(this, null);
        Variable param = new Variable(paramName, "String[]", 8);
    
        mainMethod.params.add(param);

        // Collect the local variables of the method.
        ArrayList<String> localDecls = new ArrayList<>();
        
        for(Node node : n.f14.nodes){
            String decl = node.accept(this, null);
        
            if(decl != null && !decl.isEmpty()){
                localDecls.add(decl);
            }
        }

        HashSet<String> paramNames = new HashSet<>();


        for(String local : localDecls){

            String[] parts = local.trim().split("\\s+");

            if(parts.length == 2){

                String localType = parts[0];
                String localName = parts[1];

                int size = typeSizes.getOrDefault(localType, 8);

                if(paramNames.contains(localName)){
                    throw new Exception(
                        "Local: " + localName +
                        " already exists in parameters of " + mainMethod.name
                    );
                }

                if(mainMethod.locals.containsKey(localName)){
                    throw new Exception(
                        "Duplicate local: " + localName +
                        " already exists in method " + mainMethod.name
                    );
                }

                Variable localVar = new Variable(localName, localType, size);

                mainMethod.locals.put(localVar.name, localVar);
            }
        }




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
    // @Override
    public String visit(ClassDeclaration n, ClassInfo argu) throws Exception {
        n.f0.accept(this, argu);
        
        String classname = n.f1.accept(this, argu);
        System.out.println("Class: " + classname);

        
        // ClassInfo info = new ClassInfo(classname, null);
        ClassInfo info = symbolTable.get(classname);

        info.methodOffset = 0;
        info.fieldOffset = 0;

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

        ClassInfo parentInfo = symbolTable.get(parentname);

        if(parentInfo == null){
            throw new Exception("Parent class " + parentname + " not found");
        }

        ClassInfo info = symbolTable.get(classname);

        info.fieldOffset = parentInfo.fieldOffset;
        info.methodOffset = parentInfo.methodOffset;

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
        // String _ret=null;
        String type = n.f0.accept(this, null);
        String var = n.f1.accept(this, null);

        int varSize = typeSizes.getOrDefault(type, 8);

        // Check if var is local of a method, no need to print it.
        if(info == null){
            return type + " " + var;
        }

        // Get variable info.
        String classname = info.name;
        int offset = info.fieldOffset;

        // Duplicate variable.
        if(info.fields.containsKey(var)){
            throw new Exception(
                "Duplicate field " + var +
                " in class " + info.name
            );
        }

        // Create variable's info.
        Variable varInfo = new Variable(var, type, varSize);
        info.fieldOffset += varInfo.size;

        info.fields.put(var, varInfo);

        // Need to print this somewhere else because it prints method's locals.
        System.out.println(classname + "." + varInfo.name + ": " + offset);
        
        // super.visit(info);
        
        return type + " " + var;
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

        String type = n.f1.accept(this, null);
        String name = n.f2.accept(this, null);

        int methodSize = typeSizes.getOrDefault("pointer", 8);
        
        // Get info of class.
        String classname = info.name;
        int offset = info.methodOffset;

        // Create method.
        Method methodInfo = new Method(name, methodSize, type);

        // Get arguments in String format.
        String argumentList = n.f4.present() ? n.f4.accept(this, info) : "";

        // Split the arguments list into <type> <name>, create variable and push it to the list.
        String[] declarations = argumentList.split("\\s*,\\s*");

        // Create a set with the given parameters names.
        HashSet<String> paramNames = new HashSet<>();
        
        // Create the args list for method.
        for(String decl : declarations){
            String[] parts = decl.trim().split("\\s+");
            
            if(parts.length == 2){
                String argType = parts[0];
                String argName = parts[1];
                int size = typeSizes.getOrDefault(argType, 8);
                
                
                // Check if the current arg name is already given.
                if(paramNames.contains(argName)){
                    throw new Exception(
                        "Duplicate param: " + argName +
                        " already exists in method " + methodInfo.name
                    );
                }

                // add parameteres name to the set.
                paramNames.add(argName);
                
                // Create parameter and add it to parameters list.
                Variable param = new Variable(argName, argType, size);
                
                methodInfo.params.add(param); 
            }
        }

        // Check Overloading.
        if(info.methods.containsKey(name)){
            ArrayList<Method> existingList = info.methods.get(name);

            if(existingList != null){
                for(Method existing : existingList){
                    if(existing.params.size() == methodInfo.params.size()){
                        if(!checkParamsOverload(existing.params, methodInfo.params)){
                            throw new Exception("Not overloading for function: " + name + " allowed.");
                        }

                    }
                }
            }
        }


        // Collect the local variables of the method.
        ArrayList<String> localDecls = new ArrayList<>();
        
        for(Node node : n.f7.nodes){
            String decl = node.accept(this, null);
        
            if(decl != null && !decl.isEmpty()){
                localDecls.add(decl);
            }
        }

        for(String local : localDecls){

            String[] parts = local.trim().split("\\s+");

            if(parts.length == 2){

                String localType = parts[0];
                String localName = parts[1];

                int size = typeSizes.getOrDefault(localType, 8);

                if(paramNames.contains(localName)){
                    throw new Exception(
                        "Local: " + localName +
                        " already exists in parameters of " + methodInfo.name
                    );
                }

                if(methodInfo.locals.containsKey(localName)){
                    throw new Exception(
                        "Duplicate local: " + localName +
                        " already exists in method " + methodInfo.name
                    );
                }

                Variable localVar = new Variable(localName, localType, size);

                methodInfo.locals.put(localVar.name, localVar);
            }
        }


        // Check if method can be overriden.
        Method inherited = findMethodParent(info, methodInfo);

        if(inherited != null){

            if(!inherited.returnType.equals(type)){
                throw new Exception("Invalid override for method RETURN TYPE " + methodInfo.name);
            }

            if(inherited.params.size() != methodInfo.params.size()){ 
                throw new Exception("Invalid override for method PARAMS SIZE " + name);
            }

            for(int i=0; i<inherited.params.size(); i++){

                String p1 = inherited.params.get(i).type;
                String p2 = methodInfo.params.get(i).type;

                if(!p1.equals(p2)){
                    throw new Exception("Invalid override for method PARAM TYPE DIFF " + name);
                }
            }
            
        }
             
        System.out.println(classname + "." + methodInfo.name + ": " + offset);

        info.methodOffset += methodSize;
        info.methods.computeIfAbsent(name, k -> new ArrayList<>()).add(methodInfo);



        // super.visit(n, argu);
        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, ClassInfo info) throws Exception {
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
    public String visit(Type n, ClassInfo argu) throws Exception{
        String type = n.f0.accept(this, null);

        if(!typeAcceptable(type)){
            throw new Exception("Not known type: " + type);
        }

        return type;
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

