package com.dto;

import com.pojo.StorePojo;
import com.service.ApiException;
import com.service.StoreService;
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
public class StoreDto {
    @Autowired
    private StoreService storeService;

    public void add(@RequestPart MultipartFile file) throws IOException, ApiException {
        BufferedReader TSVFile = new BufferedReader(new
                InputStreamReader(file.getInputStream(), "UTF-8"));
        String dataRow = TSVFile.readLine(); // Read first line.
        int rowNumber = 0;
        while (dataRow != null) {
            if(rowNumber==0){
                if(checkFileHeading(dataRow)==false){
                    throw new ApiException("File orientation is not proper");
                }
            }
            else {
                storeService.add(convertRowsToPojo(dataRow));
            }
            dataRow = TSVFile.readLine();// Read next line of data.
            rowNumber++;
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
