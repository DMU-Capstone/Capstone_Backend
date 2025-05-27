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
            String uploadDir = "/home/ubuntu/app/uploads/events/";
            for(MultipartFile image : images){
                if (image.isEmpty()) {
                    System.err.println("[경고] 업로드된 이미지가 비어 있음: " + image.getOriginalFilename());
                    continue;
                }

                String dbFilePath = saveImage(image, uploadDir, "events");
                EventImage eventImage = new EventImage(dbFilePath);
                eventImageRepository.save(eventImage);
                System.out.println("[성공] 저장됨: " + dbFilePath);
            }

        } catch (IOException e) {
            System.err.println("[오류] 이미지 저장 실패: " + e.getMessage());
            throw new RuntimeException("이미지 저장 실패", e);
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
        String dbFilePath = "/app/uploads/" + dir + "/" + fileName;

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
