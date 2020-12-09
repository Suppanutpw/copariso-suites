import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PDFFile {
    private Path targetPath;
    private Path resultPath; // result for text-only compare
    private String fileText;
    private ArrayList<PDFHighlightPos> highlightPos;
    private String errorMessage;

    // For read pdf file text
    private PDFParser parser;
    private PDFTextStripper pdfStripper;
    private PDDocument pdDoc;
    private COSDocument cosDoc;
    private File file;

    public PDFFile(String targetPath) {
        // set Path for open file
        setTargetPath(targetPath);

        // set highlightPos
        highlightPos = new ArrayList<PDFHighlightPos>();
    }

    public boolean open() {
        try {
            // get PDF text file
            fileText = toText();
            errorMessage = null;
        } catch (Exception ex) {
            // if there have error you can get cause message
            errorMessage = ex.getMessage();
        }

        return errorMessage == null;
    }

    private String toText() throws IOException {
        this.pdfStripper = null;
        this.pdDoc = null;
        this.cosDoc = null;

        // Open PDF file with PDFParser
        file = new File(targetPath.toString());
        parser = new PDFParser(new RandomAccessFile(file, "r")); // update for PDFBox V 2.0

        // read PDDocument text via pdfStripper
        parser.parse();
        cosDoc = parser.getDocument();
        pdfStripper = new PDFTextStripper();
        pdDoc = new PDDocument(cosDoc);
        pdDoc.getNumberOfPages();
        pdfStripper.setStartPage(0);
        pdfStripper.setEndPage(pdDoc.getNumberOfPages());
        fileText = pdfStripper.getText(pdDoc);
        pdDoc.close(); // close file

        return fileText;
    }

    public void setResultFileName(String resultFileName) {
        this.resultPath = Paths.get(Setting.getDefaultResultPath(), resultFileName);
    }

    public String getTargetPath() {
        return targetPath.toString();
    }

    // if you want to change selected file
    public void setTargetPath(String targetPath) {
        // set selected file path
        this.targetPath = Paths.get(targetPath);
    }

    public String getResultPath() {
        return resultPath.toString();
    }

    public String getFileText() {
        return fileText;
    }

    public ArrayList<PDFHighlightPos> getHighlightPos() {
        return highlightPos;
    }

    public void setHighlightPos(ArrayList<PDFHighlightPos> highlightPos) {
        this.highlightPos = highlightPos;
    }

    public int textLength() {
        return fileText.length();
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}