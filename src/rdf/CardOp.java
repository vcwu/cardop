package rdf;

import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpVisitor;
import com.hp.hpl.jena.sparql.algebra.OpWalker;
import com.hp.hpl.jena.sparql.algebra.op.OpAssign;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpConditional;
import com.hp.hpl.jena.sparql.algebra.op.OpDatasetNames;
import com.hp.hpl.jena.sparql.algebra.op.OpDiff;
import com.hp.hpl.jena.sparql.algebra.op.OpDisjunction;
import com.hp.hpl.jena.sparql.algebra.op.OpDistinct;
import com.hp.hpl.jena.sparql.algebra.op.OpExt;
import com.hp.hpl.jena.sparql.algebra.op.OpExtend;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpGraph;
import com.hp.hpl.jena.sparql.algebra.op.OpGroup;
import com.hp.hpl.jena.sparql.algebra.op.OpJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpLabel;
import com.hp.hpl.jena.sparql.algebra.op.OpLeftJoin;
import com.hp.hpl.jena.sparql.algebra.op.OpList;
import com.hp.hpl.jena.sparql.algebra.op.OpMinus;
import com.hp.hpl.jena.sparql.algebra.op.OpNull;
import com.hp.hpl.jena.sparql.algebra.op.OpOrder;
import com.hp.hpl.jena.sparql.algebra.op.OpPath;
import com.hp.hpl.jena.sparql.algebra.op.OpProcedure;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.algebra.op.OpPropFunc;
import com.hp.hpl.jena.sparql.algebra.op.OpQuad;
import com.hp.hpl.jena.sparql.algebra.op.OpQuadPattern;
import com.hp.hpl.jena.sparql.algebra.op.OpReduced;
import com.hp.hpl.jena.sparql.algebra.op.OpSequence;
import com.hp.hpl.jena.sparql.algebra.op.OpService;
import com.hp.hpl.jena.sparql.algebra.op.OpSlice;
import com.hp.hpl.jena.sparql.algebra.op.OpTable;
import com.hp.hpl.jena.sparql.algebra.op.OpTopN;
import com.hp.hpl.jena.sparql.algebra.op.OpTriple;
import com.hp.hpl.jena.sparql.algebra.op.OpUnion;

/*
 * Delicious sandboxing for cardinality optimization woohoo
 * 
 */

public class CardOp {
	
	//Here's the meat and potatoes. 
	public static ArrayList<Op> reorder(Query orig){
	
		
		// Get the service
		Op op = Algebra.compile(orig);
		ArrayList<Op> stuff = new ArrayList<Op>();
		OpWalker.walk(op, new Converter(stuff));
		return stuff;
		
	}
	
	public static class Converter  implements OpVisitor {
	
		
		//private HashMap<Op, Integer> map;
		private ArrayList<Op> map;
			
		public Converter(ArrayList<Op> m)	{
			map = m;
		}
	
		@Override
		public void visit(OpService arg0) {
			// TODO Auto-generated method stub
			//YAAAY!... opefully this does what i want it to do T.T
			System.out.println("VISITING OPSERVICE");
			map.add(arg0);
		}
		
		@Override
		public void visit(OpBGP arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpQuadPattern arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpTriple arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpQuad arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpPath arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpTable arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpNull arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpProcedure arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpPropFunc arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpFilter arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpGraph arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void visit(OpDatasetNames arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpLabel arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpAssign arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpExtend arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpJoin arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpLeftJoin arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpUnion arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpDiff arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpMinus arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpConditional arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpSequence arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpDisjunction arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpExt arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpList arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpOrder arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpProject arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpReduced arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpDistinct arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpSlice arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpGroup arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void visit(OpTopN arg0) {
			// TODO Auto-generated method stub
			
		}
	}

}
