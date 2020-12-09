import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Setting {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    // Setting is the class for config process via GUI
    // DEFAULT_RESULT_PATH => path for save result file ex. /Users/mac/Desktop/
    private static Path DEFAULT_RESULT_FILE_PATH;
    private static PDColor OLD_DIF_COLOR;
    private static PDColor NEW_DIF_COLOR;
    private static String log = "";
    private static ArrayList<CmpHistory> history;
    private static CoparisoView view;

    static {
        // ตั้งค่าของที่อยู่ไฟล์ผลลัพธ์
        // modify result path here!!!
        // default now is ./resources
        Setting.setDefaultResultPath(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString(), "resources").toString());
        // ตั้งค่าสีไฮไลท์ของไฟล์เก่า/ใหม่
        Setting.setTextOldHighlightColor(255, 0, 0);
        Setting.setTextNewHighlightColor(0, 255, 0);
    }

    // setter & getter for saved difference file path
    public static String getDefaultResultPath() {
        return DEFAULT_RESULT_FILE_PATH.toString();
    }

    public static void setDefaultResultPath(String defaultResultPath) {
        DEFAULT_RESULT_FILE_PATH = Paths.get(defaultResultPath);
    }

    // setter & getter for difference color
    // input color intensity is in range 0-1
    public static PDColor getOldDifColor() {
        return OLD_DIF_COLOR;
    }

    public static PDColor getNewDifColor() {
        return NEW_DIF_COLOR;
    }

    public static void setTextOldHighlightColor(float red, float green, float blue) {
        OLD_DIF_COLOR = new PDColor(new float[]{red, green, blue}, PDDeviceRGB.INSTANCE);
    }

    public static void setTextNewHighlightColor(float red, float green, float blue) {
        NEW_DIF_COLOR = new PDColor(new float[]{red, green, blue}, PDDeviceRGB.INSTANCE);
    }

    public static String getOS() {
        return OS;
    }

    public static String getLog() {
        return log;
    }

    public static CoparisoView getView() {
        return view;
    }

    public static void setView(CoparisoView view) {
        Setting.view = view;
    }

    public static void addLog(String log) {
        // add Date for check error log message
        LocalDateTime dateTime = LocalDateTime.now();
        String dateNow = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Setting.log += dateNow + " - " + log + "\n";
        System.out.println(dateNow + " - " + log);
    }

    public static void setReadLog(CoparisoView view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    view.logArea.setText(log);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        addLog("log reader Interrupted : " + ex.getMessage());
                    }
                }
            }
        }).start();
    }

    public static void writeLog() {
        // write Server log file
        String fileLog = "";
        try (Reader reader = new FileReader("serverLog.log")) {
            int ch = 0;
            while ((ch = reader.read()) != -1) {
                fileLog += (char) ch;
            }
        } catch (IOException ex) {
            addLog("log doesn't exists re-crate : " + ex.getMessage());
        }

        try (FileWriter writeFile = new FileWriter("serverLog.log")) {
            // Constructs a FileWriter given a file name, using the platform's default charset
            if (!fileLog.equals("null")) {
                log = fileLog + log;
            }
            writeFile.write(log);
        } catch (IOException ex) {
            System.out.println("write serverLog error : " + ex.getMessage());
        }
    }

    public static void readDB() {

        history = new ArrayList();
        JSONParser parser = new JSONParser();

        // read JSON form clientDB.json
        try (Reader reader = new FileReader("clientDB.json")) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            // System.out.println(jsonObject.toJSONString());
            DEFAULT_RESULT_FILE_PATH = Paths.get((String) jsonObject.get("resultPath"));

            // fetch Compare History Array in JSONArray and then add to jsonObject
            JSONArray cmp = (JSONArray) jsonObject.get("cmp");
            for (int i = 0; i < cmp.size(); i++) {
                JSONObject current = (JSONObject) cmp.get(i);

                history.add(new CmpHistory(
                        (String) current.get("date"),
                        (String) current.get("old"),
                        (String) current.get("new"),
                        (String) current.get("txtold"),
                        (String) current.get("txtnew"),
                        (String) current.get("oallcmp")
                ));
            }
        } catch (Exception ex) {
            System.out.println("database file not found!");
        }
    }

    public static void writeDB() {
        JSONObject jsonObject = new JSONObject();

        // add static setting in object
        jsonObject.put("resultPath", DEFAULT_RESULT_FILE_PATH.toString());

        // add Compare History Array in JSONArray and then add to dbObj
        JSONArray cmp = new JSONArray();
        history.forEach((history -> {
            JSONObject current = new JSONObject();

            current.put("date", history.getDate());
            current.put("old", history.getOldPath().toString());
            current.put("new", history.getNewPath().toString());
            current.put("txtold", history.getOldTextOnlyPath().toString());
            current.put("txtnew", history.getNewTextOnlyPath().toString());
            current.put("oallcmp", history.getOverallPath().toString());
            cmp.add(current);
        }));
        jsonObject.put("cmp", cmp);

        // write JSON to database file
        try (FileWriter file = new FileWriter("clientDB.json")) {
            // Constructs a FileWriter given a file name, using the platform's default charset
            file.write(jsonObject.toJSONString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
