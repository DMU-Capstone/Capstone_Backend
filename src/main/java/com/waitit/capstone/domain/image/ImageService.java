package com.waitit.capstone.domain.image;

import com.waitit.capstone.global.util.PageResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class ImageService {
    private final EventImageRepository eventImageRepository;
    private final ImageMapper imageMapper;
    public void uploadEvent(List<MultipartFile> images){
        try{
            //이미지 파일 저장을 위한 경로 설정
            String uploadDir = "src/main/resources/static/uploads/events/";

            //각 이미지 파일에 대해 업로드 및 db 저장 수행
            for(MultipartFile image : images){
                String dbFilePath = saveImage(image,uploadDir);
                EventImage eventImage = new EventImage(dbFilePath);
                eventImageRepository.save(eventImage);
            }

        }catch (IOException e) {
            // 파일 저장 중 오류가 발생한 경우 처리
            e.printStackTrace();
        }
    }

    private String saveImage(MultipartFile image, String uploadDir) throws IOException {
        //파일 이름 생성
        String fileName = UUID.randomUUID().toString().replace("-","")+"_"+image.getOriginalFilename();
        //실제 파일이 저장될 경로
        String filePath = uploadDir + fileName;
        //db에 저장할 경로 문자열
        String dbFilePath = "/uploads/events/" + fileName;

        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path,image.getBytes());

        return dbFilePath;
    }

    public PageResponse<AllImageResponse> getAllImage(Pageable pageable){
        Page<EventImage> eventImages = eventImageRepository.findAll(pageable);
        Page<AllImageResponse> page = eventImages.map(imageMapper::toAllImageResponse);
        return new PageResponse<>(page);
    }
    public String getImgPath(Long id){
      EventImage eventImage =  eventImageRepository.findById(id).orElseThrow(()->new IllegalArgumentException("없는 이미지 입니다."));
      return eventImage.getImgPath();
    }
}
