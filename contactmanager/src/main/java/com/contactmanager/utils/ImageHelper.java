package com.contactmanager.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ClassPathResource;

public class ImageHelper {
	
    public static boolean deleteImage(String image) {

        try {
        	
        	File file = new ClassPathResource("static/images").getFile();

			Path imagePath = Paths.get(file.getAbsolutePath() + File.separator + image);
        	
            Files.delete(imagePath);
            System.out.println("deleted");
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

	/*
	 * public boolean addImage(Part part, String path) throws IOException {
	 * 
	 * FileOutputStream fos = null;
	 * 
	 * try {
	 * 
	 * fos = new FileOutputStream(path); InputStream is = part.getInputStream();
	 * 
	 * byte data[] = new byte[is.available()]; is.read(data); fos.write(data);
	 * fos.close();
	 * 
	 * return true; } catch (Exception e) { if (fos != null) { fos.close(); }
	 * e.printStackTrace();
	 * 
	 * return false; }
	 * 
	 * }
	 */

}
