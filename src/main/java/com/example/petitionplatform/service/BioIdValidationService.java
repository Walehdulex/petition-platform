package com.example.petitionplatform.service;


import com.example.petitionplatform.exception.BioIdInvalidException;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Set;

@Service
public class BioIdValidationService {
    private static final Set<String> VALID_BIOIDS = Set.of(
            "K1YL8VA2HG", "7DMPYAZAP2", "D05HPPQNJ4", "2WYIM3QCK9", "DHKFIYHMAZ",
            "LZK7P0X0LQ", "H5C98XCENC", "6X6I6TSUFG", "QTLCWUS8NB", "Y4FC3F9ZGS",
            "V30EPKZQI2", "O3WJFGR5WE", "SEIQTS1H16", "X16V7LFHR2", "TLFDFY7RDG",
            "PGPVG5RF42", "FPALKDEL5T", "2BIB99Z54V", "ABQYUQCQS2", "9JSXWO4LGH",
            "QJXQOUPTH9", "GOYWJVDA8A", "6EBQ28A62V", "30MY51J1CJ", "FH6260T08H",
            "JHDCXB62SA", "O0V55ENOT0", "F3ATSRR5DQ", "1K3JTWHA05", "FINNMWJY0G",
            "CET8NUAE09", "VQKBGSE3EA", "E7D6YUPQ6J", "BPX8O0YB5L", "AT66BX2FXM",
            "1PUQV970LA", "CCU1D7QXDT", "TTK74SYYAN", "4HTOAI9YKO", "PD6XPNB80J",
            "BZW5WWDMUY", "340B1EOCMG", "CG1I9SABLL", "49YFTUA96K", "V2JX0IC633",
            "C7IFP4VWIL", "RYU8VSS4N5", "S22A588D75", "88V3GKIVSF", "8OLYIE2FRC" /* ... add all valid BioIDs */
    );

    public boolean isValidBioId(String bioId) {
        return VALID_BIOIDS.contains(bioId);
    }

    public void validateBioId(String bioId) {
        if (!isValidBioId(bioId)) {
            throw new BioIdInvalidException("Invalid BioID");
        }
    }

    public String decodeBioIdQR(byte[] qrCodeImage) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(qrCodeImage));
            BinaryBitmap binaryBitmap = new BinaryBitmap(
                    new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage))
            );

            QRCodeReader qrCodeReader = new QRCodeReader();
            Result result = qrCodeReader.decode(binaryBitmap);

            String decodedBioId = result.getText();

            return isValidBioId(decodedBioId) ? decodedBioId : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
