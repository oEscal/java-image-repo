package se.kth.servlet;

import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {

    public static final String UPLOAD_DIRECTORY = System.getenv().getOrDefault("IMAGES_DIR", "upload");
    public static final String DEFAULT_FILENAME = "default.file";
    public static final Set<PosixFilePermission> UPLOAD_FILE_PERMISSIONS = new HashSet <> (Arrays.asList(new PosixFilePermission[] {
        PosixFilePermission.OWNER_READ,
        PosixFilePermission.OWNER_WRITE,
        PosixFilePermission.OWNER_EXECUTE,
        PosixFilePermission.GROUP_READ,
        PosixFilePermission.OTHERS_READ
    }));

    public static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;
    public static final int MAX_FILE_SIZE = 1024 * 1024 * 40;
    public static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50;
}