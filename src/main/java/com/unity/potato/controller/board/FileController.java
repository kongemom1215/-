package com.unity.potato.controller.board;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.unity.potato.dto.response.Result;
import com.unity.potato.util.EnvUtil;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
@Slf4j
public class FileController {

    private final String uploadDir = Paths.get("C:", "tui-editor", "upload").toString();
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final EnvUtil envUtil;

    @PostMapping("/image-upload")
    public ResponseEntity<?> uploadEditorImage(@RequestParam final MultipartFile image){
        try {
            if(image.isEmpty()){
                return ResponseEntity.ok(new Result("9999", "이미지를 요청해주세요."));
            }
            String originFilename = image.getOriginalFilename();
            String extName = originFilename.substring(originFilename.lastIndexOf(".") + 1);
            String saveFileName = StringUtil.createSaveFileName(extName);

            if(envUtil.isEqualProfile("local")){
                String fileFullPath = Paths.get(uploadDir, saveFileName).toString();
                File dir = new File(uploadDir);
                if (dir.exists() == false) {
                    dir.mkdirs();
                }

                File uploadFile = new File(fileFullPath);
                image.transferTo(uploadFile);

                return ResponseEntity.ok(new Result("0000", "이미지 업로드 성공", saveFileName));
            } else if(envUtil.isEqualProfile("prd")){
                File file = convertMultiPartToFile(image);
                amazonS3.putObject(new PutObjectRequest(bucketName, saveFileName, file)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                file.delete();

                return ResponseEntity.ok(new Result("0000", "이미지 업로드 성공", saveFileName));
            }

            return ResponseEntity.ok(new Result("9999", "올바른 환경에서 요청해주세요."));
        } catch (MaxUploadSizeExceededException e){
            return ResponseEntity.ok(new Result("9997", "10MB 이하의 이미지를 요청해주세요."));
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9996", "이미지 업로드 요청 중 오류가 발생하였습니다."));
        }
    }

    @GetMapping(value = "/image-print", produces = { MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE })
    public byte[] printEditorImage(@RequestParam final String filename){
        try {
            if(envUtil.isEqualProfile("local")){
                String fileFullPath = Paths.get(uploadDir, filename).toString();

                File uploadedFile = new File(fileFullPath);
                if(!uploadedFile.exists()){
                    throw new RuntimeException("file not found");
                }
                byte[] imageBytes = Files.readAllBytes(uploadedFile.toPath());
                return imageBytes;
            } else if(envUtil.isEqualProfile("prd")){
                S3Object s3Object = amazonS3.getObject(bucketName, filename);
                try (InputStream inputStream = s3Object.getObjectContent()) {
                    return inputStream.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException("Error reading file from S3", e);
                }
            }
        } catch (Exception e){
            throw new RuntimeException("Error reading file");
        }
        throw new RuntimeException("Error reading file from S3");
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
