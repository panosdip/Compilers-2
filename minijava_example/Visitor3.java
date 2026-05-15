
import syntaxtree.*;
import visitor.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;

class Visitor3 extends GJDepthFirst<String, CurrentInfo>{

    Map<String, ClassInfo> symbolTable;

    public Visitor3(Map<String, ClassInfo> symbolTable){
        this.symbolTable = symbolTable;
    }

    private Variable getParam(String paramName, Method method){

        ArrayList<Variable> list = method.params;

        if(list == null){
            return null;
        }

        for(Variable Param : list){
            if(Param.name.equals(paramName)){
                return Param;
            }
        }

        return null;
    }

    // Function that checks if the variable <name> is inherited from another class.
    private Variable checkVarInherited(String name, ClassInfo _class){
        ClassInfo parentClass = symbolTable.get(_class.parent);

        while(parentClass != null){
            if(parentClass.fields.containsKey(name)){
                return parentClass.fields.get(name);
            }

            parentClass = symbolTable.get(parentClass.parent);
        }

        return null;
    }

    // boolean lookVar(name, className){
    //     if(symbolTable.containsKey(className)){
    //         ClassInfo info = symbolTable.get(className);

    //         info.
    //     }

    //     return false;
    // }

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
    // @Override
    // public String visit(MainClass n, CurrentInfo argu) throws Exception {

    //     String classname = n.f1.accept(this, argu);
    //     // System.out.println("Class: " + classname);

    //     // argu.currentClass = symbolTable.get(classname);


    //     // ClassInfo info = new ClassInfo(classname, null, 0, 0);
    //     // symbolTable.put(classname, info);

    //     // super.visit(n, info);

    //     // System.out.println();

    //     return null;
    // }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    // @Override
    public String visit(ClassDeclaration n, CurrentInfo argu) throws Exception {
        n.f0.accept(this, argu);
            
        String classname = n.f1.accept(this, argu);

        
        CurrentInfo info = new CurrentInfo(symbolTable.get(classname), null);
        
        n.f2.accept(this, info);
        n.f3.accept(this, info);
        n.f4.accept(this, info);
        n.f5.accept(this, info);

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
    public String visit(ClassExtendsDeclaration n, CurrentInfo argu) throws Exception {
        n.f0.accept(this, argu);

        String classname = n.f1.accept(this, null);
        String parentname = n.f3.accept(this, null);

        ClassInfo parentInfo = symbolTable.get(parentname);

        CurrentInfo info = new CurrentInfo(symbolTable.get(classname), null);

        n.f2.accept(this, info);
        n.f3.accept(this, info);
        n.f4.accept(this, info);
        n.f5.accept(this, info);
        n.f6.accept(this, info);
        n.f7.accept(this, info);

        return null;
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
    public String visit(MethodDeclaration n, CurrentInfo info) throws Exception {

        String name = n.f2.accept(this, null);

        Method method = info.currentClass.methods.get(name);

        info.currentMethod = method;

        String exp = n.f10.accept(this, info);

        // super.visit(n, argu);
        return null;
    }

    // f0 -> PrimaryExpression 
    // f1 -> &&
    // f2 -> PrimaryExpression 

    @Override
    public String visit(AndExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("boolean") || !rightExp.equals("boolean")){
            throw new Exception("Operator && needs type of boolean.");
        }

        return "boolean";
    }

    @Override
    public String visit(CompareExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("int") || !rightExp.equals("int")){
            throw new Exception("Operator < needs type of int.");
        }

        return "int";
    }

    @Override
    public String visit(PlusExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("int") || !rightExp.equals("int")){
            throw new Exception("Operator + needs type of int.");
        }

        return "int";
    }

    @Override
    public String visit(MinusExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("int") || !rightExp.equals("int")){
            throw new Exception("Operator - needs type of int.");
        }

        return "int";
    }

    @Override
    public String visit(TimesExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("int") || !rightExp.equals("int")){
            throw new Exception("Operator * needs type of int.");
        }

        return "int";
    }

    /*
        f0 -> PrimaryExpression 
        f1 -> "[" 
        f2 -> PrimaryExpression 
        f3 -> "]"
    */
    @Override
    public String visit(ArrayLookup n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("int[]") || !rightExp.equals("int")){
            throw new Exception("Operator [] needs type of int[] and int.");
        }

        return "int";
    }

    /*
        f0 -> PrimaryExpression 
        f1 -> "." 
        f2 -> "length"
    */

    @Override
    public String visit(ArrayLength n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);

        System.out.println(leftExp + "." + "length");

        if(!leftExp.equals("int[]")){
            throw new Exception("Operator .length needs type of int[].");
        }

        return "int";
    }

    /*
        f0 -> PrimaryExpression 
        f1 -> "." 
        f2 -> Identifier
        f3 -> "("
        f4 -> ( ExpressionList )? 
        f5 -> ")"

    */

    @Override
    public String visit(MessageSend n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);

        // If leftExp is Identifier then check if the method exists in that class or if it is inherited.
        // and check if MessageSend is legal.
        if(leftExp.equals("int") || leftExp.equals("int[]") || leftExp.equals("boolean")){
            String className = leftExp;

            ClassInfo info = symbolTable.get(className);

            String methodCalled = n.f2.accept(this, argu);

            if(info.methods != null){
                // NEED TO CHECK IF THE METHOD IS INHERITED AS WELL.
                if(!info.methods.containsKey(methodCalled)){
                    throw new Exception("Method " + methodCalled + "does not exist in class " + className);
                }

                // Check if the parameters passed are legal.

            }


        }
        else{
            throw new Exception("Operator .method needs type of class.");
        }

        return "int";
    }

    @Override
    public String visit(IntegerLiteral n, CurrentInfo argu){
        return "int";
    }

    @Override
    public String visit(TrueLiteral n, CurrentInfo argu){
        return "boolean";
    }

    public String visit(FalseLiteral n, CurrentInfo argu){
        return "boolean";
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    @Override
    public String visit(Identifier n, CurrentInfo argu){
        // Find the identifier starting from locals, parameters, fields and last inherited
        // if it exists return the type of the identifier.

        String name = n.f0.toString();

        if(argu == null){
            return name;
        }

        if(argu.currentMethod != null){


            Variable v = argu.currentMethod.locals.get(name);

            if (v != null) return v.type;

            v = getParam(name, argu.currentMethod);
            if (v != null) return v.type;
        }

        if(argu.currentClass != null){
            Variable v = argu.currentClass.fields.get(name);
            if (v != null) return v.type;
        }

        Variable v = checkVarInherited(name, argu.currentClass);

        if (v != null) return v.type;

        return name;
    }
}
