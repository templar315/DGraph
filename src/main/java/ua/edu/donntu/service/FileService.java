package ua.edu.donntu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.edu.donntu.service.exceptions.FileSaveException;
import ua.edu.donntu.service.exceptions.MessageDigestException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileService {

    private static final String FILES_VOLUME = "/var/lib/dgraph";

    public String getHash(byte[] fileArray, String digest) throws MessageDigestException {
        log.debug("Request to generate {} hash of byte array", digest);
        try {
            return String.format("%02x", new BigInteger(1, MessageDigest.getInstance(digest).digest(fileArray)));
        } catch (NoSuchAlgorithmException exception) {
            log.error("Message digest error: ", exception);
            throw new MessageDigestException("Incorrect message digest");
        }
    }

    public boolean saveFile(String filePath, byte[] fileArray) throws FileSaveException {
        log.debug("Request to save file with path: " + filePath);
        try (FileOutputStream fos = new FileOutputStream(FILES_VOLUME + filePath)) {
            fos.write(fileArray);
            return true;
        } catch (IOException exception) {
            log.error("File save exception: ", exception);
            throw new FileSaveException("Error while saving file");
        }
    }

    public boolean deleteFile(String filePath) {
        log.debug("Request to delete file with path: " + filePath);
        File file = new File(FILES_VOLUME + filePath);
        return file.delete();
    }

    public void deleteAllFiles() {
        log.debug("Request to delete all files");
        File[] files = new File(FILES_VOLUME).listFiles();
        if (files != null && files.length > 0) {
            Arrays.stream(files).forEach(File::delete);
        }
    }

    public byte[] generateFileByteArray(int size) {
        log.debug("Request to generate file byte array");
        if (size <= 0) {
            return new byte[0];
        }
        Random random = new Random();
        byte[] result = new byte[size];
        random.nextBytes(result);
        return result;
    }
}
