package com.waitit.capstone.domain.manager.controller;

import com.waitit.capstone.domain.manager.service.QrcodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "QR코드 API", description = "QR코드 생성 관련 API")
public class QrCodeController {
    private final QrcodeService qrcodeService;

    @Operation(summary = "QR코드 생성", description = "주어진 URL로 QR코드를 생성하여 이미지로 반환합니다.")
    @GetMapping("/qrcode")
    public ResponseEntity<byte[]> generateQrCode(@RequestParam String url){
        try{
            byte[] qrCodeImage = qrcodeService.makeQrCode(url);

            final HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);

        }catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
