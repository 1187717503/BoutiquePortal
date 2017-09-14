package pk.shoplus.util;


import com.alibaba.fastjson.JSONObject;
import difflib.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingyifan on 2017/7/4.
 */
public class FileUtil {

    private static Logger logger = Logger.getLogger(FileUtil.class);

    private static String encoding = "UTF-8";

    public static List<DiffRow> CompareTxt(String originalPath,String revisedPath) throws IOException {
        logger.info("start FileUtil.CompareTxt();originalPath:"+originalPath+",revisedPath:"+revisedPath);
        List<DiffRow> changeRows = new ArrayList<>();
        List<String> original = FileUtils.readLines(new File(originalPath));
        List<String> revised = FileUtils.readLines(new File(revisedPath));

        Patch patch = DiffUtils.diff(original, revised);
        DiffRowGenerator.Builder builder = new DiffRowGenerator.Builder();
        builder.showInlineDiffs(false);
        DiffRowGenerator generator = builder.build();

        for (Delta delta :  patch.getDeltas()) {
            List<DiffRow> generateDiffRows = generator.generateDiffRows((List<String>) delta.getOriginal().getLines(), (List<String>) delta
                    .getRevised().getLines());
            for (DiffRow row : generateDiffRows) {
                changeRows.add(row);
            }
        }
        logger.info("end FileUtil.CompareTxt();");
        return changeRows;
    }
    
    
    public static List<DiffRow> CompareTxtByType(String originalPath,String revisedPath) throws IOException {
        logger.info("start FileUtil.CompareTxtByType();originalPath:"+originalPath+",revisedPath:"+revisedPath);
        List<DiffRow> changeRows = new ArrayList<>();
        List<String> original = FileUtils.readLines(new File(originalPath),"windows-1252");
        List<String> revised = FileUtils.readLines(new File(revisedPath),"windows-1252");

        Patch patch = DiffUtils.diff(original, revised);
        DiffRowGenerator.Builder builder = new DiffRowGenerator.Builder();
        builder.showInlineDiffs(false);
        DiffRowGenerator generator = builder.build();

        for (Delta delta :  patch.getDeltas()) {
            List<DiffRow> generateDiffRows = generator.generateDiffRows((List<String>) delta.getOriginal().getLines(), (List<String>) delta
                    .getRevised().getLines());
            for (DiffRow row : generateDiffRows) {
                changeRows.add(row);
            }
        }
        logger.info("end FileUtil.CompareTxtByType();");
        return changeRows;
    }


    public static String readFile(String path){
        StringBuffer stringBuffer = new StringBuffer();
        try {
            File file=new File(path);
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
//                    System.out.println(lineTxt);
                    stringBuffer.append(lineTxt+"\n");
                }
                read.close();
            }else{
                logger.info("error message : file is not found !!!");
            }
        } catch (Exception e) {
            logger.error(" error message : " + e.getMessage());
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public static String readFileByFilippo(String path) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            File file=new File(path);
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    if(lineTxt.indexOf("#") != 0 &&
                            !lineTxt.contains("html") &&
                            !lineTxt.contains("ART_ID") &&
                            !lineTxt.contains("N|N")) {
                        stringBuffer.append(lineTxt + "\n");
                    }
                }
                read.close();
            }else{
                logger.info("error message : file is not found !!!");
            }
        } catch (Exception e) {
            logger.error(" error message : " + e.getMessage());
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public static List<String> readFileByFilippoList(String path) {
        List<String> lists = new ArrayList<>();
        try {
            File file=new File(path);
            if(file.isFile() && file.exists()){
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    if(lineTxt.indexOf("#") != 0 &&
                            !lineTxt.contains("html") &&
                            !lineTxt.contains("ART_ID") &&
                            !lineTxt.contains("N|N")) {
                        lists.add(lineTxt);
                    }
                }
                read.close();
            }else{
                logger.info("error message : file is not found !!!");
            }
        } catch (Exception e) {
            logger.error(" error message : " + e.getMessage());
            e.printStackTrace();
        }
        return lists;
    }

    public static void createFile(String filePath,String fileName,String filecontent){
        try {
            File f1 = new File(filePath);
            File f2 = new File(filePath+fileName);
            if(!f1.exists()) {
                f1.mkdirs();
            }
            if(!f2.exists()) {
                f2.createNewFile();
            }
            writeFileContent(filePath + fileName, filecontent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    public static void createFileByType(String filePath,String fileName,String filecontent){
        try {
            File f1 = new File(filePath);
            File f2 = new File(filePath+fileName);
            if(!f1.exists()) {
                f1.mkdirs();
            }
            if(!f2.exists()) {
                f2.createNewFile();
            }
            writeFileContentByType(filePath + fileName, filecontent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    public static boolean fileExists(String path){
        boolean flag = false;
        try {
            File file = new File(path);
            if(file.isFile() && file.exists()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void writeFileContent(String filepath,String newstr) throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();

            /*//文件原有内容
            for(int i=0;(temp =br.readLine())!=null;i++){
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer = buffer.append(System.getProperty("line.separator"));
            }*/
            buffer.append(newstr);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    
    public static void writeFileContentByType(String filepath,String newstr) throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos  = null;
        OutputStreamWriter pw = null;
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis,"windows-1252");
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();

            /*//文件原有内容
            for(int i=0;(temp =br.readLine())!=null;i++){
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer = buffer.append(System.getProperty("line.separator"));
            }*/
            buffer.append(newstr);

            fos = new FileOutputStream(file);
            pw = new OutputStreamWriter(fos,"windows-1252");
            pw.write(buffer.toString().toCharArray());
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    public static void writeFileAppendContent(String filepath,String newstr) throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos  = null;
        PrintWriter pw = null;
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();

            String temp = "";
            //文件原有内容
            for(int i=0;(temp =br.readLine())!=null;i++){
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer = buffer.append(System.getProperty("line.separator"));
            }
            buffer.append(newstr);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }
}
