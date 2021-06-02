package com.util;

import com.pojo.StylePojo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public class FileUtil {

    public static void downloadFile(String fileName, HttpServletResponse response) throws IOException {
        String fileLocation = getFileLocation(fileName);
        InputStream inputStream = null;
        inputStream = new FileInputStream(fileLocation);

        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while (  (bytesRead = inputStream.read(buffer)) != -1)
            baos.write(buffer, 0, bytesRead);

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename =data.txt");

        byte[] outBuf = baos.toByteArray();
        response.getOutputStream().write(outBuf);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    public static void addrow(String fileName, String datarow) throws IOException {
        FileWriter fos = new FileWriter("files/"+fileName,true);
        PrintWriter dos = new PrintWriter(fos);
        dos.println(datarow);
    }

    private static String getFileLocation(String filename){
        return "files/" + filename + ".txt";
    }
}
