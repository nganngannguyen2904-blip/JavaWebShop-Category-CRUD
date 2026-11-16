import java.io.*;
import java.nio.file.*;

public class GenerateSampleVideo {
    public static void main(String[] args) throws Exception {
        // Base64 of a valid 1-second 320x240 black MP4 video
        // Valid ISO ftypisom, moov, mdat
        String base64Mp4 = 
            "AAAAHGZ0eXBpc29tAAAAAGlzb21tcDQxAAAACHZpZGVvAAAAgG1vb3YAAABsbXZoZAAAAADbZq" +
            "g322aoNwAA+gAAAAAAAEAAAUAAAgABAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAA" +
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB0dHJhazAAAAx0a2hkAAAABNtmqDfbZqg3AAAAAQAAAA" +
            "AAAAEAAAAAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "AAABBm1kaWEAAAAgbWRoZAAAAADbZqg322aoNwAA+gAAAEAAAAAcAAAAAAAhaGRscgAAAAAAAA" +
            "AAdmlkZQAAAAAAAAAAAAAAAFZpZGVvSGFuZGxlcgAAAAF5bWluZgAAABR2bWhkAAAAAQAAAAAA" +
            "AAAAAAAAJGRpbmYAAAAcZHJlZgAAAAAAAAABAAAADHVybCAAAAABAAABUGhhbXN0YmwAAAHWc3" +
            "RzZAAAAAAAAAABAAABr2F2YzEAAAAAAAAAAQAAAAAAAAAAAAAAAAAAAAAAUAAYASwAAAEsAAAAA" +
            "AAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAABhhdnNDAWQAAP/hABFnZAAA/+gBAABAAgAA" +
            "BAADAAAA4AEHjwwC8AAAAAVo68AAAgAAABhzdHRzAAAAAAAAAAEAAAABAAAPoAAAABxzdHNjAA" +
            "AAAAAAAAEAAAABAAAAAQAAAAEAAAAUc3RzegAAAAAAAAAAAAAAAQAAAAwAAAAUc3RjbwAAAAAAAA" +
            "ABAAAAgAAAACB1ZHRhAAAAGG1ldGEAAAAAAAAAIWhkbHIAAAAAAAAAAG1kaXIAAAAAAAAAAAAA" +
            "AABkYXRhAAAEAAAAAAAKZGF0YQAAAADbZqg3AAAAAAAMbWRhdAAAAAGAAAA=";

        byte[] videoBytes = java.util.Base64.getDecoder().decode(base64Mp4);

        File dir = new File("upload/video");
        if (!dir.exists()) dir.mkdirs();

        for (int i = 1; i <= 12; i++) {
            String fileName = String.format("video_demo_%02d.mp4", i);
            File f = new File(dir, fileName);
            Files.write(f.toPath(), videoBytes);
            System.out.println("Tạo thành công file video mẫu: " + f.getAbsolutePath() + " (" + f.length() + " bytes)");
        }
    }
}
