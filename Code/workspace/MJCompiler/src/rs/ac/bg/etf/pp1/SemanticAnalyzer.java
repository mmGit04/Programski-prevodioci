package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;

import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;


public class SemanticAnalyzer extends VisitorAdaptor {

    private boolean mainDetected = false;
    public boolean greska_sem=false;
    private String currentName="";
    public static Struct boolType = new Struct(5);
    private Struct var_type=Tab.noType;//Trenutni tip promeljive koja se definise kada imamo veci broj designatora koje definisemo sa tim tipom.
    
  public  int  globalVarCount=0;
  public  int globalArrayCount=0;
  public  int functionCount=0;
  public  int globalConstCount=0;
    
    private String objNodeToString(Obj obj) {
    	if(obj==null) return null;
    	StringBuilder sb=new StringBuilder("Obj cvor: ");
    	switch(obj.getKind())
    	{
    	case Obj.Con:  sb.append("Con "); break;
        case Obj.Var:  sb.append("Var "); break;
        case Obj.Type: sb.append("Type "); break;
        case Obj.Meth: sb.append("Meth "); break;
        case Obj.Fld:  sb.append("Fld "); break;
        case Obj.Prog: sb.append("Prog "); break;
    	}
    	sb.append(obj.getName());
    	sb.append(": ");
    	
        switch (obj.getType().getKind()) 
        {
            case Struct.None:   sb.append("notype"); break;
            case Struct.Int:    sb.append("int"); break;
            case Struct.Char:   sb.append("char"); break;
            case Struct.Class:  sb.append("class"); break;
            case 5:             sb.append("bool"); break;
            case Struct.Array:  sb.append("Arr of ");
            
            	switch (obj.getType().getElemType().getKind()) 
            	{
                    case Struct.None:   sb.append("notype"); break;
                    case Struct.Int:    sb.append("int"); break;
                    case Struct.Char:   sb.append("char"); break;
                    case Struct.Class:  sb.append("class"); break;
                    case 5:             sb.append("bool"); break;
                    default: break;
            	}
            
            default: break;
        }

        sb.append(", ");
        sb.append(obj.getAdr());
        sb.append(", ");
        sb.append(obj.getLevel());

        return sb.toString();
    }
    
    boolean isDeclared(String name)
    {
    	if(name==null) return true;
    	Obj temp=Tab.find(name);
    	if(temp.getKind()==Obj.Type) return true;
    	
    	temp=Tab.currentScope.findSymbol(name);
    	if(temp==Tab.noObj || temp==null) return false;
    	return true;
    }
    
    public static void init() {//Za ubacivanje i novog tipa podataka bool;
    	Tab.init();
    	Tab.currentScope.addToLocals (new Obj (Obj.Type, "bool", boolType ) );
    	
    }
    
    public void report_error(String message) {
    	greska_sem=true;
        System.err.print(message);
        System.err.println("");
        System.err.flush();
      }
    public void report_info(String message) {
    	System.out.print(message);
    	System.out.println("");
    }
    
	public void visit(Programm program) {
		
		program.getProgramName().obj.setLocals(Tab.currentScope.getLocals());
		
		Tab.closeScope();
		
       if(!mainDetected) report_error("Semanticka greska: void main() nije pronadjeno.");
       
	} 
    
	public void visit(Program_name prog_id) {
		prog_id.obj = Tab.insert(Obj.Prog,prog_id.getId(),Tab.noType);
        Tab.openScope();
        
	}
	
	public void visit(Const_number const_number) { 
		
		
		if(var_type.getKind()!=Tab.intType.getKind())
		{
			report_error("Konteksni uslov nije ispunjen("+ const_number.getLine()+ "): Konstanta " + const_number.getId() + " nije int tipa.");
			return;
		}
		if(isDeclared(const_number.getId()))
			report_error("Semanticka greska(" + const_number.getLine() + "): " + const_number.getId() + " je vec deklarisano.");
		else {
			Obj tem=Tab.insert(Obj.Con, const_number.getId(), var_type); 
		
			 
			tem.setAdr(const_number.getValue());
			//Vrednost ovde postavljam ako smo sad ubacili element;
			report_info("Deklarisana simbolicka konstana " + const_number.getId() + " na liniji " + const_number.getLine() +"." + objNodeToString(tem) );
			globalConstCount++;
		}
	}

