package se.kth.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import com.hubspot.jinjava.Jinjava;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet(name = "UploadWelcomeServlet", urlPatterns = "/")
public class UploadWelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String uploadPath = Constants.UPLOAD_DIRECTORY;
        Jinjava jinjava = new Jinjava();

        List<String> images = new ArrayList<>();
        File images_dir = new File(uploadPath);
        if (images_dir.exists()) {
            String[] all_images_paths = images_dir.list();
            Arrays.sort(all_images_paths, Comparator.reverseOrder());
            for (String image_path : all_images_paths) {
                byte[] fileContent = FileUtils.readFileToByteArray(new File(uploadPath + "/" + image_path));
                images.add(Base64.getEncoder().encodeToString(fileContent));
            }
        }

        Map<String, Object> context = new HashMap<>();
        context.put("images", images);

        String fileName = this.getServletContext().getRealPath("index.html");
        Path path = Paths.get(fileName);
        String template = Files.readString(path);
        String renderedTemplate = jinjava.render(template, context);
        
        PrintWriter out = response.getWriter();
        out.print(renderedTemplate);
        out.close();
        // request.getRequestDispatcher("/index.html").forward(request, response);
    }
}
