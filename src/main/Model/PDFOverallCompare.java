import de.redsix.pdfcompare.PdfComparator;

import javax.swing.*;
import java.nio.file.Paths;

public class PDFOverallCompare implements Runnable {

    private PdfComparator overallCmp;
    private String errorMessage;
    private String overallFileName;
    private PDFFile olderFilePath, newerFilePath;

    public PDFOverallCompare(PDFFile olderFilePath, PDFFile newerFilePath, String overallFileName) {
        this.olderFilePath = olderFilePath;
        this.newerFilePath = newerFilePath;
        this.overallFileName = overallFileName;
    }

    public boolean pdfCompare(PDFFile file1, PDFFile file2) {
        try {
            // use thread for share job with text-only compare (they're independent method)
            overallCmp = new PdfComparator(file2.getTargetPath(), file1.getTargetPath());
            overallCmp.compare().writeTo(getOverallPath());

            Setting.addLog("Copariso created overall compare file : " + getOverallPath());

            // if there no error here so return null
            errorMessage = null;
        } catch (Exception ex) {
            // if there have error you can get cause message
            errorMessage = ex.getMessage();
        }

        // if there have no error message then send true
        return errorMessage == null;
    }

    // setter & getter for overall file attribute
    // getOverallPath there no .pdf
    private String getOverallPath() {
        return Paths.get(Setting.getDefaultResultPath(), overallFileName).toString();
    }

    // have .pdf in path
    public String getOverallPathPDF() {
        return Paths.get(Setting.getDefaultResultPath(), overallFileName + ".pdf").toString();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void run() {
        if (!pdfCompare(olderFilePath, newerFilePath)) {
            Setting.addLog("Overall Compare Error : " + errorMessage);
            JOptionPane.showMessageDialog(Setting.getView(), "Overall Compare Error " + errorMessage, "Error Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