	public void visit(Const_char const_char) { 
		
	if(var_type.getKind()!=Tab.charType.getKind())
	{
		report_error("Konteksni uslov nije ispunjen("+ const_char.getLine()+ "): Konstanta " + const_char.getId() + " nije char tipa.");
		return;
	}
	if(isDeclared(const_char.getId()))
		report_error("Semanticka greska(" + const_char.getLine() + "): " + const_char.getId() + " je vec deklarisano.");
	else {
		Obj tem=Tab.insert(Obj.Con, const_char.getId(),var_type); 
		
		
		tem.setAdr(const_char.getValue());
			
		
	report_info("Deklarisana simbolicka konstana " + const_char.getId() + " na liniji " + const_char.getLine() +"." + objNodeToString(tem) );
	globalConstCount++;
	}
	
}
	
    public void visit(Const_bool const_bool) { 
		
		if(var_type.getKind()!=boolType.getKind())
		{
			report_error("Konteksni uslov nije ispunjen("+ const_bool.getLine()+ "): Konstanta " + const_bool.getId() + " nije bool tipa.");
			return;
		}
		if(isDeclared(const_bool.getId()))
			report_error("Semanticka greska(" + const_bool.getLine() + "): " + const_bool.getId() + " je vec deklarisano.");
		else {
		Obj tem=Tab.insert(Obj.Con, const_bool.getId(), var_type); 

		
			if(const_bool.getValue().equals("true"))
			tem.setAdr(1);
			else tem.setAdr(0);
		
		report_info("Deklarisana simbolicka konstana " + const_bool.getId() + " na liniji " + const_bool.getLine() +"." + objNodeToString(tem) );
		globalConstCount++;
		
	}

}
	
	public void visit(Typee type) { 
		Obj novi=Tab.find(type.getId());
		if(novi==Tab.noObj) //find vraca noOBJ dok u currentScope vraca ili noObj ili null;
		{
            report_error("Semanticka greska (" + type.getLine() + "):" +type.getId()+" nije tip.");
            var_type=Tab.noType;
            type.obj=null;
		}
		else 
		{
			if(novi.getKind()==Obj.Type){
				var_type=novi.getType();
				type.obj=novi;
			}
			else {
				type.obj=null;
				var_type=Tab.noType;
				report_error("Semanticka greska (" + type.getLine() + "):" +type.getId()+" nije tip.");
			}
		}  
		
	}
	

	public void visit(Global_var_id globid) { 
	if(var_type==Tab.noType) return;
	
	if(isDeclared(globid.getId())) {
		report_error("Semanticka greska(" + globid.getLine() + "): " + globid.getId() + " je vec deklarisano.");
		return;
	}
	Obj pom=Tab.insert(Obj.Var, globid.getId(), var_type); 
	 report_info("Deklarisana globalna promenljiva " + globid.getId()+ " na liniji " + globid.getLine()+ "." + objNodeToString(pom));
	globalVarCount++;	
	
	}
	
	public void visit(Global_var_array_id varArrayId) {
		if(var_type==Tab.noType) return;
		if(isDeclared(varArrayId.getId())) {
			report_error("Semanticka greska(" + varArrayId.getLine() + "): " + varArrayId.getId() + " je vec deklarisano.");
			return;
		}
		Obj pom=Tab.insert(Obj.Var, varArrayId.getId() ,new Struct(Struct.Array, var_type));  
		 report_info("Deklarisana globalna promenljiva " + varArrayId.getId()+ " na liniji " + varArrayId.getLine() + "." + objNodeToString(pom));
		globalArrayCount++;
	}

	public void visit(Func_void2 voidRetType) {
		voidRetType.obj=null;
		if(isDeclared(voidRetType.getName())) {
			report_error("Semanticka greska(" + voidRetType.getLine() + "): " + voidRetType.getName() + " je vec deklarisano.");
			
		}
		else {
			voidRetType.obj=Tab.insert( Obj.Meth, voidRetType.getName(), Tab.noType); 
			voidRetType.obj.setLevel(0);
			Tab.openScope();
	
			functionCount++;
		}
		if(voidRetType.getName().equals("main") && voidRetType.obj!=null) {
			mainDetected=true;
			report_info("Detektovana void main() funkcija na liniji " + voidRetType.getLine() + ".");
		}
        		 
	}
	
	public void visit(FunctionStart1 funcStart) {
		Obj o = funcStart.getFunction().obj;
		if(o!=null) {
	    	o.setLocals( Tab.currentScope.getLocals());
		}
		
	}
	
	public void visit(Function_decl func) {
	
        Tab.closeScope();
    	
}
	
