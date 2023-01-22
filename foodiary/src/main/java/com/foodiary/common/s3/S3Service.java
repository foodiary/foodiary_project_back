package com.foodiary.common.s3;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    // dirName = 사진 저장할 폴더명
    public HashMap<String, String> upload(MultipartFile multipartFile, String dirName) throws IOException {

        // 파일 유효성 검사
        
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentType(multipartFile.getContentType());
        objMeta.setContentLength(multipartFile.getSize());

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = createStoreFileName(originalFilename);
        System.out.println("스토어 이름 확인 : " + storeFilename);

        String uploadImageUrl = putS3(multipartFile, dirName+"/"+storeFilename, objMeta);

        HashMap<String, String> fileMap = new HashMap<>();
        fileMap.put("serverName", storeFilename);
        fileMap.put("url", uploadImageUrl);

        log.info("파일 업로드 성공");
        return fileMap;

    }

    private String putS3(MultipartFile multipartFile, String fileName, ObjectMetadata objMeta) throws IOException{
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), objMeta)
            .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 서버에 저장되는 이름, uuid+밀리초
    private String createStoreFileName(String originalFilename) {
        
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        long currentMilliseconds = System.currentTimeMillis();

        return uuid + currentMilliseconds  + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    } 
    
    // 이미지 s3에서 삭제
    public void deleteImage(String url) {
        amazonS3Client.deleteObject(bucket, url);
    }

}
