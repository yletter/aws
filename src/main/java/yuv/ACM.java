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
public static void main(String profile, FileWriter csvWriter, Region region) {

// Create ACM client

AcmClient acmClient = AcmClient.builder().region(region)

.credentialsProvider(ProfileCredentialsProvider.builder().profileName(profile).build()).build();

// Retrieve ACM certificates

ListCertificatesResponse listCertificatesResponse = acmClient.listCertificates();

List<CertificateSummary> certificateSummaries =

listCertificatesResponse.certificateSummaryList();

try {

// Write certificate metadata to CSV

for (CertificateSummary certificateSummary: certificateSummaries) {

DescribeCertificateResponse describeCertificateResponse = acmClient.describeCertificate(

DescribeCertificateRequest.builder()

.certificateArn(certificateSummary.certificateArn())

.build()

);

CertificateDetail certificateDetail = describeCertificateResponse.certificate();

csvWriter.append(String.format("%5, %s, %s, %s, %s, %s, %s, %s, %s\n", profile.replaceAll("blue", ""),

region.id(),

certificateDetail.certificateArn(),

certificateDetail.domainName(),

certificateDetail.statusAsString(),

certificateDetail.typeAsString(), certificateDetail.hasInUseBy(),

certificateDetail.issuedAt(),

certificateDetail.notAfter()

));

System.out.println("ACM certificates metadata exported -" + profile);

}

} catch (IOException e) {

e.printStackTrace();

}

}

}