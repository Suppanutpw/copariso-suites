public class PDFTextOnlyCompare {

    private String errorMessage;
    private PDFCompareText cmpText;
    private PDFHighlighter highlighterFile1, highlighterFile2;

    public boolean pdfCompare(PDFFile file1, PDFFile file2) {
        try {
            // get file name add create new 2 result file names
            // ex. old-oldFileName-newFileName.pdf
            // ex. new-oldFileName-newFileName.pdf

            // find highlight position in word range form
            cmpText = new PDFCompareText(file1, file2);
            cmpText.findNotMatchPos();

            // you can share 2 PDFHighlighter.highlight() to each Thread
            highlighterFile1 = new PDFHighlighter(file1);
            highlighterFile1.highlight(Setting.getOldDifColor());
            Setting.addLog("Copariso created old-new text-only compare file : " + file1.getResultPath());

            highlighterFile2 = new PDFHighlighter(file2);
            highlighterFile2.highlight(Setting.getNewDifColor());
            Setting.addLog("Copariso created new-old text-only compare file : " + file2.getResultPath());

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

}
