package com.example.toyprojectback.service;

import com.example.toyprojectback.entity.Board;
import com.example.toyprojectback.entity.UploadImage;
import com.example.toyprojectback.repository.BoardRepository;
import com.example.toyprojectback.repository.UploadImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UploadImageService {

    private final UploadImageRepository uploadImageRepository;
    private final BoardRepository boardRepository;
    private final String rootPath = System.getProperty("user.dir");
    private final String fileDir = rootPath + "/src/main/resources/static/upload-images";

    public String getFullPath(String fileName) {
        return fileDir + "/" + fileName;

    }

    public UploadImage saveImage(MultipartFile multipartFile, Board board)throws IOException {
        if (multipartFile.isEmpty()) {
            return null;

        }
        String originalFilename = multipartFile.getOriginalFilename();
        // 원본 파일명 -> 서버에 저장된 파일명 ( 중복 X )
        // 파일명이 중복되지 않도록 UUID 설정 + 확장자 유지
        String savedFilename = UUID.randomUUID() + "." + extractExt(originalFilename);

        // 파일 저장
        multipartFile.transferTo(new File(getFullPath(savedFilename)));
        return uploadImageRepository.save(UploadImage.builder()
                .originalFilename(originalFilename)
                .savedFilename(savedFilename)
                .board(board)
                .build());
    }

    @Transactional
    public void deleteImage (UploadImage uploadImage) throws IOException {
        uploadImageRepository.delete(uploadImage);
        Files.deleteIfExists(Paths.get(getFullPath(uploadImage.getOriginalFilename())));
    }

    //확장자 추출

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf("."); // 마지막 . 위치 찾기 (확장자 앞 . 위치)
        return originalFilename.substring(pos + 1); // 확장자 추출
    }

    public ResponseEntity<UrlResource> downloadImage(Long boardId) throws MalformedURLException {
        Board board = boardRepository.findById(boardId).get();
        if(board ==null || board.getUploadImage() == null) {
            return null;
        }

        UrlResource urlResource = new UrlResource("file:" + getFullPath(board.getUploadImage().getSavedFilename()));

        String encodedUploadFileName = UriUtils.encode(board.getUploadImage().getOriginalFilename(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        //header CONTENT_DISPOSITION에 설정을 통해 클릭시 다운로드 진행

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);

    }
}

//saveImage() 메소드에서 입력된 파일 이름을 UUID+원본파일 확장자로 바꿔서 저장
//downloadImage() 메소드에는 다시 원본 파일명으로 수정 후 파일 return
//이 프로젝트에서 파일 업로드 방식은 로컬 프로젝트에 "/src/main/resources/static/upload-images 폴더를 추가 후 해당 폴더에 이미지를 업로드 하는 방식
// 따라서 해당 경로에 폴더가 없으면 에러가 발생할 수 있기 때문에 직접 폴더를 추가 해줘야 한다
// 만약 이 프로젝트를 EC2 서버로 배포하고 싶다면 EC2 인스턴스 프로젝트 내부에서 파일을 관리해도 되지만 S3 버킷을 사용하는 것이 더 안전하다

