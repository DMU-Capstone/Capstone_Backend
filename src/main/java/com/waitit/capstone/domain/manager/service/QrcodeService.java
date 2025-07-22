package com.waitit.capstone.domain.manager.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QrcodeService {


    public byte[] makeQrCode(String url)throws WriterException, IOException {

        // QR Code - BitMatrix: qr code 정보 생성
        int WIDTH = 200;
        int HEIGHT = 200;
        BitMatrix encode  = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);

        try{

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(encode, "PNG", out);
            return out.toByteArray();

        }catch (Exception e){
            log.warn("QR Code OutputStream 도중 Excpetion 발생, {}", e.getMessage());
            throw e;
        }
    }
}
