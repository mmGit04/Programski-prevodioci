package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;
import rs.etf.pp1.symboltable.visitors.SymbolTableVisitor;

public class MJTest {

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}

	public static void main(String[] args) throws Exception {
		Logger log = Logger.getLogger(MJTest.class);
		Reader br = null;
		try {
			
			File sourceCode = new File("test/program.mj");	
			PrintStream myconsole=new PrintStream("test/izlaz.out");
			System.setOut(myconsole);
			PrintStream myconsolerr=new PrintStream("test/izlaz.err");
			System.setErr(myconsolerr);
			log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
			br = new BufferedReader(new FileReader(sourceCode));
			
			Yylex lexer = new Yylex(br);
			
			//Testiranje samo rada lexera uz ispisivanje u output fajlove.
			/*Symbol currToken=null;
			while((currToken=lexer.next_token()).sym!=sym.EOF) {
				if(currToken!=null) {
					System.out.println(currToken.toString() + "  " + currToken.value.toString());
				}
		}
		*/
			
			
	     	MJParser p = new MJParser(lexer);
	        Symbol s = p.parse();  //pocetak parsiranja
	        if (p.greska) System.out.println("Ulazni program ima sintaksnih gresaka!");
	        else System.out.println("Parsiranje uspesno zavrseno!");

			Program prog = (Program)(s.value);
			
			SemanticAnalyzer.init();
			SemanticAnalyzer sa=new SemanticAnalyzer();
			prog.traverseBottomUp(sa);
			tsdump();
			if(sa.greska_sem) System.out.println("Ulazni program ima semantickih gresaka");
			else System.out.println("Ulazni program nema semantickih gresaka");
			
		//Treba raditi generisanje koda samo ako nije bilo nikakvih sintaksnih ili semnatickih gresaka.	
	    CodeGenerator cg=new CodeGenerator();
			prog.traverseBottomUp(cg);
			if(!p.greska && !sa.greska_sem) {
				File objFile=new File("test/program.obj");
				if (objFile.exists())
	        		objFile.delete();
				Code.write(new FileOutputStream(objFile));
				System.out.println("Uspesno izvrsen ispis koda u obj file");
			}
			else System.out.println("Nije izvrsen ispis u obj file.");
		/// ispis sintaksnog stabla
	        System.out.println(prog.toString(""));
		    System.out.println("===================================");

	       
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { log.error(e1.getMessage(), e1); }
		}
	}
     public static void tsdump() {

 		System.out.println("=====================SYMBOL TABLE DUMP=========================");
 		SymbolTableVisitor	stv = new NewSymbolTableVisitor();
 		for (Scope s = Tab.currentScope; s != null; s = s.getOuter()) {
 			s.accept(stv);
 		}
 		System.out.println(stv.getOutput());
	}
}
