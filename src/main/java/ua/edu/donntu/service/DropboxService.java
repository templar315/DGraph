package ua.edu.donntu.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.edu.donntu.service.exceptions.FileDeleteException;
import ua.edu.donntu.service.exceptions.FileDownloadException;
import ua.edu.donntu.service.exceptions.FileSaveException;

import java.io.*;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DropboxService {

    private final Logger log = LoggerFactory.getLogger(DropboxService.class);

    @Value("${application.dropbox.identifier}")
    private String identifier;

    @Value("${application.dropbox.token}")
    private String accessToken;

    private DbxClientV2 client;

    public DbxClientV2 getClient() {
        log.debug("Request to get dropbox client");
        if (client == null) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder(identifier).build();
            client = new DbxClientV2(config, accessToken);
        }
        return client;
    }

    public FileMetadata upload(InputStream stream, String dropboxPath) throws FileSaveException {
        log.debug("Request to upload file to dropbox path: " + dropboxPath);
        try {
            FileMetadata metadata = getClient()
                    .files()
                    .uploadBuilder(dropboxPath)
                    .withMode(WriteMode.ADD)
                    .uploadAndFinish(stream);
            stream.close();
            return metadata;
        } catch (DbxException | IOException exception) {
            log.error("Dropbox upload error: ", exception);
            throw new FileSaveException("Error while saving file");
        }
    }

    public MultipartFile download(String dropboxPath) throws FileDownloadException {
        log.debug("Request to download file from dropbox path: " + dropboxPath);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            FileMetadata metadata = getClient()
                    .files()
                    .downloadBuilder(dropboxPath)
                    .download(byteArrayOutputStream);
            return new MockMultipartFile(
                    "file",
                    metadata.getName(),
                    String.valueOf(ContentType.MULTIPART_FORM_DATA),
                    byteArrayOutputStream.toByteArray());
        } catch (DbxException | IOException exception) {
            log.error("Dropbox download error: ", exception);
            throw new FileDownloadException("Error while downloading file");
        }
    }

    public void delete(String dropboxPath) throws FileDeleteException {
        log.debug("Request to delete file by dropbox path: " + dropboxPath);
        try {
            getClient().files().deleteV2(dropboxPath);
        } catch (DbxException exception) {
            log.error("Dropbox delete error: ", exception);
            throw new FileDeleteException("Error while deleting file");
        }
    }
}
