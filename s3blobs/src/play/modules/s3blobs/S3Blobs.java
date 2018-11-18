package play.modules.s3blobs;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.BucketNameUtils;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketPolicy;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.exceptions.ConfigurationException;

public class S3Blobs extends PlayPlugin {

	@Override
	public void onApplicationStart() {
		if (!ConfigHelper.getBoolean("s3.storage.enabled", true)) {
			Logger.info("S3Blobs module disabled");
			return;
		}		

		Logger.info("Starting the S3Blobs module");
		
		if (!Play.configuration.containsKey("aws.access.key")) {
			throw new ConfigurationException("Bad configuration for s3: no access key");
		} else if (!Play.configuration.containsKey("aws.secret.key")) {
			throw new ConfigurationException("Bad configuration for s3: no secret key");
		} else if (!Play.configuration.containsKey("s3.bucket")) {
			throw new ConfigurationException("Bad configuration for s3: no s3 bucket");
        } else if (!Play.configuration.containsKey("s3.region")) {
            throw new ConfigurationException("Bad configuration for s3: no s3 region");
        }

		String region = Play.configuration.getProperty("s3.region");
		S3Blob.s3Bucket = Play.configuration.getProperty("s3.bucket");
		S3Blob.serverSideEncryption = ConfigHelper.getBoolean("s3.useServerSideEncryption", false);
		String accessKey = Play.configuration.getProperty("aws.access.key");
		String secretKey = Play.configuration.getProperty("aws.secret.key");
		BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
		Regions regions = Regions.fromName(region);
		
		// Due to trouble with connections, allow the overriding of these connection values from config 
		ClientConfiguration config = new ClientConfiguration();
		config.setConnectionTimeout(ConfigHelper.getInt("s3.clientConfiguration.connectionTimeout", 5000)); // 5 seconds;
		config.setConnectionTTL(ConfigHelper.getInt("s3.clientConfiguration.connectionTTL", 120000)); // 2 minutes
		config.setMaxConnections(ConfigHelper.getInt("s3.clientConfiguration.maxConnections", 75));
		
		S3Blob.s3Client = AmazonS3ClientBuilder.standard().withClientConfiguration(config).withCredentials(new AWSStaticCredentialsProvider(creds)).withRegion(regions).build();
		if (!S3Blob.s3Client.doesBucketExistV2(S3Blob.s3Bucket)) {
			S3Blob.s3Client.createBucket(S3Blob.s3Bucket);
		}
	}
}
