// generated with ast extension for cup
// version 0.8
// 9/4/2018 15:44:37


package rs.ac.bg.etf.pp1.ast;

public class Globa_var_error extends GlobalVarPart {

    public Globa_var_error () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Globa_var_error(\n");

        buffer.append(tab);
        buffer.append(") [Globa_var_error]");
        return buffer.toString();
    }
}
