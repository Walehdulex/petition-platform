package com.example.petitionplatform.service;


import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Set;

@Service
public class BioValidationService {
    private static final Set<String> VALID_BIOIDS = Set.of(
            "K1YL8VA2HG", "7DMPYAZAP2", "D05HPPQNJ4" /* ... add all valid BioIDs */
    );

    public boolean isValidBioId(String bioId) {
        return VALID_BIOIDS.contains(bioId);
    }

    public String decodeBioIdQR(byte[] qrCodeImage) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(qrCodeImage));
            BinaryBitmap binaryBitmap = new BinaryBitmap(
                    new HybridBinarizer(
                            new BufferedImageLuminanceSource(bufferedImage)
                    )
            );

            Result result = new MultiFormatReader().decode(binaryBitmap);
            String decodedBioId = result.getText();

            return isValidBioId(decodedBioId) ? decodedBioId : null;
        } catch (Exception e) {
            return null;
        }
    }
}
