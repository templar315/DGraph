package ua.edu.donntu.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import ua.edu.donntu.service.exceptions.MessageDigestException;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        FileService.class,
        FileOutputStream.class})
public class FileServiceTest {

    private final FileService fileService = PowerMockito.spy(new FileService());
    private final FileOutputStream fileOutputStream = PowerMockito.mock(FileOutputStream.class);
    private final File mockedFile = PowerMockito.mock(File.class);
    private final File[] files = new File[]{ mockedFile, mockedFile, mockedFile };

    private static final String FILES_VOLUME = "/var/lib/dgraph";
    private final String digest = "SHA-256";
    private final String filePath = "\\4967d20a6b5d124f56edce5666df77c6977df075595e491b6748e390e0abd0fe.txt";

    byte[] file = "file1".getBytes();
    byte[] emptyArray = new byte[] {};

    @Test
    public void getHash() throws Exception {
        assertThat((String) Whitebox.invokeMethod(fileService, "getHash", file, digest))
                .isEqualTo(String.format(
                        "%02x",
                        new BigInteger(1, MessageDigest.getInstance(digest).digest(file))));
        assertThat((String) Whitebox.invokeMethod(fileService, "getHash", emptyArray, digest))
                .isEqualTo(String.format(
                        "%02x",
                        new BigInteger(1, MessageDigest.getInstance(digest).digest(emptyArray))));
        try {
            Whitebox.invokeMethod(fileService, "getHash", file, "incorrectDigest");
            assert false;
        } catch (MessageDigestException mde) {
            assert true;
        }
    }

    @Test
    public void saveFile() throws Exception {
        PowerMockito.whenNew(FileOutputStream.class).withArguments(FILES_VOLUME + filePath).thenReturn(fileOutputStream);
        assertThat((boolean) Whitebox.invokeMethod(fileService, "saveFile", filePath, file)).isTrue();
    }

    @Test
    public void deleteFile() throws Exception {
        PowerMockito.whenNew(File.class).withArguments(FILES_VOLUME + filePath).thenReturn(mockedFile);
        PowerMockito.when(mockedFile.delete()).thenReturn(true);
        assertThat((boolean) Whitebox.invokeMethod(fileService, "deleteFile", filePath)).isTrue();
    }

    @Test
    public void deleteAllFiles() throws Exception {
        PowerMockito.whenNew(File.class).withArguments(FILES_VOLUME).thenReturn(mockedFile);
        PowerMockito.when(mockedFile.listFiles()).thenReturn(files);
        PowerMockito.when(mockedFile.delete()).thenReturn(true);
        Whitebox.invokeMethod(fileService, "deleteAllFiles");
        Mockito.verify(mockedFile, times(3)).delete();
    }
}
