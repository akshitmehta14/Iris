package com.dto;

import com.pojo.StorePojo;
import com.service.ApiException;
import com.service.StoreService;
import com.util.FileUtil;
import com.util.StringUtil;
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
public class StoreDto {
    @Autowired
    private StoreService storeService;

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
        while (dataRow != null) {
            storeService.add(convertRowsToPojo(dataRow));
            dataRow = TSVFile.readLine(); // Read next line of data.
        }
    }

    public void selectAll(HttpServletResponse response) throws IOException {
        List<StorePojo> stylePojo = storeService.selectAll();
        FileWriter fos = new FileWriter("files/downloads/store.txt");
        PrintWriter dos = new PrintWriter(fos);
        dos.println("Branch\tCity");
        for(StorePojo s:stylePojo){
            dos.print(s.getBranch()+'\t'+s.getCity());
            dos.println();
        }
        fos.close();
        FileUtil.downloadFile("downloads/store",response);
    }

    public void downloadErrors(HttpServletResponse response) throws ApiException, IOException {
        File file = new File("C:\\projects\\Iris\\files\\error-files\\store-error.txt");
        if (file.exists() == false) {
            throw new ApiException("Upload a file first.");
        }
        FileUtil.downloadFile("error-files/store-error", response);
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
            StorePojo dataConverted = convertRowsToPojo(dataRow);
            dataConverted = normalize(dataConverted);
            try {
                check(dataConverted);
                storeService.exists(dataConverted);
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

    private void check(StorePojo dataConverted) throws ApiException {
        if(StringUtil.isEmpty(dataConverted.getBranch()) || StringUtil.isEmpty(dataConverted.getBranch())){
            throw new ApiException("One or more fields empty.");
        }
    }

    private StorePojo normalize(StorePojo dataConverted) {
        dataConverted.setCity(StringUtil.toLowerCaseTrim(dataConverted.getCity()));
        dataConverted.setBranch(StringUtil.toLowerCaseTrim(dataConverted.getBranch()));
        return dataConverted;
    }

    private void refreshFile() throws IOException {
        FileWriter fos = new FileWriter("files/error-files/store-error.txt", false);
        PrintWriter dos = new PrintWriter(fos);
        dos.println("Row Number\tBranch\tCity\tError Message");
        fos.close();
    }

    private StorePojo convertRowsToPojo(String dataRow) {
        StringTokenizer st = new StringTokenizer(dataRow, "\t");
        StorePojo storePojo = new StorePojo();
        List<String> dataArray = new ArrayList<String>();
        while (st.hasMoreElements()) {
            dataArray.add(st.nextElement().toString());
        }
        storePojo.setBranch(dataArray.get(0));
        storePojo.setCity(dataArray.get(1));
        return storePojo;
    }

    private boolean checkFileHeading(String dataRow) {
        StringTokenizer st = new StringTokenizer(dataRow, "\t");
        List<String> dataArray = new ArrayList<String>();
        while (st.hasMoreElements()) {
            dataArray.add(st.nextElement().toString());
        }
        if (dataArray.get(0).equals("Branch" )==false || dataArray.get(1).equals("City")==false ) {
            return false;
        }
        return true;
    }

}
