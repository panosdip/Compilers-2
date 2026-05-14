
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

    // boolean lookVar(name, className){
    //     if(symbolTable.containsKey(className)){
    //         ClassInfo info = symbolTable.get(className);

    //         info.
    //     }

    //     return false;
    // }

    // f0 -> PrimaryExpression 
    // f1 -> &&
    // f2 -> PrimaryExpression 

    @Override
    public String visit(AndExpression n, ClassInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("boolean") || !rightExp.equals("boolean")){
            throw new Exception("Operator && needs type of boolean.");
        }

        return "boolean";
    }

    public String visit(CompareExpression n, ClassInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("boolean") || !rightExp.equals("boolean")){
            throw new Exception("Operator + needs type of boolean.");
        }

        return "boolean";
    }

    public String visit(PlusExpression n, ClassInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("int") || !rightExp.equals("int")){
            throw new Exception("Operator + needs type of int.");
        }

        return "int";
    }

    public String visit(MinusExpression n, ClassInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("int") || !rightExp.equals("int")){
            throw new Exception("Operator + needs type of int.");
        }

        return "int";
    }

    public String visit(TimesExpression n, ClassInfo argu) throws Exception{
        String leftExp = n.f0.accept(this, argu);
        String rightExp = n.f2.accept(this, argu);

        System.out.println(leftExp + " " + rightExp);

        if(!leftExp.equals("int") || !rightExp.equals("int")){
            throw new Exception("Operator + needs type of int.");
        }

        return "int";
    }

    @Override
    public String visit(IntegerLiteral n, ClassInfo argu){
        return "int";
    }

    @Override
    public String visit(TrueLiteral n, ClassInfo argu){
        return "boolean";
    }

    public String visit(FalseLiteral n, ClassInfo argu){
        return "boolean";
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    @Override
    public String visit(Identifier n, ClassInfo argu) {
        // Find the identifier starting from locals, parameters, fields and last inherited
        // if it exists return the type of the identifier.
        return n.f0.toString();
    }
}
