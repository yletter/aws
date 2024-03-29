package yuv;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.*;
/*

aws s3api list-multipart-uploads --bucket yuvarajmultipart --profile default
aws s3api abort-multipart-upload --bucket yuvarajmultipart --key City.mp3 --upload-id Pahhwp1BFerdHZIojJxKXFAfxYsgNLVSz6WUINMyXcg36BivcGm5nxC46xVXyeM7Ty_JLvDzD9w4ECGs9qS.eB1SJfz97KQM_J4Am_PE.dFf2AnyJtSK5ta6MeF_R8Rt --profile default
aws s3api list-multipart-uploads --bucket yuvarajmultipart --profile default --query "Uploads[*].[UploadId,Key]" --output text

*/
public class MultipartUpload {

    public static void main(String[] args) {
        String bucketName = "yuvarajmultipart";
        String keyName = "City.mp3";
        String filePath = "Songs";

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .build();

        // Initiate a multipart upload
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(keyName)
            .build();

        CreateMultipartUploadResponse createResponse = s3.createMultipartUpload(createRequest);

        String uploadId = createResponse.uploadId();

        // Prepare the parts to be uploaded
        List<CompletedPart> completedParts = new ArrayList<>();
        int partNumber = 1;
        ByteBuffer buffer = ByteBuffer.allocate(5 * 1024 * 1024); // Set your preferred part size (5 MB in this example)

        // Read the file and upload each part
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            long fileSize = file.length();
            long position = 0;

            while (position < fileSize) {
                file.seek(position);
                int bytesRead = file.getChannel().read(buffer);

                buffer.flip();
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .contentLength((long) bytesRead)
                    .build();

                UploadPartResponse response = s3.uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(buffer));

                completedParts.add(CompletedPart.builder()
                    .partNumber(partNumber)
                    .eTag(response.eTag())
                    .build());

                buffer.clear();
                position += bytesRead;
                partNumber++;
                if (partNumber < 10) 
                    break; // interrupt
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Complete the multipart upload
        CompletedMultipartUpload completedUpload = CompletedMultipartUpload.builder()
            .parts(completedParts)
            .build();

        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(keyName)
            .uploadId(uploadId)
            .multipartUpload(completedUpload)
            .build();

        CompleteMultipartUploadResponse completeResponse = s3.completeMultipartUpload(completeRequest);

        // Print the object's URL
        String objectUrl = s3.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build())
            .toExternalForm();

        System.out.println("Uploaded object URL: " + objectUrl);
    }
}