	public void visit(Var_part1 local) { 
		if(var_type==Tab.noType) return;
		if(isDeclared(local.getId()))
			report_error("Semanticka greska(" + local.getLine() + "): " + local.getId() + " je vec deklarisano.");
		else {
		Obj pom=Tab.insert(Obj.Var, local.getId(), var_type); 
		report_info("Deklarisana lokalna promenljiva " + local.getId()+ " na liniji " + local.getLine() + "." + objNodeToString(pom));
		
		}
	}
	
	public void visit(Var_part2 localarr) {
		if(var_type==Tab.noType) return;
		if(isDeclared(localarr.getId()))
			report_error("Semanticka greska(" + localarr.getLine() + "): " + localarr.getId() + " je vec deklarisano.");
		else {
		Obj pom=Tab.insert(Obj.Var, localarr.getId(),new Struct(Struct.Array, var_type));  
		report_info("Deklarisana lokalna promenljiva " + localarr.getId()+ " na liniji " + localarr.getLine() + "." + objNodeToString(pom));
		
		}
	}
		
	public void visit(Designator3 des) {
		Obj obj = null;
	   obj = Tab.find(des.getName());
	   if ( obj == Tab.noObj)
       {
		   obj=null;
           report_error("Semanticka greska(" + des.getLine() + "): "+des.getName()+" nije deklarisano pre koriscenja.");
       }
	   else {
		  if(obj.getKind()!=Obj.Meth) report_info("Simbol " + des.getName() + " je pronadjen na liniji " + des.getLine() + "." + objNodeToString(obj));
	   }
	   des.obj=obj;
	}
	
	public void visit(DesignatorArray des) {
		des.obj=des.getDesignator().obj;
		
	}
	public void visit(Designator2 desArr) {
		Obj des=desArr.getDesignatorArray().obj;
		Obj exp=desArr.getExpr().obj;
		if(des!=null && exp!=null) {
			if(des.getType().getKind()!=Struct.Array) {
				report_error("Konteksni uslov nije ispunjen(" + desArr.getLine() + "):simbol niza nije tipa niz.");
				return;
			}
			if(exp.getType()!=Tab.intType) {
				report_error("Konteksni uslov nije ispunje(" + desArr.getLine() + "):Indeks niza nije int tipa.");
				return;
			}
			desArr.obj=new Obj(Obj.Elem,des.getName(),des.getType().getElemType());
		}
			
	}
	
	public void visit(Factor1 Fnumber) {
		Obj con=new Obj(Obj.Con,"",Tab.intType);
		con.setAdr(Fnumber.getValue());
		Fnumber.obj=con;
		
	}
	
	public void visit(Factor2 Fchar) {
		Obj con=new Obj(Obj.Con,"",Tab.charType);
		con.setAdr(Fchar.getValue());
		
		Fchar.obj=con;
	}
	
	public void visit(Factor3 Fbool) {
		Obj con=new Obj(Obj.Con,"",boolType);
		if(Fbool.getValue().equals("true"))
			con.setAdr(1);
		else con.setAdr(0);
		
		Fbool.obj=con;
		
	}
	
	public void visit(Factor5 Fexpr) {
		Fexpr.obj=Fexpr.getExpr().obj;
	}
	
	public void visit(Factor7 FnewArr) {
		Obj t=FnewArr.getType().obj;
		Obj e=FnewArr.getExpr().obj;
		if(t!=null && e!=null) {
			if(e.getType()!=Tab.intType)
				report_error("Konteksni uslov nije ispunjen("+ FnewArr.getLine()+ "): Izraz koji definise velicinu niza mora biti int tipa.");
			else {
				Obj arr=new Obj(Obj.Elem,t.getName(),new Struct(Struct.Array,t.getType()));
				FnewArr.obj=arr;
			}
		}
		else FnewArr.obj=null;
	}
	
	public void visit(Factor8 desFactor) {
		desFactor.obj=desFactor.getDesignator().obj;
			
	}
	public void visit(Term2 termFactor) {
		termFactor.obj=termFactor.getFactor().obj;
			
	}
	
	public void visit(Term1 termCom) {
		Obj term=termCom.getTerm().obj;
		Obj fact=termCom.getFactor().obj;
		if(term!=null && fact!=null)
		{
			if(term.getType().equals(fact.getType()) && term.getType()==Tab.intType)
				
				termCom.obj=term;
		
			else {
			report_error("Konteksni uslov nije ispunjen(" + termCom.getLine() + "):Operand nije Int tipa.");
			termCom.obj=null;
			}
		
		}
	}
	
	public void visit(Expr3 minus_term) {
		Obj term=minus_term.getTerm().obj;
		if(term!=null) {
			if(term.getType()!=Tab.intType)
			report_error("Konteksni uslov nije ispunjen:(" + minus_term.getLine() + "):Operand nije int tipa.");
		
		}
		
		minus_term.obj=term;
	}
	
