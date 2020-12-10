import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.io.*;
import java.net.URISyntaxException;
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
    private static Path DEFAULT_DATABASE_PATH;
    private static PDColor OLD_DIF_COLOR;
    private static PDColor NEW_DIF_COLOR;
    private static String log = "";
    private static ArrayList<CmpHistory> history;
    private static CoparisoView view;

    static {
        // ตั้งค่าของที่อยู่ไฟล์ผลลัพธ์
        // modify result path here!!!
        // default now is ./resources
        DEFAULT_DATABASE_PATH = Paths.get(System.getProperty("user.home"));
        try {
            DEFAULT_DATABASE_PATH = Paths.get(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath()).getParent();
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(view, "current dir not found DB will save in : " + DEFAULT_DATABASE_PATH, "Warning Message", JOptionPane.INFORMATION_MESSAGE);
        }
        DEFAULT_RESULT_FILE_PATH = DEFAULT_DATABASE_PATH;
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

    // get database path in String
    public static String getDefaultDatabasePath() {
        return DEFAULT_DATABASE_PATH.toString();
    }

    public static void setDefaultDatabasePath(Path defaultDatabasePath) {
        DEFAULT_DATABASE_PATH = defaultDatabasePath;
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

    public static CoparisoView getView() {
        return view;
    }

    public static void setView(CoparisoView view) {
        Setting.view = view;
    }

    public static ArrayList<CmpHistory> getHistory() {
        return history;
    }

    public static void setHistory(ArrayList<CmpHistory> history) {
        Setting.history = history;
    }

    public static void addLog(String log) {
        // add Date for check error log message
        LocalDateTime dateTime = LocalDateTime.now();
        String dateNow = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Setting.log += dateNow + " - " + log + "\n";
        System.out.println(dateNow + " - " + log);
        updateLog();
    }

    public static void updateLog() {
        view.logArea.setText(Setting.log);
    }

    public static void writeLog() {
        // write copariso log file
        String fileLog = "";
        try (Reader reader = new FileReader(Paths.get(getDefaultDatabasePath(), "coparisoLog.log").toString())) {
            int ch = 0;
            while ((ch = reader.read()) != -1) {
                fileLog += (char) ch;
            }
        } catch (IOException ex) {
            addLog("log doesn't exists re-crate : " + ex.getMessage());
        }

        try (FileWriter writeFile = new FileWriter(Paths.get(getDefaultDatabasePath(), "coparisoLog.log").toString())) {
            // Constructs a FileWriter given a file name, using the platform's default charset
            if (!fileLog.equals("null")) {
                log = fileLog + log;
            }
            writeFile.write(log);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean readDB() {

        history = new ArrayList();
        JSONParser parser = new JSONParser();

        // read JSON form coparisoDB.json
        try (Reader reader = new FileReader(Paths.get(getDefaultDatabasePath(), "coparisoDB.json").toString())) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            // System.out.println(jsonObject.toJSONString());
            DEFAULT_RESULT_FILE_PATH = Paths.get((String) jsonObject.get("resultPath"));
            DEFAULT_DATABASE_PATH = Paths.get((String) jsonObject.get("dbPath"));

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
            Setting.addLog("Welcome to Copariso Suites Power By SPW");
            return false;
        }
        return true;
    }

    public static void writeDB() {
        JSONObject jsonObject = new JSONObject();

        // add static setting in object
        jsonObject.put("resultPath", DEFAULT_RESULT_FILE_PATH.toString());
        jsonObject.put("dbPath", DEFAULT_DATABASE_PATH.toString());

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
        try (FileWriter file = new FileWriter(Paths.get(getDefaultDatabasePath(), "coparisoDB.json").toString())) {
            // Constructs a FileWriter given a file name, using the platform's default charset
            file.write(jsonObject.toJSONString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
