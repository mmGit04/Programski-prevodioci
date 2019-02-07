// generated with ast extension for cup
// version 0.8
// 25/4/2018 14:45:36


package rs.ac.bg.etf.pp1.ast;

public class Matched1 extends Statement {

    private Designator Designator;
    private DesignatorLeft DesignatorLeft;

    public Matched1 (Designator Designator, DesignatorLeft DesignatorLeft) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.DesignatorLeft=DesignatorLeft;
        if(DesignatorLeft!=null) DesignatorLeft.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public DesignatorLeft getDesignatorLeft() {
        return DesignatorLeft;
    }

    public void setDesignatorLeft(DesignatorLeft DesignatorLeft) {
        this.DesignatorLeft=DesignatorLeft;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(DesignatorLeft!=null) DesignatorLeft.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(DesignatorLeft!=null) DesignatorLeft.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(DesignatorLeft!=null) DesignatorLeft.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Matched1(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesignatorLeft!=null)
            buffer.append(DesignatorLeft.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Matched1]");
        return buffer.toString();
    }
}
