// generated with ast extension for cup
// version 0.8
// 25/4/2018 14:45:36


package rs.ac.bg.etf.pp1.ast;

public class Func_dec1 extends FunctionDeclList {

    private FunctionDeclList FunctionDeclList;
    private FunctionDecl FunctionDecl;

    public Func_dec1 (FunctionDeclList FunctionDeclList, FunctionDecl FunctionDecl) {
        this.FunctionDeclList=FunctionDeclList;
        if(FunctionDeclList!=null) FunctionDeclList.setParent(this);
        this.FunctionDecl=FunctionDecl;
        if(FunctionDecl!=null) FunctionDecl.setParent(this);
    }

    public FunctionDeclList getFunctionDeclList() {
        return FunctionDeclList;
    }

    public void setFunctionDeclList(FunctionDeclList FunctionDeclList) {
        this.FunctionDeclList=FunctionDeclList;
    }

    public FunctionDecl getFunctionDecl() {
        return FunctionDecl;
    }

    public void setFunctionDecl(FunctionDecl FunctionDecl) {
        this.FunctionDecl=FunctionDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FunctionDeclList!=null) FunctionDeclList.accept(visitor);
        if(FunctionDecl!=null) FunctionDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FunctionDeclList!=null) FunctionDeclList.traverseTopDown(visitor);
        if(FunctionDecl!=null) FunctionDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FunctionDeclList!=null) FunctionDeclList.traverseBottomUp(visitor);
        if(FunctionDecl!=null) FunctionDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Func_dec1(\n");

        if(FunctionDeclList!=null)
            buffer.append(FunctionDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FunctionDecl!=null)
            buffer.append(FunctionDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Func_dec1]");
        return buffer.toString();
    }
}
