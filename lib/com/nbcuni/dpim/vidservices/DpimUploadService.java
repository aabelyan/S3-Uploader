/*
 *	Hans K Formon
 *	DpimUploadService.java
 *	NBCUniversal	
 *	2014-01-07
 *
 *	This class acts as the Upload service of DPIM's set of video services. It leverages
 *	Amazon's Java SDK for AWS to provide various functionality related to uploading
 *	and managing files in Amazon S3.
 */
 
package com.nbcuni.dpim.vidservices;
 
import java.util.List;
import java.io.File;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
 
public class DpimUploadService {
	
	private static AmazonS3 s3;
	
	public DpimUploadService() {
		AWSCredentialsProvider credentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
		if (s3 == null)
			s3 = new AmazonS3Client(credentialsProvider);
	}
	
	public List<Bucket> getBuckets() {
		return s3.listBuckets();
	}
	
	public PutObjectResult putObject(String bucketName , String key , File file) {
		return s3.putObject(new PutObjectRequest(bucketName , key , file));
	}
	
	public S3Object getObject(String bucketName , String key) {
		return s3.getObject(new GetObjectRequest(bucketName , key));
	}
}