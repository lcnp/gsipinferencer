package ca.nrcan.lms.gsc;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;


import software.amazon.awssdk.services.s3.model.S3Object;

public class Main {

	private static final String DEFAULT_BUCKET = "gin-ries-1";
	private static final String DEFAULT_INPUT_FOLDER = "triples";
	private static final String DEFAULT_OUTPUT_FOLDER = "infered";

	// https://docs.aws.amazon.com/AmazonS3/latest/dev/RetrievingObjectUsingJava.html 
	// https://stackoverflow.com/a/54494547/8691687 (mount ec3 bucket)
	// https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html
	// https://docs.docker.com/machine/examples/aws/
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		
		Options options = getOptions();
		CommandLineParser parser = new DefaultParser();
	
			CommandLine cmd;
			try {
				cmd = parser.parse(options,args);
				
				String bucketName = cmd.getOptionValue("i",null);
				String inputFolderName = cmd.getOptionValue("d",DEFAULT_INPUT_FOLDER);
				String outputFolderName = cmd.getOptionValue("o",DEFAULT_OUTPUT_FOLDER);
				// if we have a bucket, we are processing s3, otherwise, it's a regular folder
				
				if (bucketName != null)
					processS3(bucketName,inputFolderName,outputFolderName);
				else
					processLocal(inputFolderName,outputFolderName);

			} catch (ParseException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			

	}
	
	private static void processLocal(String inputFolderName,String outputFolderName) 
	{
		List<Path> files = null;
		try (Stream<Path> walk = Files.walk(Paths.get(inputFolderName))) 
		{
				files = walk.filter(Files::isRegularFile).collect(Collectors.toList());

		}
		catch (IOException e) {
		e.printStackTrace();
		}

		
		// load the files into a Model
		Model m = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RULES_INF);
		for(Path f :files)
			{
				String filename = f.toString();
				String ext = filename.substring(filename.lastIndexOf("."));
				if (".RDF".equalsIgnoreCase(ext) || ".TTL".equalsIgnoreCase(ext))
					{
						Logger.getAnonymousLogger().log(Level.INFO,"Loading " + filename);
						RDFDataMgr.read(m, filename);
							Logger.getAnonymousLogger().log(Level.INFO, " # loaded " + filename);
					}
						else
					System.out.println("skipping " + filename);
			}
		
		System.out.println("Done reading");
		
		/**
		StmtIterator stmts = m.listStatements();
		int c = 1000;
		while(stmts.hasNext())
		{
			Statement s = stmts.next();
			System.out.println(s);
			c--;
			if (c == 0) break;
		}
		**/
		

		
		
		
		// outputs the inferred statements to bucket
		String outfile = outputFolderName + "/infered_dataset.ttl";
		
		
		
		FileOutputStream fs;
		try {
			fs = new FileOutputStream(new File(outfile),false);
			RDFDataMgr.write(fs, m, Lang.TURTLE);
			// note, RDFDataMgr is chocking on GIN ontology
			fs.flush();
			//fs.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


		System.out.println("Done");
		



	}
	
	private static Options getOptions()
	{
		Options opt = new Options();
		opt.addOption("i",true,"bucket name");
		opt.addOption("d",true,"input folder name");
		opt.addOption("o",true,"output folder name");
		return opt;
		
				
		
	}
	
	/***
	 * pr
	 * @param cmd
	 */
	private static void processS3(String bucketName, String inputFolderName, String outputFolderName)
	{
		
		System.out.println(String.format("Processing data from %s/%s",bucketName,inputFolderName));
		AWSDataSource aws = new AWSDataSource();
		
		List<S3Object> s3obj = aws.getObjects(bucketName);
		if (s3obj.size() == 0)
			// nothing to do
		{
			System.err.println(String.format("Nothing to process in %s (%s)",bucketName,inputFolderName));
			return;
		}
		// load the files into a Model
		Model m = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RULES_INF);
		for(S3Object s :s3obj)
		{
			String k = s.key().toLowerCase();
			if (k.startsWith(inputFolderName) && k.endsWith(".ttl"))
			{
				
				RDFDataMgr.read(m,aws.getObjectInputStream(bucketName, k),Lang.TURTLE);
				Logger.getAnonymousLogger().log(Level.INFO, " # loaded " + k);
			}
			else
				System.out.println("skipping " + k);
		}
		
		// outputs the inferred statements to bucket
		String outkey = outputFolderName + "/infered_dataset.ttl";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		RDFDataMgr.write(baos, m, Lang.TURTLE);
		aws.upload(baos, bucketName, outkey);
	
		
	
	
	}
	
	

}
