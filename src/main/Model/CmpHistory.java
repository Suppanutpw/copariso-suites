import java.nio.file.Path;
import java.nio.file.Paths;

public class CmpHistory {

    // DB Model there have 6 field in this DB (it will store in clientDB.json)
    private Path oldPath; // user selected old file path
    private Path newPath; // user selected new file path
    private Path oldTextOnlyPath; // result for old text-only compare path
    private Path newTextOnlyPath; // result for new text-only compare path
    private Path overallPath; // result for overall compare path
    private String date; // time that user compare these files

    public CmpHistory(String date, String oldPath, String newPath, String oldTextOnlyPath, String newTextOnlyPath, String overallPath) {
        this.date = date;
        this.oldPath = Paths.get(oldPath);
        this.newPath = Paths.get(newPath);
        this.oldTextOnlyPath = Paths.get(oldTextOnlyPath);
        this.newTextOnlyPath = Paths.get(newTextOnlyPath);
        this.overallPath = Paths.get(overallPath);
    }

    public String getOldTextOnlyPath() {
        return oldTextOnlyPath.toString();
    }

    public String getNewTextOnlyPath() {
        return newTextOnlyPath.toString();
    }

    public String getOverallPath() {
        return overallPath.toString();
    }

    public String getOldPath() {
        return oldPath.toString();
    }

    public String getNewPath() {
        return newPath.toString();
    }

    public String getDate() {
        return date;
    }
}
