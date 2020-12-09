import java.util.ArrayList;

/* Dynamic Programming Java implementation of LCS problem */
public class PDFCompareText {

    private ArrayList<Integer> pdfTextPos1, pdfTextPos2;
    private PDFFile file1, file2;

    public PDFCompareText(PDFFile file1, PDFFile file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    // Function to return all LCS of X[0..m-1], Y[0..n-1] but it not an answer!!! you have to reverse it
    public String LCS(String X, String Y, int m, int n) {
        int[][] L = new int[m + 1][n + 1];

        // Following steps build L[m+1][n+1] in bottom up fashion. Note
        // that L[i][j] contains length of LCS of X[0..i-1] and Y[0..j-1]
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0)
                    L[i][j] = 0;
                else if (X.charAt(i - 1) == Y.charAt(j - 1))
                    L[i][j] = L[i - 1][j - 1] + 1;
                else
                    L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]);
            }
        }

        // Following code is used to print LCS
        int index = L[m][n];

        // Create a character array to store the lcs string
        char[] lcs = new char[index + 1];
        lcs[index] = '\u0000'; // Set the terminating character

        // Start from the right-most-bottom-most corner and
        // one by one store characters in lcs[]
        int i = m;
        int j = n;
        while (i > 0 && j > 0) {
            // If current character in X[] and Y are same, then
            // current character is part of LCS
            if (X.charAt(i - 1) == Y.charAt(j - 1)) {
                // Put current character in result
                lcs[index - 1] = X.charAt(i - 1);

                // save match subsequence position for calculate opposite highlight pos
                pdfTextPos1.add(i - 1);
                pdfTextPos2.add(j - 1);

                // reduce values of i, j and index
                i--;
                j--;
                index--;
            }

            // If not same, then find the larger of two and
            // go in the direction of larger value
            else if (L[i - 1][j] > L[i][j - 1])
                i--;
            else
                j--;
        }

        return new String(lcs);
    }

    public void findNotMatchPos() {

        // if two file text all same
        if (file1.getFileText().equals(file2.getFileText())) {
            return;
        }

        int m = file1.textLength(), n = file2.textLength();

        // initialize position of subString
        pdfTextPos1 = new ArrayList();
        pdfTextPos2 = new ArrayList();

        // find longest common sequence
        LCS(file1.getFileText(), file2.getFileText(), m, n);

        // find range of {not longest common sequence}
        file1.setHighlightPos(findRangeForHighlight(pdfTextPos1, m));
        file2.setHighlightPos(findRangeForHighlight(pdfTextPos2, n));


        // extends {not longest common sequence} range in full word
        findRangeForHighlightWords(file1);
        findRangeForHighlightWords(file2);
    }

    public ArrayList<PDFHighlightPos> findRangeForHighlight(ArrayList<Integer> pdfTextPos, int textSize) {
        // find position Start - End for highlight word
        int pdfTextPosSize = pdfTextPos.size();
        ArrayList<PDFHighlightPos> pdfHighlightPos = new ArrayList<PDFHighlightPos>();

        // if {not subsequence words} at first of file
        if (pdfTextPos.get(pdfTextPosSize - 1) > 1) {
            pdfHighlightPos.add(new PDFHighlightPos(1, pdfTextPos.get(pdfTextPosSize - 1) - 1));
        }

        // find {not subsequence words} position and change it as range for highlight
        for (int i = pdfTextPosSize - 2; i >= 0; i--) {
            if (pdfTextPos.get(i) - pdfTextPos.get(i + 1) != 1) {
                pdfHighlightPos.add(new PDFHighlightPos(pdfTextPos.get(i + 1) + 1, pdfTextPos.get(i) - 1));
            }
        }

        // if {not subsequence words} at end of file
        if (textSize - 1 - pdfTextPos.get(0) > 1) {
            pdfHighlightPos.add(new PDFHighlightPos(pdfTextPos.get(0) + 1, textSize - 2));
        }

        return pdfHighlightPos;
    }

    public void findRangeForHighlightWords(PDFFile file) {
        String fileText = file.getFileText();
        ArrayList<PDFHighlightPos> pdfHighlightPos = file.getHighlightPos();
        int textSize = file.textLength(), highlight_length = pdfHighlightPos.size();

        if (highlight_length == 0) {
            return;
        }

        // expends position Start - End for highlight all word (highlight all word that have modified)
        for (PDFHighlightPos highlight : pdfHighlightPos) {
            // find back Start position for {not digit and alphabet}
            for (int i = highlight.posStart; i >= 0; i--) {
                if (!(Character.isLetter(fileText.charAt(i)) || Character.isDigit(fileText.charAt(i)))) {
                    highlight.posStart = Math.min(highlight.posStart, i + 1);
                    break;
                }
            }

            // find forward Stop position for {not digit and alphabet}
            for (int i = highlight.posStop; i < textSize; i++) {
                if (!(Character.isLetter(fileText.charAt(i)) || Character.isDigit(fileText.charAt(i)))) {
                    highlight.posStop = Math.max(highlight.posStop, i - 1);
                    break;
                }
            }
        }

        // if first or last word have modify in range of 5 characters (average of english word length)
        if (pdfHighlightPos.get(0).posStart <= 5) {
            pdfHighlightPos.get(0).posStart = 0;
        }
        if (pdfHighlightPos.get(highlight_length - 1).posStop >= textSize - 5) {
            pdfHighlightPos.get(highlight_length - 1).posStop = textSize - 2;
        }

        // fix overlap range because of expends range for word
        for (int i = 0; i < pdfHighlightPos.size() - 1; i++) {
            // if Stop position of current range overlap with Start of next range
            // or distance between Stop position of current range and with Start of next range <= 5
            if (Math.abs(pdfHighlightPos.get(i).posStop - pdfHighlightPos.get(i + 1).posStart) <= 5
                    || pdfHighlightPos.get(i + 1).posStart <= pdfHighlightPos.get(i).posStop) {
                // then combine it together (combine in current range and delete next range)
                pdfHighlightPos.get(i).posStop = pdfHighlightPos.get(i + 1).posStop;
                pdfHighlightPos.remove(i + 1);
                i--; // if there have overlap re-find a same position
            }
        }

    }
}