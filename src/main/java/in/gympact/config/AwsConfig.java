package in.gympact.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsConfig {

	@Value("${secretKey}")
	private String secretKey;

	@Value("${accessKey}")
	private String accessKey;
	
	@Bean
	public AmazonS3 getClient() {
		BasicAWSCredentials cred = new BasicAWSCredentials(accessKey, secretKey);
		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(cred)).withRegion(Regions.AP_SOUTHEAST_2).build();
	}
}
