// generated with ast extension for cup
// version 0.8
// 25/4/2018 14:45:36


package rs.ac.bg.etf.pp1.ast;

public class No_statement_list extends StatementList {

    public No_statement_list () {
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
        buffer.append("No_statement_list(\n");

        buffer.append(tab);
        buffer.append(") [No_statement_list]");
        return buffer.toString();
    }
}
