package vn.iotstar.controller.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import vn.iotstar.util.Constant;

/**
 * Controller phục vụ phát trực tiếp video (HTML5 Video Stream)
 * Hỗ trợ MIME type video/mp4, video/webm và xử lý HTTP 206 Partial Content (Range Header)
 * URL: /video/stream?fname=filename.mp4
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/video/stream")
public class VideoStreamController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fname = req.getParameter("fname");
        if (fname == null || fname.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy tên file video!");
            return;
        }

        // Chống Path Traversal
        fname = new File(fname).getName();

        // Tìm file video trong các thư mục upload
        File file = findVideoFile(req, fname);

        if (file == null || !file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File video không tồn tại trên hệ thống!");
            return;
        }

        long length = file.length();
        String contentType = "video/mp4";
        String lower = fname.toLowerCase();
        if (lower.endsWith(".webm")) {
            contentType = "video/webm";
        } else if (lower.endsWith(".ogg") || lower.endsWith(".ogv")) {
            contentType = "video/ogg";
        }

        resp.setContentType(contentType);
        resp.setHeader("Accept-Ranges", "bytes");

        // Xử lý HTTP Range Requests cho phép trình duyệt tua (seek) video mượt mà
        String rangeHeader = req.getHeader("Range");
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            long start = 0;
            long end = length - 1;

            try {
                if (ranges[0].length() > 0) {
                    start = Long.parseLong(ranges[0]);
                }
                if (ranges.length > 1 && ranges[1].length() > 0) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException ignored) {
            }

            if (start > end || start >= length) {
                resp.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                resp.setHeader("Content-Range", "bytes */" + length);
                return;
            }

            long contentLength = end - start + 1;
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            resp.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + length);
            resp.setContentLengthLong(contentLength);

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = resp.getOutputStream()) {
                fis.skip(start);
                byte[] buffer = new byte[8192];
                long bytesRemaining = contentLength;
                int read;
                while (bytesRemaining > 0 && (read = fis.read(buffer, 0, (int) Math.min(buffer.length, bytesRemaining))) != -1) {
                    os.write(buffer, 0, read);
                    bytesRemaining -= read;
                }
                os.flush();
            }
        } else {
            // Không yêu cầu Range, phát toàn bộ video
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentLengthLong(length);
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = resp.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
        }
    }

    private File findVideoFile(HttpServletRequest req, String fname) {
        // 1. Thư mục Constant.UPLOAD_DIR/video
        File f1 = new File(Constant.UPLOAD_DIR + File.separator + "video" + File.separator + fname);
        if (f1.exists()) return f1;

        // 2. Thư mục Constant.UPLOAD_DIR
        File f2 = new File(Constant.UPLOAD_DIR + File.separator + fname);
        if (f2.exists()) return f2;

        // 3. Thư mục webapp/upload/video
        String realPath = req.getServletContext().getRealPath("/upload/video/" + fname);
        if (realPath != null) {
            File f3 = new File(realPath);
            if (f3.exists()) return f3;
        }

        return null;
    }
}
