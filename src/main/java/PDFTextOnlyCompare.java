import javax.swing.*;

public class PDFTextOnlyCompare implements Runnable {

    private String errorMessage;
    private PDFCompareText cmpText;
    private PDFHighlighter highlighterFile1, highlighterFile2;
    private PDFFile olderFile, newerFile;

    public PDFTextOnlyCompare(PDFFile olderFilePath, PDFFile newerFilePath) {
        this.olderFile = olderFilePath;
        this.newerFile = newerFilePath;
    }

    public boolean pdfCompare() {
        try {
            // get file name add create new 2 result file names
            // ex. old-oldFileName-newFileName.pdf
            // ex. new-oldFileName-newFileName.pdf
            // find highlight position in word range form
            cmpText = new PDFCompareText(olderFile, newerFile);
            cmpText.findNotMatchPos();

            // you can share 2 PDFHighlighter.highlight() to each Thread
            highlighterFile1 = new PDFHighlighter(olderFile);
            highlighterFile1.highlight(Setting.getOldDifColor());
            Setting.addLog("Copariso created old-new text-only compare file : " + olderFile.getResultPath());

            highlighterFile2 = new PDFHighlighter(newerFile);
            highlighterFile2.highlight(Setting.getNewDifColor());
            Setting.addLog("Copariso created new-old text-only compare file : " + newerFile.getResultPath());

            // if there no error here so return null
            errorMessage = null;
        } catch (Exception ex) {
            // if there have error you can get cause message
            errorMessage = "PDF text-only compare error : " + ex.getMessage();
            Setting.addLog(errorMessage);
        }

        // if there have no error message then send true
        return errorMessage == null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void run() {
        if (!pdfCompare()) {
            JOptionPane.showMessageDialog(Setting.getView(), "Text-only Error : " + errorMessage , "Error Message", JOptionPane.ERROR_MESSAGE);
        }
    }
}
