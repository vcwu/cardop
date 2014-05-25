package rdf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.algebra.optimize.Optimize;
import com.hp.hpl.jena.sparql.mgt.Explain;

public class ExecuteQuery {
	public static void main(String args[]){
		//org.apache.jena.atlas.logging.Log.setLog4j();
		//BasicConfigurator.configure();
		//ARQ.setExecutionLogging(Explain.InfoLevel.ALL) ;
		
		//Optimize.noOptimizer();
		
		
		//Query query = QueryFactory.read("file:/Users/vptarmigan/anapsid_old/federatedActor_sparql11.query");
		//Query query = QueryFactory.read("file:/Users/vptarmigan/dropbox/rdf_queries/2_db_imdb_schwarz.query");
		
		
		Query query = QueryFactory.read("file:/Users/vptarmigan/dropbox/rdf_queries/star_trek_2/star_trek.query");
		String cardinality_file = "/Users/vptarmigan/dropbox/rdf_queries/star_trek_2/star_trek.cardinalities";
		
		//Parse given cardinalities - done outside for timing and testing purposes
		ArrayList<ServiceObj> givenCardinalities = CardOp.parseCardinalityFile(cardinality_file);	
		System.out.println(givenCardinalities);
		//Reorder using the given cardinalities
		//query = CardOp.reorder(query, givenCardinalities); 
		//execute(query);
		
		
		
	}
	
	public static void execute(Query query)	{	
		Model model = ModelFactory.createDefaultModel();
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		System.out.println("QueryExecutionFactory create - > \n" + qexec.toString());
		
		ResultSet results = qexec.execSelect();
		System.out.println(ResultSetFormatter.asText(results));
	}
	
}
