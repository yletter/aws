package yuv;

import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.acm.AcmClient;

import software.amazon.awssdk.services.acm.model.*;

import java.io.FileWriter;

import java.io.IOException;

import java.util.List;

import software.amazon.awssdk.auth.credentials. ProfileCredentialsProvider;

public class ACMCertificates Metadata ToCSV {

public static void main(String[] args) {

/*

awsprompt

awsstageread

*/

// Create CSV file

String csvFilePath = "acm_certificates_metadata.csv";

FileWriter csvWriter = null;

try {

csvWriter = new FileWriter(csvFilePath);

// Write CSV header

t, Region, CertificateArn, Domain Name, Status, Type, InUse, IssuedAt, NotAfter\n"); csvWriter.append("Environment, Region, CertificateArn, Domain Name,

main("stageblue", csvWriter, Region. US_EAST_1);

main("stageblue", csvWriter, Region. US_EAST_2);

} catch (IOException e) {

e.printStackTrace();

} finally {

try {

csvWriter.close();

} catch (IOException e) {

e.printStackTrace();

}
}
}