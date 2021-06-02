package com.dto;

import com.pojo.StylePojo;
import com.service.ApiException;
import com.service.StyleService;
import com.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


@Service
public class StyleDto {
    @Autowired
    private StyleService styleService;

    public void add(MultipartFile file) throws IOException, ApiException {
        BufferedReader TSVFile = new BufferedReader(new
                InputStreamReader(file.getInputStream(), "UTF-8"));
        String dataRow = TSVFile.readLine(); // Read first line.
        if (checkFileHeading(dataRow) == false) {
            throw new ApiException("File orientation is not proper");
        }
        boolean errors = scanFileForErrors(file);
        if (errors) {
            throw new ApiException("File contains some errors.");
        }
        dataRow = TSVFile.readLine();

        int rowNumber = 1;
        while (dataRow != null) {
            styleService.add(convertRowsToPojo(dataRow));
            dataRow = TSVFile.readLine(); // Read next line of data.
            rowNumber++;
        }
    }

    public void selectAll(HttpServletResponse response) throws IOException {
        List<StylePojo> stylePojo = styleService.selectAll();
        FileWriter fos = new FileWriter("files/downloads/style.txt", false);
        PrintWriter dos = new PrintWriter(fos);
        dos.println("Style Code\tBrand\tCategory\tSub-Category\tMRP\tGender");
        for (StylePojo s : stylePojo) {
            dos.print(s.getStyleCode() + '\t' + s.getBrand() + '\t' + s.getCategory() + '\t' + s.getSubCategory() + '\t' + s.getMrp() + '\t' + s.getGender());
            dos.println();
        }
        fos.close();
        FileUtil.downloadFile("downloads/style", response);
    }

    public void downloadErrors(HttpServletResponse response) throws ApiException, IOException {
        File file = new File("C:\\projects\\Iris\\files\\error-files\\style-error.txt");
        if (file.exists() == false) {
            throw new ApiException("Upload a file first.");
        }
        FileUtil.downloadFile("error-files/style-error", response);
    }

    public boolean scanFileForErrors(MultipartFile file) throws IOException {
        BufferedReader TSVFile = new BufferedReader(new
                InputStreamReader(file.getInputStream(), "UTF-8"));
        boolean ans = false;
        String dataRow = TSVFile.readLine();
        int rowNumber = 1;
        refreshFile();
        dataRow = TSVFile.readLine();
        FileWriter fos = new FileWriter("files/error-files/style-error.txt", true);
        PrintWriter dos = new PrintWriter(fos);
        while (dataRow != null) {
            try {
                styleService.exists(convertRowsToPojo(dataRow));
            } catch (ApiException e) {
                String x = dataRow + '\t' + e.getMessage();
                System.out.println(x);

                dos.println(rowNumber+"\t"+x);
                ans = true;
            }
            System.out.println(rowNumber);
            rowNumber++;
            dataRow = TSVFile.readLine(); // Read next line of data.
        }
        fos.close();
        return ans;
    }

    private void refreshFile() throws IOException {
        FileWriter fos = new FileWriter("files/error-files/style-error.txt", false);
        PrintWriter dos = new PrintWriter(fos);
        dos.println("Row Number\tStyle Code\tBrand\tCategory\tSub-Category\tMRP\tGender\tError Message");
        fos.close();
    }

    private StylePojo convertRowsToPojo(String dataRow) {
        StringTokenizer st = new StringTokenizer(dataRow, "\t");
        StylePojo stylePojo = new StylePojo();
        List<String> dataArray = new ArrayList<String>();
        while (st.hasMoreElements()) {
            dataArray.add(st.nextElement().toString());
        }
        stylePojo.setStyleCode(dataArray.get(0));
        stylePojo.setBrand(dataArray.get(1));
        stylePojo.setCategory(dataArray.get(2));
        stylePojo.setSubCategory(dataArray.get(3));
        stylePojo.setMrp(Double.parseDouble(dataArray.get(4)));
        stylePojo.setGender(dataArray.get(5));
        return stylePojo;
    }

    private boolean checkFileHeading(String dataRow) {
        StringTokenizer st = new StringTokenizer(dataRow, "\t");
        List<String> dataArray = new ArrayList<String>();
        while (st.hasMoreElements()) {
            dataArray.add(st.nextElement().toString());
        }
        if (!dataArray.get(0).equals("Style Code") || !dataArray.get(1).equals("Brand") || !dataArray.get(2).equals("Category") || !dataArray.get(3).equals("Sub-Category") || !dataArray.get(4).equals("MRP") || !dataArray.get(5).equals("Gender")) {
            return false;
        }
        return true;
    }
}
