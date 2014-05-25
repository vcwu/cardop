package rdf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
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
	public static Query reorder(Query orig, ArrayList<ServiceObj> givenCardinalities ){
		
		
		//Convert query to algebra and parse by service call
		Op op = Algebra.compile(orig);	
		ArrayList<Op> indivServices = grabIndivServices(op);
		
		//Grab the estimated cardinality from the file and reorder into a new algebra op
		ArrayList<ServiceObj> pairedCardinalities = grabEstimatedCardinalityArray(indivServices, givenCardinalities);
		pairedCardinalities= reorderAlgebra(pairedCardinalities);	//do the actual reordering based on the cardinality
		Op newOp  = makeNewAlgebra(pairedCardinalities);	//mash them together into one algebra expression again
		
		printDebug(orig, op, pairedCardinalities, newOp);	
		return OpAsQuery.asQuery(newOp);

	}

	/*
	 * Parse the file of estimated cardinalities.
	 */
	static ArrayList<ServiceObj> parseCardinalityFile(String file_card)	{
		System.out.println("Parsing cardinality file...");
		ArrayList<ServiceObj> tempList = new ArrayList<ServiceObj>();
		try	{
			BufferedReader in = new BufferedReader(new FileReader(file_card));
			Scanner src = new Scanner(in);
			src.useDelimiter("\\$");
			
			Op op;
			int card;
			while(src.hasNext())	{
				//Grab query -> change to algebra
				op = Algebra.compile(QueryFactory.create(src.next()));	
				
				//Grab the cardinality
				card =  Integer.parseInt(src.next().trim());
				
				ServiceObj serv = new ServiceObj(op, card);
				tempList.add(serv);
			}
		}
		catch (FileNotFoundException e)	{
			System.err.println("File " + file_card + " not found");
		}
		catch (QueryException e){
			System.err.println("Syntax Error in file " + file_card);
			System.err.println("Incorrect syntax in query");
			System.err.println(e.getMessage());
		}
		catch (NumberFormatException e){
			System.err.println("Syntax error in file " + file_card);
			System.err.println("Numerical cardinality required");
			System.err.println(e.getMessage());
		}
		
		System.out.println("Done.");
		return tempList;
	}
	
	
	static void printDebug(Query orig, Op op, ArrayList<ServiceObj> cardinalities, Op newOp){
		System.out.println("Original Query\n========================");
		System.out.println(orig);
		
		System.out.println("Original Algebra\n========================");
		System.out.println(op);
		
		System.out.println("Reorder by Cardinality \n========================");
		System.out.println(cardinalities);
		
		System.out.println("redone algebra  \n========================");
		System.out.println(newOp);
	}
	
	/*
	 * reorder the algebra using greedy algorithm
	 * returns the reordered algebra
	 * 
	 * -> looking up if hash map even preserves order in java
	 * ok. hash map doesn't preserve or guarantee order. but treemap does which implements sortedmap interface :D
	 * 
	 * wait. so treemaps are apparently a loooot slower. i don't actually even need to be sorting IN the map itself, 
	 * all that matters is the key value anyways
	 * 
	 */
	private static ArrayList<ServiceObj> reorderAlgebra(ArrayList<ServiceObj> servObjs) {
		//Reorder the service calls based on the cardinality
		Collections.sort(servObjs);	//sorts based on the compareTo method in servObjs
		return servObjs;	//just return the sorted array list for now for testing
	}
	
	/*
	 * makeNewAlgebra
	 * given an array list of already sorted service calls, mash them together into one algebra expression again
	 * problem - this erases the algebra outside the service calls.. so basically select * 
	 */
	
	private static Op makeNewAlgebra(ArrayList<ServiceObj> servObjs)	{
		//erm let's just assume we have two service calls ok
		Op op = OpSequence.create(servObjs.get(0).getOp(), servObjs.get(1).getOp());
		return op;
	}
	
	
	
	/*
	 * Returns array list of individual service calls
	 */
	private static ArrayList<Op> grabIndivServices(Op op)	{
		ArrayList<Op> stuff = new ArrayList<Op>();
		OpWalker.walk(op, new ServiceGrabber(stuff));
		return stuff;
	}
	
	
	/*
	 * Given a bunch of service calls, will look up their respective estimated cardinalities from given cardinality.
	 * If a service call bgp is not found in the estimated cardinality array, cardinality of infinity is given. 
	 * Essentially a left join between scraped services, given service cardinalities; the service calls without a given cardinality 
	 * are assigned a cardinality of infinity. 
	 * 
	 */
	private static ArrayList<ServiceObj>  grabEstimatedCardinalityArray(ArrayList<Op> services, ArrayList<ServiceObj> givenCard){
		
		ArrayList<ServiceObj> pairedList = new ArrayList<ServiceObj>();
		ServiceObj temp;
		int card;
		for(Op op: services){
			temp = new ServiceObj(op);
			if(givenCard.contains(temp))	{
				card = givenCard.get(givenCard.indexOf(temp)).getCard();
			}
			else	{
				card = Integer.MAX_VALUE;
			}
			pairedList.add(new ServiceObj(op, card));
			
		}
		return pairedList;
	}
	
	/*
	 * Fake cardinality grabbing - for now, just ascending order.
	 */
	private static ArrayList<ServiceObj>  grabEstimatedCardinalityArray(ArrayList<Op> services){
		
		ArrayList<ServiceObj> list = new ArrayList<ServiceObj>();
		int cardinality = 10;
		for(Op op: services){
			//int cardinality = (int) (Math.random() * 30);	//for now randomly generate cardinalites from 0 - 30
			list.add(new ServiceObj(op, cardinality));
			cardinality +=1;
		}
		return list;
	}
	

	/*
	 * This class will extract service calls from a algebra's query and put them in an array list.
	 * Use OpWalker to walk through a query using this OpVisitor.  
	 */
	public static class ServiceGrabber  implements OpVisitor {
	
		
		//private HashMap<Op, Integer> map;
		private ArrayList<Op> map;
			
		public ServiceGrabber(ArrayList<Op> m)	{
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
