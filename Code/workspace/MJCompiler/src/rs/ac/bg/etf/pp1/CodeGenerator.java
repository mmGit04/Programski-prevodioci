package rs.ac.bg.etf.pp1;

import java.util.Collection;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;

public class CodeGenerator extends VisitorAdaptor{

	public int nVars(Collection<Obj> objects) {
		int n=0;
		
		for(Obj pom:objects) {
			if(pom.getKind()==Obj.Var)
				n++;
		}
		return n;
	}
	
	
	public void visit(Programm program) {
		Code.dataSize=nVars(program.getProgramName().obj.getLocalSymbols());
	}
	
	public void visit(Func_void2 voidRetType) {
		Obj func=voidRetType.obj;
		func.setAdr(Code.pc);//Kad naidjem na ime funkcije ja dodeljujem adresu jer necu nista stavljati u bufer.
		if(func.getName().equals("main")) {
			Code.mainPc=Code.pc;
		}
	
	}
	
	public void visit(FunctionStart1 funcStart) {
		Obj func=funcStart.getFunction().obj;
		Code.put(Code.enter);
		Code.put(func.getLevel());
		Code.put(nVars(func.getLocalSymbols()));
		
	}
	
	
	public void visit(Function_decl func) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	public void visit(Matched1 assign) {
		Obj des=assign.getDesignator().obj;//Ovo sad moze biti i element niza i niz i obicna promenljiva.Ako je elem onda se koriste aload i bload.Koje sa steka skidaju adrese niza i indeks.
		Code.store(des);
	}
	
	public void visit(Designator_statement5 callFunc) {
		Obj func=callFunc.getFuncCall().obj;
		int destAdr=func.getAdr()-Code.pc;
		Code.put(Code.call);
		Code.put2(destAdr);
	}
	
	
	public void visit(Designator_statement3 state_inc) {
		Obj des=state_inc.getDesignator().obj;
		Code.load(des);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(des);
	}
	
	public void visit(Designator_statement4 state_dec) {
		Obj des=state_dec.getDesignator().obj;
		Code.load(des);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(des);
		
	}
	
	public void visit(Matched2 read) {
		Obj des=read.getDesignator().obj;
		if(des.getType()==Tab.intType || des.getType()==SemanticAnalyzer.boolType) {
			Code.put(Code.read);
			Code.store(des);
		}
		else {
			Code.put(Code.bread);
			Code.store(des);
		}
	}
	
	public void visit(Matched3 print_expr) {
		Obj expr=print_expr.getExpr().obj;
		if(expr.getType()==Tab.intType) {
			Code.loadConst(5);
			Code.put(Code.print);
		}
		else if(expr.getType()==Tab.charType) {
			Code.loadConst(1);
			Code.put(Code.bprint);
			
		}
		else if(expr.getType()==SemanticAnalyzer.boolType) {
			Code.loadConst(1);
			Code.put(Code.print);
			
		}
	}
	
	public void visit(Matched4 print_expr) {
		Obj expr=print_expr.getExpr().obj;
		if(expr.getType()==Tab.intType) {
			Code.loadConst(print_expr.getWidth());
			Code.put(Code.print);
		}
		else if(expr.getType()==Tab.charType) {
			Code.loadConst(print_expr.getWidth());
			Code.put(Code.bprint);
		}
		else if(expr.getType()==SemanticAnalyzer.boolType) {
			Code.loadConst(print_expr.getWidth());
			Code.put(Code.print);
		}
		
	}
	
	public void visit(Factor1 Fnumber) {
		Obj o=Fnumber.obj;
		Code.load(o);
	}
	
	public void visit(Factor2 Fchar) {
		Obj o=Fchar.obj;
		Code.load(o);
	}
	
	public void visit(Factor3 Fbool) {
		Obj o=Fbool.obj;
		Code.load(o);
	}
	
	public void visit(Factor8 Fdes) {
		Obj o=Fdes.obj;
		Code.load(o);
	}
	public void visit(Mulop_mull mullop) {
		Obj mull=new Obj(Obj.Con,"mulop",Tab.intType);
		mull.setAdr(Code.mul);
		mullop.obj=mull;
	}
	
	public void visit(Mulop_div divop) {
		Obj div=new Obj(Obj.Con,"divop",Tab.intType);
		div.setAdr(Code.div);
		divop.obj=div;
	}
	
	public void visit(Mulop_mod modop) {
		Obj mod=new Obj(Obj.Con,"modop",Tab.intType);
		mod.setAdr(Code.rem);
		modop.obj=mod;//Ovu liniju koda sam bila zaboravila.
	}
	
	public void visit(Addop1 addop) {
		Obj add=new Obj(Obj.Con,"addop",Tab.intType);
		add.setAdr(Code.add);
		addop.obj=add;
	}
	
	public void visit(Addop2 subop) {
		Obj sub=new Obj(Obj.Con,"subop",Tab.intType);
		sub.setAdr(Code.sub);//Ovde sam prvo napisala add;
		subop.obj=sub;
	}
	
	public void visit(Factor7 newArr) {
		Obj tip=newArr.getType().obj;
		Code.put(Code.newarray);
		if(tip.getType()==Tab.charType) Code.put(0);
		else Code.put(1);
	}
	
	public void visit(Term1 termcom) {
		Obj op=termcom.getMulop().obj;
		Code.put(op.getAdr());
	}
	
	public void visit(Expr3 minusterm) {
		Code.put(Code.neg);
	}
	
	public void visit(Expr1 exprcom) {
		Obj op=exprcom.getAddop().obj;
		Code.put(op.getAdr());
	}
	
	public void visit(DesignatorArray desArr) {
		Obj des=desArr.obj;
		//Obj des=desArr.getDesignator().obj;//Ovo sluzi da bi na stek ucitali adresu niza .Koja je potrebno za rad sa nizovima.Npr kod dedele vrednosti elementu niza.//Ili ako je factor.
		Code.load(des);
	}
	
	
}
