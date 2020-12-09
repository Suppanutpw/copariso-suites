import de.redsix.pdfcompare.PdfComparator;

import java.nio.file.Paths;

public class PDFOverallCompare {

    private PdfComparator overallCmp;
    private String errorMessage;
    private String overallFileName;

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

    public void setOverallFileName(String overallFileName) {
        this.overallFileName = overallFileName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
