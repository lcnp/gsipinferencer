package ca.nrcan.lms.gsc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * load data from a AWS bucket
 * @author eboisver
 *
 */
public class AWSDataSource {
	public static final String GIN_BUCKET = "gin-ries-1";
	private static S3Client s3;
	private Region region = Region.CA_CENTRAL_1;
	
	
	public AWSDataSource()
	{
		s3 = S3Client.builder().region(region).build();
		
	}
	
	
	public List<Bucket> getBuckets()
	{
		ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
		ListBucketsResponse listBucketsResponse = s3.listBuckets(listBucketsRequest);
		return listBucketsResponse.buckets();
	}
	
	/**
	 * get the list of objects in a bucket
	 * we are interested in gin-ries-1
	 * @param b
	 * @return
	 */
	public List<S3Object> getObjects(String b)
	{
		ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(b).build();
		ListObjectsResponse res = s3.listObjects(listObjects);
		return res.contents();
		
	}
	
	public List<S3Object> getObjectInFolder(String bucket,String folder)
	{
	
		ListObjectsRequest lisObjectsRequest = ListObjectsRequest.builder().bucket(bucket).prefix(folder + "/").build();
		ListObjectsResponse res = s3.listObjects(lisObjectsRequest);
		return res.contents();
		
	}
	
	public List<S3Object> getObjects(Bucket b)
	{
		return getObjects(b.name());
	}

	public String getObjectContent(String bucket,String key)
	{
		GetObjectRequest gor = GetObjectRequest.builder().bucket(bucket).key(key).build();
		return s3.getObject(gor, ResponseTransformer.toBytes()).asUtf8String();
	}
	
	public InputStream getObjectInputStream(String bucket,String key)
	{
		GetObjectRequest gor = GetObjectRequest.builder().bucket(bucket).key(key).build();
		return s3.getObject(gor, ResponseTransformer.toInputStream());
	}
	
	/**
	 * upload a byte array
	 * @param baos
	 */
	public void upload(ByteArrayOutputStream baos,String bucket,String key)
	{
		PutObjectRequest por = PutObjectRequest.builder().bucket(bucket).key(key).build();
		s3.putObject(por, RequestBody.fromBytes(baos.toByteArray()));
	}
	
}
