package database;

import vn.iotstar.util.Constant;
import java.io.File;

public class TestImageSearch {
    public static void main(String[] args) {
        System.out.println("Constant.UPLOAD_DIR = " + Constant.UPLOAD_DIR);
        String[] files = {
            "sp_nam_01.png", "sp_nam_02.png", "sp_nam_10.png",
            "sp_nu_01.png", "sp_nu_05.png", "sp_nu_10.png",
            "sp_giay_01.png", "sp_dh_01.png", "sp_dt_01.png", "sp_mp_01.png"
        };

        String[] subDirs = {"/product/", "/category/", "/", ""};

        for (String fileName : files) {
            boolean found = false;
            for (String sub : subDirs) {
                File f = new File(Constant.UPLOAD_DIR + sub + fileName);
                if (f.exists() && f.isFile()) {
                    System.out.println("FOUND: " + fileName + " at " + f.getAbsolutePath() + " (" + f.length() + " bytes)");
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("NOT FOUND: " + fileName);
            }
        }
    }
}
