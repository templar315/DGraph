package ua.edu.donntu.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.donntu.service.exceptions.FileInputStreamException;
import ua.edu.donntu.service.exceptions.MessageDigestException;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileService {

    private final Logger log = LoggerFactory.getLogger(FileService.class);

    protected String getHash(InputStream inputStream, String digest) throws MessageDigestException,
                                                                            FileInputStreamException {
        log.debug("Request to generate {} hash of stream", digest);
        StringBuilder result = new StringBuilder();
        try (DigestInputStream dis = new DigestInputStream(inputStream, MessageDigest.getInstance(digest))) {
            //while (dis.read() != -1);
            for (byte b : dis.getMessageDigest().digest()) {
                result.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException exception) {
            log.error("Message digest error: ", exception);
            throw new MessageDigestException("Incorrect message digest");
        } catch (IOException exception) {
            log.error("File input stream error: ", exception);
            throw new FileInputStreamException("File input stream error");
        }
        return result.toString();
    }

    protected String getHashByFile(MultipartFile file, String digest) throws MessageDigestException,
                                                                             FileInputStreamException {
        log.debug("Request to generate {} hash of file", digest);
        try {
            return getHash(file.getInputStream(), digest);
        } catch (IOException exception) {
            log.error("File input stream error: ", exception);
            throw new FileInputStreamException("File input stream error");
        }
    }
}
