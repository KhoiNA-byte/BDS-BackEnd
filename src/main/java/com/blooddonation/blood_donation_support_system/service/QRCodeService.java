package com.blooddonation.blood_donation_support_system.service;

import com.google.zxing.WriterException;
import java.io.IOException;

public interface QRCodeService {
    byte[] generateQRCode(String content) throws WriterException, IOException;
}