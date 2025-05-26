package com.waitit.capstone.domain.image;

import com.waitit.capstone.domain.image.entity.EventImage;
import com.waitit.capstone.domain.image.entity.HostImage;
import com.waitit.capstone.domain.image.repository.EventImageRepository;
import com.waitit.capstone.domain.image.repository.HostImageRepository;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.global.util.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
@Transactional
public class ImageService {
    private final HostImageRepository hostImageRepository;
    private final HostRepository hostRepository;
    private final EventImageRepository eventImageRepository;
    private final ImageMapper imageMapper;
    public void uploadEvent(List<MultipartFile> images){
        try{
            //이미지 파일 저장을 위한 경로 설정
            String uploadDir = "/home/ubuntu/app/uploads/events/";
            //각 이미지 파일에 대해 업로드 및 db 저장 수행
            for(MultipartFile image : images){
                String dbFilePath = saveImage(image,uploadDir,"events");
                EventImage eventImage = new EventImage(dbFilePath);
                eventImageRepository.save(eventImage);
            }

        }catch (IOException e) {
            // 파일 저장 중 오류가 발생한 경우 처리
            e.printStackTrace();
        }
    }
    public HostImage uploadHost(Long id, MultipartFile image) throws IOException{
        Host host = hostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Host not found: " + id));
        String uploadDir = "/home/ubuntu/app/uploads/hosts/";
        // 1) 파일 저장
        String dbFilePath = saveImage(image, uploadDir, "hosts");

        // 2) HostImage 생성 (host 연관관계도 세팅)
        HostImage img = HostImage.of(host, dbFilePath);

        // 3) 반환만, 실제 저장은 cascade로 처리
        return img;
    }

    private String saveImage(MultipartFile image, String uploadDir,String dir) throws IOException {
        //파일 이름 생성
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + image.getOriginalFilename();
        String filePath = uploadDir + fileName;
        String dbFilePath = "/uploads/" + dir + "/" + fileName;

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
