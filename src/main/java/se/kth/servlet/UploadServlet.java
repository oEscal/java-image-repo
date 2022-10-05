package se.kth.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.zeroturnaround.zip.NameMapper;
import org.zeroturnaround.zip.ZipEntryCallback;
import org.zeroturnaround.zip.ZipUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.zip.ZipEntry;


final class CurrentTime {
    public static long get() {
        return (Instant.now().getEpochSecond() * 1000000000L + Instant.now().getNano())/100000;
    }
}

final class MyNameMapper implements NameMapper {

    public MyNameMapper() {}

    public String map(String name) {
        return String.valueOf(CurrentTime.get());
    }
}

final class MyZipEntryCallback implements ZipEntryCallback {

    @Override
    public void process(InputStream in , ZipEntry zipEntry) throws IOException {
        String fileName = String.valueOf(CurrentTime.get());

        String filePath = Constants.UPLOAD_DIRECTORY + File.separator + fileName;
        File storeFile = new File(filePath);

        Files.copy( in , storeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.setPosixFilePermissions(FileSystems.getDefault().getPath(filePath), Constants.UPLOAD_FILE_PERMISSIONS);
    }

}


@WebServlet(name = "UploadServlet", urlPatterns = {
    "/upload_file"
})
public class UploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (ServletFileUpload.isMultipartContent(request)) {

            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(Constants.MEMORY_THRESHOLD);
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(Constants.MAX_FILE_SIZE);
            upload.setSizeMax(Constants.MAX_REQUEST_SIZE);
            String uploadPath = Constants.UPLOAD_DIRECTORY;

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            try {
                List <FileItem> formItems = upload.parseRequest(request);

                if (formItems != null && formItems.size() > 0) {

                    for (FileItem item: formItems) {
                        if (!item.isFormField()) {
                            if (item.getName().endsWith(".zip")) {
                                // ZipUtil.iterate(item.getInputStream(), new MyZipEntryCallback());
                                ZipUtil.unpack(item.getInputStream(), new File(uploadPath));
                            } else {
                                // Create file name from Unix time stamp -> unique file name with order
                                String fileName = String.valueOf(CurrentTime.get());

                                String filePath = uploadPath + File.separator + fileName;
                                File storeFile = new File(filePath);
                                // storeFile.setReadable(true, false);
                                item.write(storeFile);
                                Files.setPosixFilePermissions(FileSystems.getDefault().getPath(filePath), Constants.UPLOAD_FILE_PERMISSIONS);
                                request.setAttribute("message", "File " + fileName + " has uploaded successfully!");
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                request.setAttribute("message", "There was an error: " + ex.getMessage());
            }

            response.sendRedirect("/");
            // request.getRequestDispatcher("/result.html").forward(request, response);
        }
    }
}
