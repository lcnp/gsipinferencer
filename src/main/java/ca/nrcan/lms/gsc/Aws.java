package ca.nrcan.lms.gsc;

import java.util.List;

import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;

public class Aws {

	public static void main(String[] args) {
		System.out.println("Trying AWS code");
		//System.out.println(System.getenv("AWS_ACCESS_KEY_ID"));
		AWSDataSource aws = new AWSDataSource();
		List<Bucket> buckets = aws.getBuckets();
		for(Bucket b:buckets)
		{
			System.out.println(b.name());
		}
		
		/**
		List<S3Object> objs = aws.getObjects(AWSDataSource.GIN_BUCKET);
		for (S3Object o:objs)
		{
			String k = o.key();
			// is it a ttl file in triples ?
			if (k.startsWith("triples/") && k.endsWith(".ttl"))
			{
				// get the content
				String ttl = aws.getObjectContent(AWSDataSource.GIN_BUCKET, k);
				System.out.println(ttl);
				
			}
		}
		**/
		
		

	}

}