	public void visit(Expr2 term) {
		term.obj=term.getTerm().obj;
	}
	
	public void visit(Expr1 termCom) {
		Obj term=termCom.getTerm().obj;
		Obj expr=termCom.getExpr().obj;
		if(term!=null && expr!=null) {
			if(term.getType().equals(expr.getType()) && (term.getType()==Tab.intType))
				termCom.obj=expr;

			else {
				report_error("Konteksni uslov nije ispunjen(" + termCom.getLine()+ "):Operand nije int tipa.");
				termCom.obj=null;
				}
			
		}
	}
	
	public void visit(Func_des func_call) {
		Obj func=func_call.getDesignator().obj;
		if(func!=null) {
			if(Obj.Meth==func.getKind())
				report_info("Poziv globalne funkcije " + func.getName() + " u liniji " + func_call.getLine()+ ".");
				
			else {
				report_error("Semanticka greska("+func_call.getLine() + "):naziv funkcije nije funkcija.");
				func_call.obj=null;
			}
			func_call.obj=func;
			
		}
	}
	
	public void visit(Designator_statement4 state_dec) {
		
		Obj des=state_dec.getDesignator().obj;
		if(des!=null) {
			if(!(des.getKind()==Obj.Var || des.getKind()==Obj.Elem)) {
				report_error("Semanticka greska(" + state_dec.getLine()+ "):Simbol mora biti promenljiva ili element niza.");
				return;
			}
			if(des.getType()!=Tab.intType) {
				report_error("Semanticka greska(" + state_dec.getLine()+ "):Simbol mora biti int tipa.");
				return;
			}
			
		}
		
	}
		
	public void visit(Designator_statement3 state_inc) {
		
		Obj des=state_inc.getDesignator().obj;
		if(des!=null) {
			if(!(des.getKind()==Obj.Var || des.getKind()==Obj.Elem)) {
				report_error("Semanticka greska(" + state_inc.getLine()+ "):Simbol mora biti promenljiva ili element niza.");
				return;
			}
			if(des.getType()!=Tab.intType) {
				report_error("Semanticka greska(" + state_inc.getLine()+ "):Simbol mora biti int tipa.");
				return;
			}
			
		}
		
	}
	
	public void visit(Designator_left des_left) {
		des_left.obj=des_left.getExpr().obj;
	}

	public void visit(Matched1 assign) {
		Obj des=assign.getDesignator().obj;
		Obj expr=assign.getDesignatorLeft().obj;
		if(des!=null && expr!=null) {
			
			Struct t1=des.getType();//Ja sam imala gresku da sam tip new Array factora nazvala elem,a onda sam struct promenljive postavila kao array,ta greska ne utice. 
			Struct t2=expr.getType();
			
			if(!t2.assignableTo(t1)) {
				report_error("Semantcika greska("+ assign.getLine()+ "):Tipovi nisu kompatabilni pri dodeli.");
				return;
			}
			if(!(des.getKind()==Obj.Var || des.getKind()==Obj.Elem)) {
				report_error("Semanticka greska(" + assign.getLine()+ "):Simbol mora biti promenljiva ili element niza.");
				return;
			}
		}
	}	
		
	public void visit(Matched2 read_state) {
		Obj des=read_state.getDesignator().obj;
		if(des!=null) {
			if(!(des.getKind()==Obj.Var || des.getKind()==Obj.Elem)) {
				report_error("Semanticka greska(" + read_state.getLine()+ "):Simbol mora biti promenljiva ili element niza.");
				return;
			}
			if(des.getType()!=Tab.intType && des.getType()!=Tab.charType && des.getType()!=boolType ) {
				report_error("Semanticka greska(" + read_state.getLine()+ "):Simbol mora biti int,char ili bool tipa.");
				return;
			}
				
		}		
	}
		
	public void visit(Matched3 print_expr) {
		Obj e=print_expr.getExpr().obj;
		if(!(e.getType()==Tab.intType || e.getType()==Tab.charType || e.getType()==boolType))
			report_error("Semanticka greska(" + print_expr.getLine()+ "):Simbol mora biti int,char ili bool tipa.");

	}	
		
	public void visit(Matched4 print_expr) {
		Obj e=print_expr.getExpr().obj;
		if(!(e.getType()==Tab.intType || e.getType()==Tab.charType || e.getType()==boolType))
			report_error("Semanticka greska(" + print_expr.getLine()+ "):Simbol mora biti int,char ili bool tipa.");

	}	
	

    
}
