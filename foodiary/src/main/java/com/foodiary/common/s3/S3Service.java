package com.foodiary.common.s3;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
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
        

        File uploadFile = convert(multipartFile, dirName)
                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
        return upload(uploadFile, dirName);

    }

    private HashMap<String, String> upload(File uploadFile, String dirName) {
        // fileName = 폴더명 / 파일명
        String fileName = dirName + "/" + uploadFile.getName();
        System.out.println("파일 이름 확인 : " + fileName);
        String uploadImageUrl = putS3(uploadFile, fileName);
        System.out.println("업로드 url : "+ uploadImageUrl);
        removeNewFile(uploadFile);

        HashMap<String, String> fileMap = new HashMap<>();
        fileMap.put("serverName", uploadFile.getName());
        fileMap.put("url", uploadImageUrl);
        
        return fileMap;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
            .withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if(targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }


    private Optional<File> convert(MultipartFile multipartFile, String filePath) throws IOException {
        if(multipartFile.isEmpty()) {
            return Optional.empty();
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = createStoreFileName(originalFilename);
        System.out.println("스토어 이름 확인 : " + storeFilename);

        File file = new File(System.getProperty("user.dir")+"/src/main/resources/static/"+filePath+"/"+storeFilename);
        // File file = new File(System.getProperty("user.dir")+"/src/main/resources/static/"+storeFilename);

        multipartFile.transferTo(file);

        return Optional.of(file);
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

    // public ResponseEntity<?> checkFile(MultipartFile multipartFile) throws Exception {


    // }

}
