package ua.edu.donntu.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileService {

    private final Logger log = LoggerFactory.getLogger(FileService.class);

    protected String getHash(InputStream inputStream, MessageDigest digest) throws IOException {
        log.debug("Request to calculate {} hash of file", digest.getAlgorithm());

        try (DigestInputStream dis = new DigestInputStream(inputStream, digest)) {
            while (dis.read() != -1);
            digest = dis.getMessageDigest();
        }

        StringBuilder result = new StringBuilder();
        for (byte b : digest.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
