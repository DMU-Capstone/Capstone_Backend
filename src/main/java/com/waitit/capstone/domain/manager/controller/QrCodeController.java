package com.waitit.capstone.domain.manager.controller;

import com.waitit.capstone.domain.manager.service.QrcodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QrCodeController {
    private final QrcodeService qrcodeService;

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
