package com.fyp.reconciliation_automation.AwsBucket;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fyp.reconciliation_automation.service.MockMultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AwsOperations {
    @Value("${bucket_access_key}")
    private String accessKey;

    @Value("${bucket_secret_key}")
    private String secretKey;

    private AmazonS3 s3Client;

    @Value("${bucket_target}")
    private String bucketName;

    @Value("${aws_cloudfront_endpoint}")
    private String endpoint;

    private static final Pattern S3_URL_PATTERN = Pattern.compile(
            "^https://[a-zA-Z0-9.-]+\\.s3\\.[a-zA-Z0-9.-]+\\.amazonaws\\.com/.*$|" +
                    "^https://s3\\.[a-zA-Z0-9.-]+\\.amazonaws\\.com/[a-zA-Z0-9.-]+/.*$|" +
                    "^https://[a-zA-Z0-9.-]+\\.s3\\.amazonaws\\.com/.*$"
    );
    private static final Pattern CLOUDFRONT_URL_PATTERN = Pattern.compile(
            "^https://[a-zA-Z0-9.-]+\\.cloudfront\\.net/.*$"
    );
    
    private void initS3Client() {
        if (s3Client == null) {
            try {
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withRegion(Regions.US_EAST_2)
                        .build();
                log.info("S3 client initialized for bucket {} in region us-east-2", bucketName);
            } catch (Exception e) {
                log.error("Failed to initialize S3 client for bucket {}", bucketName, e);
                throw new RuntimeException("Unable to initialize S3 client: " + e.getMessage(), e);
            }
        }
    }

    public boolean isValidS3OrCloudFrontUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        boolean isS3Url = S3_URL_PATTERN.matcher(url).matches();
        boolean isCloudFrontUrl = CLOUDFRONT_URL_PATTERN.matcher(url).matches();
        if (!isS3Url && !isCloudFrontUrl) {
            log.warn("Invalid URL format (neither S3 nor CloudFront): {}", url);
            return false;
        }

        try {
            URL resourceUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);
            int responseCode = connection.getResponseCode();
            connection.disconnect();

            return responseCode == 200 || responseCode == 403;
        } catch (IOException e) {
            log.error("Error checking URL accessibility: {}", url, e);
            return false;
        }
    }

    public String uploadFile(File toUpload, String fileName) {
        initS3Client();
        String sanitizedFileName = sanitizeFileName(fileName);
        String url = null;
        String directory = "recon";
        String bucketKey = directory + "/" + sanitizedFileName;

        try {
            s3Client.putObject(bucketName, bucketKey, toUpload);
            url = endpoint + "recon/" + sanitizedFileName;
            log.info("File uploaded to S3: {}/{}", bucketName, bucketKey);
        } catch (Exception e) {
            log.error("Failed to upload file to S3: {}/{}", bucketName, bucketKey, e);
            throw new RuntimeException("Unable to upload file to S3: " + e.getMessage(), e);
        }

        return url;
    }

    public void deleteFile(String filePath, String fileName) {
        initS3Client();
        String sanitizedFileName = sanitizeFileName(fileName);
        String fileKey = filePath + "/" + sanitizedFileName;
        try {
            DeleteObjectsRequest delObjReq = new DeleteObjectsRequest(bucketName)
                    .withKeys(fileKey);
            s3Client.deleteObjects(delObjReq);
            log.info("File deleted from S3: {}/{}", bucketName, fileKey);
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}/{}", bucketName, fileKey, e);
            throw new RuntimeException("Unable to delete file from S3: " + e.getMessage(), e);
        }
    }

    public MultipartFile downloadFromS3(String url) throws IOException {
        initS3Client();
        try {
            if (CLOUDFRONT_URL_PATTERN.matcher(url).matches()) {
                return downloadFromCloudFront(url);
            }

            AmazonS3URI s3URI = new AmazonS3URI(url);
            String bucket = s3URI.getBucket();
            String key = s3URI.getKey();

            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucket, key));

            String filename = key.contains("/") ? key.substring(key.lastIndexOf("/") + 1) : key;

            String contentType = s3Object.getObjectMetadata().getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = determineContentType(filename);
            }

            byte[] content = s3Object.getObjectContent().readAllBytes();
            return new MockMultipartFile(
                    filename,
                    filename,
                    contentType,
                    content
            );
        } catch (Exception e) {
            log.error("Failed to download file from S3/CloudFront: {}", url, e);
            throw new IOException("Failed to download file from S3/CloudFront: " + e.getMessage());
        }
    }

    private MultipartFile downloadFromCloudFront(String url) throws IOException {
        URL resourceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);

        try (InputStream inputStream = connection.getInputStream()) {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            File tempFile = Files.createTempFile("cloudfront-", "-" + fileName).toFile();
            Files.copy(inputStream, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            byte[] content = Files.readAllBytes(tempFile.toPath());
            String contentType = determineContentType(fileName);
            MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, contentType, content);

            tempFile.delete();

            return multipartFile;
        } finally {
            connection.disconnect();
        }
    }

    private String determineContentType(String filename) {
        if (filename.toLowerCase().endsWith(".csv")) {
            return "text/csv";
        } else if (filename.toLowerCase().endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (filename.toLowerCase().endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else {
            return "application/octet-stream";
        }
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        String sanitized = fileName
                .replaceAll("\\s+", "_")
                .replaceAll("[()\\[\\]{}\\|<>\\\\]", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "_");
        sanitized = sanitized.replaceAll("_+", "_");
        sanitized = sanitized.replaceAll("^_+|_+$", "");
        log.debug("File name sanitized from '{}' to '{}'", fileName, sanitized);
        return sanitized;
    }
}