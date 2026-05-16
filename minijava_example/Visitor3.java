
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

    // Find the identifier starting from locals, parameters, fields and last inherited
    // if it exists return the type of the identifier.
    private String getType(String name, CurrentInfo argu) throws Exception{
        
        if(name.equals("int") || name.equals("boolean") || name.equals("int[]")){
            return name;
        }

        if(argu == null){
            return name;
        }

        // Class names
        if(symbolTable.containsKey(name)){
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

        throw new Exception("Variable " + name + " is not declared");
    }

    private boolean checkParamsPassed(String params, CurrentInfo argu) throws Exception{
        String[] declarations;

        if(params == null || params.trim().isEmpty()){
            declarations = new String[0];
        }
        else{
            declarations = params.split("\\s*,\\s*");
        }        

        ArrayList<Variable> methodParams = argu.currentMethod.params;

        if(declarations.length > methodParams.size()){
            int size = declarations.length - methodParams.size();
            throw new Exception("Passed " + size + " extra params to method " + argu.currentMethod.name);
        }
        else if(declarations.length < methodParams.size()){
            int size = methodParams.size() - declarations.length;
            throw new Exception("Passed " + size + " fewer params to method " + argu.currentMethod.name);
        }

        int pos = 0;
        for(String decl : declarations){
            Variable current_param = methodParams.get(pos);

            String paramType = getType(decl, argu);
            System.out.println(paramType);
            String methodParamType = current_param.type;

            if(!isSubType(paramType, methodParamType)){
                return false;
            }

            pos++;
        }

        return true;
    }

    private Variable getParam(String paramName, Method method){

        ArrayList<Variable> list = method.params;

        if(list == null){
            return null;
        }

        for(Variable Param : list){
            System.out.println(Param.name);
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

    // Function that checks if the method <methodName> is inherited from another class.
    private Method checkMethodInherited(String methodName, ClassInfo _class){
        ClassInfo parentClass = symbolTable.get(_class.parent);

        while(parentClass != null){
            if(parentClass.methods.containsKey(methodName)){
                return parentClass.getMethod(methodName);
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

    // NEED TO DO THIS ALSO FOR MAIN.
    @Override
    public String visit(MainClass n, CurrentInfo argu) throws Exception {

        String className = n.f1.accept(this, null);

        ClassInfo currentClass = symbolTable.get(className);

        if(currentClass == null){
            throw new Exception("Main class " + className + " not found");
        }

        Method mainMethod = currentClass.getMethod("main");

        if(mainMethod == null){
            throw new Exception("main method not found in class " + className);
        }

        CurrentInfo info = new CurrentInfo(currentClass, mainMethod);

        // visit variable declarations
        n.f14.accept(this, info);

        // visit statements
        n.f15.accept(this, info);

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

        String classname = n.f1.accept(this, argu);
        String parentname = n.f3.accept(this, argu);

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

    /*
        f0 -> Identifier
        f1 -> "="
        f2 -> Expression
        f3 -> ";"
    */

    @Override
    public String visit(AssignmentStatement n, CurrentInfo argu) throws Exception {

        String var = n.f0.accept(this, argu);

        String varType = getType(var, argu);

        String expr = n.f2.accept(this, argu);

        String exprType = getType(expr, argu);

        System.out.println(varType + " " + exprType);

        if(!varType.equals(exprType)){
            throw new Exception("Bad assigment of " + exprType + " to " + varType);
        }

        return exprType;
    }

    /*
        f0 -> Identifier 
        f1 -> "[" 
        f2 -> Expression
        f3 -> "]" 
        f4 -> "=" 
        f5 -> Expression 
        f6 -> ";"
    
    */

    @Override
    public String visit(ArrayAssignmentStatement n, CurrentInfo argu) throws Exception {

        String var = n.f0.accept(this, argu);

        String varType = getType(var, argu);

        if(!varType.equals("int[]")){
            throw new Exception("Var for array assigment has to be int " + var + " is " + varType);
        }

        String expr1 = n.f2.accept(this, argu);

        String exprType1 = getType(expr1, argu);

        System.out.println(varType + " " + exprType1);

        if(!exprType1.equals("int")){
            throw new Exception("Bad array lookup of " + exprType1 + " instead of int");
        }

        String expr2 = n.f5.accept(this, argu);

        String exprType2 = getType(expr2, argu);

        if(!exprType2.equals("int")){
            throw new Exception("Bad array asigment value of " + exprType2 + " instead of int");
        }


        return "int";
    }   

    /*
        f0 -> "if" 
        f1 -> "("
        f2 -> Expression 
        f3 -> ")"
        f4 -> Statement 
        f5 -> "else"
        f6 -> Statement
    */

    @Override
    public String visit(IfStatement n, CurrentInfo argu) throws Exception {
        String expr = n.f2.accept(this, argu);
        String exprType = getType(expr, argu);

        if(!exprType.equals("boolean")){
            throw new Exception("If statement needs boolean as expression instead got " + exprType);
        }


       return null;
    }

    /*
        f0 -> "while" 
        f1 -> "("
        f2 -> Expression 
        f3 -> ")"
        f4 -> Statement 
    */

    @Override
    public String visit(WhileStatement n, CurrentInfo argu) throws Exception {
        String expr = n.f2.accept(this, argu);
        String exprType = getType(expr, argu);

        if(!exprType.equals("boolean")){
            throw new Exception("While statement needs boolean as expression instead got " + exprType);
        }


       return null;
    }

    /*
        f0 -> "System.out.println" 
        f1 -> "(" 
        f2 -> Expression 
        f3 -> ")" 
        f4 -> ";"
    */

    @Override
    public String visit(PrintStatement n, CurrentInfo argu) throws Exception {
        String expr = n.f2.accept(this, argu);
        String exprType = getType(expr, argu);

        if(!exprType.equals("int")){
            throw new Exception("Print statement needs int as expression instead got " + exprType);
        }


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

        String name = n.f2.accept(this, info);

        Method method = info.currentClass.getMethod(name);

        info.currentMethod = method;

        String statement = n.f8.accept(this, info);

        String exp = n.f10.accept(this, info);

        String expType = getType(exp, info);

        if(!expType.equals(method.returnType)){
            throw new Exception("Method " + method.name + " returns " + expType + " instead of " + method.returnType);
        }


        // super.visit(n, argu);
        return null;
    }

    @Override
    public String visit(PrimaryExpression n, CurrentInfo argu) throws Exception {

        String value = n.f0.accept(this, argu);

        // literals/types already resolved
        if(value.equals("int") ||
            value.equals("boolean") ||
            value.equals("int[]") ||
            symbolTable.containsKey(value)) {

            return value;
        }

        System.out.println(argu.currentClass.name + " " );

        // identifier expression
        return getType(value, argu);
    }

    // f0 -> PrimaryExpression 
    // f1 -> &&
    // f2 -> PrimaryExpression 

    @Override
    public String visit(AndExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        String leftType = getType(leftExp, argu);
        String rightType = getType(rightExp, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftType.equals("boolean") || !rightType.equals("boolean")){
            throw new Exception("Operator && needs type of boolean.");
        }

        return "boolean";
    }

    @Override
    public String visit(CompareExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        String leftType = getType(leftExp, argu);
        String rightType = getType(rightExp, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftType.equals("int") || !rightType.equals("int")){
            throw new Exception("Operator < needs type of int.");
        }

        return "boolean";
    }

    @Override
    public String visit(PlusExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        String leftType = getType(leftExp, argu);
        String rightType = getType(rightExp, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftType.equals("int") || !rightType.equals("int")){
            throw new Exception("Operator + needs type of int.");
        }

        return "int";
    }

    @Override
    public String visit(MinusExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        String leftType = getType(leftExp, argu);
        String rightType = getType(rightExp, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftType.equals("int") || !rightType.equals("int")){
            throw new Exception("Operator - needs type of int.");
        }

        return "int";
    }

    @Override
    public String visit(TimesExpression n, CurrentInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        String leftType = getType(leftExp, argu);
        String rightType = getType(rightExp, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftType.equals("int") || !rightType.equals("int")){
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

        String leftType = getType(leftExp, argu);
        String rightType = getType(rightExp, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftType.equals("int[]") || !rightType.equals("int")){
            throw new Exception("Operator [] needs type of int[] and int. Instead got " + leftType + " and " + rightType);
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

        String leftType = getType(leftExp, argu);

        System.out.println(leftExp + "." + "length");

        if(!leftType.equals("int[]")){
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

        String leftType = getType(leftExp, argu);

        Method method = null;

        // If leftExp is Identifier then check if the method exists in that class or if it is inherited.
        // and check if MessageSend is legal.
        if(!leftType.equals("int") && !leftType.equals("int[]") && !leftType.equals("boolean")){
            String className = leftType;

            ClassInfo info = symbolTable.get(className);

            if(info == null){
                throw new Exception("Unknown class type: " + className);
            }

            String methodCalled = n.f2.accept(this, argu);

            // Check if the parameters passed are legal.
            String params = n.f4.present() ? n.f4.accept(this, argu) : "";

            if(info.methods != null){

                // Check if method is in class or inherited.
                if(info.methods.containsKey(methodCalled)){
                    ArrayList<Method> candidates = info.methods.get(methodCalled);

                    if(candidates == null){
                        method = checkMethodInherited(methodCalled, info);
                    }
                    else{
                        for(Method m : candidates){

                            CurrentInfo tmp = new CurrentInfo(info, m);

                            if(checkParamsPassed(params, tmp)){
                                method = m;
                                break;
                            }
                        }
                    }
                }
                else{
                    method = checkMethodInherited(methodCalled, info);
                }



                if(params != null && params.trim().isEmpty()){
                    params = null;
                }

                System.err.println(params);

            
                if(!checkParamsPassed(params, new CurrentInfo(info, method))){
                    throw new Exception("Parameters passed differ from the method's " + method.name + " signature");
                }

                if(method == null){
                    throw new Exception("Method " + methodCalled + " does not exist in class " + className);
                }

            }


        }
        else{
            throw new Exception("Operator .method needs type of class.");
        }

        return method.returnType;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */

    @Override
    public String visit(ExpressionList n, CurrentInfo argu) throws Exception {
        String ret = n.f0.accept(this, argu);

        if (n.f1 != null) {
            ret += n.f1.accept(this, argu);
        }

        return ret;
    }

    /**
    * f0 -> ","
    * f1 -> Expression()
    */
    @Override
    public String visit(ExpressionTerm n, CurrentInfo argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
    * f0 -> ( ExpressionTerm() )*
    */
    @Override
    public String visit(ExpressionTail n, CurrentInfo argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, argu);
        }

        return ret;
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

        String name = n.f0.toString();
        
        return name;
    }

    @Override
    public String visit(ThisExpression n, CurrentInfo argu) throws Exception{
        if(argu.currentClass == null){
            throw new Exception("'this' used outside class");
        }

        return argu.currentClass.name;
    }

    @Override
    public String visit(ArrayAllocationExpression n, CurrentInfo argu) throws Exception{

        String expr = n.f3.accept(this, argu);

        String exprType = getType(expr, argu);

        if(!exprType.equals("int")){
            throw new Exception("Array size expression must be int");
        }

        return "int[]";
    }

    @Override
    public String visit(AllocationExpression n, CurrentInfo argu) throws Exception {

        String className = n.f1.accept(this, argu);


        if(!symbolTable.containsKey(className)){
            throw new Exception("Class " + className + " not declared");
        }

        return className;
    }

    @Override
    public String visit(NotExpression n, CurrentInfo argu) throws Exception {

        String expr = n.f1.accept(this, argu);
        String exprType = getType(expr, argu);

        if(!exprType.equals("boolean")){
            throw new Exception("Operator ! requires boolean");
        }

        return "boolean";
    }

    @Override
    public String visit(BracketExpression n, CurrentInfo argu) throws Exception {
        return n.f1.accept(this, argu);
    }
}
