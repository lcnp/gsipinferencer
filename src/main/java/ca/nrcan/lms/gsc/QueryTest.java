package ca.nrcan.lms.gsc;



import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.query.ResultSetFormatter;

public class QueryTest {
public static final String INPUT = "c:\\ludlum\\test\\ont.ttl";
public static final String SP1 = "PREFIX gsip: <https://geoconnex.ca/def/>\r\n" + 
"PREFIX g: <https://geoconnex.ca/individual/>\r\n" + 
"SELECT ?a\r\n" + 
"WHERE {?a gsip:relate g:InstanceA}";
public static final String SP2 = "PREFIX gsip: <https://geoconnex.ca/def/>\r\n" + 
"PREFIX g: <https://geoconnex.ca/individual/>\r\n" + 
"SELECT ?a\r\n" + 
"WHERE {?a gsip:precede g:InstanceC}";
public static final String SP3 = "PREFIX gsip: <https://geoconnex.ca/def/>\r\n" + 
"PREFIX g: <https://geoconnex.ca/individual/>\r\n" + 
"select ?a ?b\r\n" + 
"WHERE {?a gsip:succede ?b}";

/**
* one off tool to load a file and perform queries and output the result to stdout
* 
* @param args
* @throws FileNotFoundException 
*/
public static void main(String[] args) throws FileNotFoundException {
// TODO Auto-generated method stub
//Model m = RDFDataMgr.loadModel(INPUT,Lang.TURTLE);
Model m = ModelFactory.createOntologyModel( OntModelSpec.OWL_LITE_MEM_RULES_INF );
m.read(new FileInputStream(INPUT),null,"TTL");
execute(m,SP1);
execute(m,SP2);
execute(m,SP3);

// output everything
RDFDataMgr.write(System.out, m, Lang.TURTLE);



}

private static void execute(Model m, String sparql)
{
// read from a file
System.out.println(sparql); 

Query query = QueryFactory.create(sparql) ;
QueryExecution qexec = QueryExecutionFactory.create(query, m) ;
ResultSet resultModel = qexec.execSelect();

ResultSetFormatter.out(System.out, resultModel, query) ;

}

}

