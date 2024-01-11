package hello;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MultiPartUpload {

    public static void main(String[] args) {
        String bucketName = "yuvarajmultipart";
        String keyName = "City.mp3";
        String filePath = "Songs";

        Region region = Region.US_EAST_1; // Replace with your desired region

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build()) {

            File file = new File(filePath);
            long contentLength = file.length();
            long partSize = 1 * 1024 * 1024; // 5MB part size (adjust as needed)

            CreateMultipartUploadResponse createMultipartUploadResponse = s3Client.createMultipartUpload(
                    CreateMultipartUploadRequest.builder()
                            .bucket(bucketName)
                            .key(keyName)
                            .build());

            String uploadId = createMultipartUploadResponse.uploadId();

            List<CompletedPart> completedParts = new ArrayList<>();

            try {
                long filePosition = 0;
                for (int i = 1; filePosition < contentLength; i++) {
                    long partSizeBytes = Math.min(partSize, contentLength - filePosition);

                    UploadPartResponse uploadPartResponse = s3Client.uploadPart(
                            UploadPartRequest.builder()
                                    .bucket(bucketName)
                                    .key(keyName)
                                    .uploadId(uploadId)
                                    .partNumber(i)
                                    .contentLength(partSizeBytes)
                                    .build(),
                            RequestBody.fromFile(file, filePosition, partSizeBytes));

                    System.out.println("Part " + i + " uploaded successfully. ETag: " + uploadPartResponse.eTag());

                    completedParts.add(CompletedPart.builder()
                            .partNumber(i)
                            .eTag(uploadPartResponse.eTag())
                            .build());

                    filePosition += partSizeBytes;
                }

                CompleteMultipartUploadResponse completeMultipartUploadResponse = s3Client.completeMultipartUpload(
                        CompleteMultipartUploadRequest.builder()
                                .bucket(bucketName)
                                .key(keyName)
                                .uploadId(uploadId)
                                .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                                .build());

                System.out.println("Multipart upload completed. Object URL: " +
                        s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(keyName)));

            } catch (Exception e) {
                s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .uploadId(uploadId)
                        .build());
                throw e;
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}


