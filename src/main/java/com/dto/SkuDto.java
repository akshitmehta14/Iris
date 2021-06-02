package com.dto;

import com.pojo.SkuPojo;
import com.pojo.StorePojo;
import com.pojo.StylePojo;
import com.service.ApiException;
import com.service.SkuService;
import com.service.StyleService;
import com.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class SkuDto {
    @Autowired
    private SkuService skuService;
    @Autowired
    private StyleService styleService;

    public void add(@RequestPart MultipartFile file) throws IOException, ApiException {
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
            skuService.add(convertRowsToPojo(dataRow));
            dataRow = TSVFile.readLine(); // Read next line of data.
            rowNumber++;
        }
    }

    public void selectAll(HttpServletResponse response) throws IOException {
        List<SkuPojo> skuPojo = skuService.selectAll();
        FileWriter fos = new FileWriter("files/downloads/sku.txt",false);
        PrintWriter dos = new PrintWriter(fos);
        dos.println("SKU\tStyle Code\tSize");
        for(SkuPojo s:skuPojo){
            dos.print(s.getSkuCode()+'\t'+s.getStyleId()+'\t'+s.getSize());
            dos.println();
        }
        fos.close();
        FileUtil.downloadFile("downloads/sku",response);
    }

    public void downloadErrors(HttpServletResponse response) throws ApiException, IOException {
        File file = new File("C:\\projects\\Iris\\files\\error-files\\sku-error.txt");
        if (file.exists() == false) {
            throw new ApiException("Upload a file first.");
        }
        FileUtil.downloadFile("error-files/sku-error", response);
    }

    public boolean scanFileForErrors(MultipartFile file) throws IOException {
        BufferedReader TSVFile = new BufferedReader(new
                InputStreamReader(file.getInputStream(), "UTF-8"));
        boolean ans = false;
        String dataRow = TSVFile.readLine();
        int rowNumber = 0;
        refreshFile();
        dataRow = TSVFile.readLine();
        FileWriter fos = new FileWriter("files/error-files/sku-error.txt", true);
        PrintWriter dos = new PrintWriter(fos);
        while (dataRow != null) {
            try {
                skuService.exists(convertRowsToPojo(dataRow));
            } catch (ApiException e) {
                String x = e.getMessage();
                System.out.println(dataRow + '\t' + e.getMessage());
                dos.print(x);
                dos.println();
                dos.print(x);
//                FileUtil.addrow("error-files/style-error.txt",x);
                ans = true;
            }
            System.out.println(rowNumber);
            rowNumber++;
            dataRow = TSVFile.readLine(); // Read next line of data.
        }

        return ans;
    }

    private void refreshFile() throws IOException {
        FileWriter fos = new FileWriter("files/error-files/sku-error.txt", false);
        PrintWriter dos = new PrintWriter(fos);
        dos.println("Row Number\tSKU\tStyle Code\tSize\tError Message");
        fos.close();
    }

    private SkuPojo convertRowsToPojo(String dataRow) throws ApiException {
        StringTokenizer st = new StringTokenizer(dataRow, "\t");
        SkuPojo skuPojo = new SkuPojo();
        List<String> dataArray = new ArrayList<String>();
        while (st.hasMoreElements()) {
            dataArray.add(st.nextElement().toString());
        }
        skuPojo.setSkuCode(dataArray.get(0));
        skuPojo.setStyleId(styleService.select(dataArray.get(1)));
        skuPojo.setSize(dataArray.get(2));
        return skuPojo;
    }

    private boolean checkFileHeading(String dataRow) {
        StringTokenizer st = new StringTokenizer(dataRow, "\t");
        List<String> dataArray = new ArrayList<String>();
        while (st.hasMoreElements()) {
            dataArray.add(st.nextElement().toString());
        }
        if (!dataArray.get(1).equals("Style Code") || !dataArray.get(0).equals("SKU") || !dataArray.get(2).equals("Size")) {
            return false;
        }
        return true;
    }
}
