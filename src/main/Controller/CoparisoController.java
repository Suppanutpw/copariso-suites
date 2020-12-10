import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CoparisoController extends WindowAdapter implements ListSelectionListener, ActionListener {
    public CoparisoView view;
    private HistoryView historyView;
    private String oldTextOnlyPath;
    private String newTextOnlyPath;
    private String overallPath;
    private PDFViewer oldFileViewer, newFileViewer, overallViewer;

    // initial code when open in first time
    public CoparisoController() {
        view = new CoparisoView();
        Setting.setView(view);
        Setting.addLog("open Copariso Suites successfully");
        Setting.setHistory(new ArrayList<CmpHistory>());
        Setting.readDB();

        init();
    }

    // initial code when restart user view
    private void init() {
        // action handler
        view.oldBtn.addActionListener(this);
        view.newBtn.addActionListener(this);
        view.resultBtn.addActionListener(this);

        view.history.addActionListener(this);
        view.compareBtn.addActionListener(this);

        view.addWindowListener(this);

        view.resultLabel.setText(Setting.getDefaultResultPath());

        // init
        Setting.setView(view);
        Setting.updateLog();
    }

    // close view when it not null (i use to restart view)
    private void closeView() {
        if (historyView != null) {
            historyView.dispose();
        }
        if (view != null) {
            view.dispose();
        }
    }

    // choose .pdf file
    private void openPDFFile(JFileChooser fileChooser, JLabel label) {
        fileChooser.setDialogTitle("Choose your PDF file");
        int selectedButton = fileChooser.showDialog(view, "Open");
        if (selectedButton == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            if (path.toLowerCase().endsWith(".pdf") && (path.charAt(path.length() - "pdf".length() - 1)) == '.') {
                label.setText(path);
            } else {
                JOptionPane.showMessageDialog(view, "please select .pdf file", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // change view for Text-Only Compare Mode
    private void textOnlyMode() {
        try {
            oldFileViewer = new PDFViewer(new File(oldTextOnlyPath));
            newFileViewer = new PDFViewer(new File(newTextOnlyPath));
            closeView();
            view = new CoparisoView(oldFileViewer, newFileViewer);
            init();
            view.overallBtn.addActionListener(this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "result file not found", "Error Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // change view for Overall Compare Mode
    private void overallMode() {
        try {
            overallViewer = new PDFViewer(new File(overallPath));
            closeView();
            view = new CoparisoView(overallViewer);
            init();
            view.textOnlyBtn.addActionListener(this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "result file not found", "Error Message", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void compare(Path olderFilePath, Path newerFilePath) {
        // static dir for server side calculate Path
        Path calPath = Paths.get(Setting.getDefaultResultPath());
        if (!Files.exists(calPath)) {
            try {
                Setting.addLog("dir doesn't exists re-create: " + calPath.toString());
                Files.createDirectories(calPath);
            } catch (IOException ex) {
                Setting.addLog("target dir not found : " + ex.getMessage());
                return;
            }
        }

        // create PDFFile class
        PDFFile file1 = new PDFFile(olderFilePath.toString());
        PDFFile file2 = new PDFFile(newerFilePath.toString());
        if (!(file1.open() & file2.open())) {
            Setting.addLog("File Can't Open :");
            Setting.addLog(file1.getErrorMessage());
            Setting.addLog(file2.getErrorMessage());
        }

        Setting.addLog("compare " + file1.getTargetPath() + " and " + file2.getTargetPath());

        // get file name only and store for name result file
        String oldName = olderFilePath.getFileName().toString().split("[.]")[0];
        String newName = newerFilePath.getFileName().toString().split("[.]")[0];

        // get date now for set unique file name
        LocalDateTime dateTime = LocalDateTime.now();
        String dateNow = dateTime.format(DateTimeFormatter.ofPattern("_dd-MM-yyyy_HH-mm-ss_"));

        String oldTextOnlyFileName = "older_" + oldName + dateNow + newName + ".pdf";
        String newTextOnlyFileName = "newer_" + oldName + dateNow + newName + ".pdf";
        String overallFileName = "overall_" + oldName + dateNow + newName + ".pdf";

        // set unique file name with date for fix duplicate file name
        file1.setResultFileName(oldTextOnlyFileName);
        file2.setResultFileName(newTextOnlyFileName);

        PDFTextOnlyCompare txtOnlyTask = new PDFTextOnlyCompare(file1, file2);
        PDFOverallCompare overallTask = new PDFOverallCompare(file1, file2, overallFileName);

        // ใช้ Thread แบ่งงาน compare 2 ตัว
        Thread textOnlyCmp = new Thread(txtOnlyTask);
        Thread overallCmp = new Thread(overallTask);

        textOnlyCmp.start();
        overallCmp.start();

        while (textOnlyCmp.isAlive() || overallCmp.isAlive()) {
        }

        oldTextOnlyPath = file1.getResultPath();
        newTextOnlyPath = file2.getResultPath();
        overallPath = overallTask.getOverallPathPDF();
        textOnlyMode();

        // save All Compare File in database (Model CmpHistory)
        dateNow = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        CmpHistory history = new CmpHistory(
                dateNow,
                olderFilePath.toString(),
                newerFilePath.toString(),
                oldTextOnlyPath,
                newTextOnlyPath,
                overallPath
        );
        Setting.getHistory().add(0, history);

        Setting.addLog("compare success enjoy!");
    }

    // save DB when window close
    @Override
    public void windowClosing(WindowEvent e) {
        Setting.addLog("close Copariso Suites successfully");
        Setting.writeLog();
        Setting.writeDB();
    }

    //for accessing past comparison by clicking on table data
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // show compare result history
            oldTextOnlyPath = Setting.getHistory().get(historyView.table.getSelectedRow()).getOldTextOnlyPath();
            newTextOnlyPath = Setting.getHistory().get(historyView.table.getSelectedRow()).getNewTextOnlyPath();
            overallPath = Setting.getHistory().get(historyView.table.getSelectedRow()).getOverallPath();
            textOnlyMode();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(view.oldBtn)) {
            openPDFFile(view.fileChooser, view.oldLabel);
        } else if (e.getSource().equals(view.newBtn)) {
            openPDFFile(view.fileChooser, view.newLabel);
        } else if (e.getSource().equals(view.resultBtn)) {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
            fileChooser.setDialogTitle("Choose you folder for saving");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int selectedButton = fileChooser.showDialog(null, "Open");
            if (selectedButton == JFileChooser.APPROVE_OPTION) {
                if (Setting.getOS().indexOf("win") >= 0) {
                    // if client os is window
                    view.resultLabel.setText(fileChooser.getSelectedFile().getPath());
                } else {
                    // if client os is mac
                    view.resultLabel.setText(fileChooser.getSelectedFile().getParent());
                }
                Setting.setDefaultResultPath(Paths.get(view.resultLabel.getText()).toString());
            }
        } else if (e.getSource().equals(view.compareBtn)) {
            if (!Files.exists(Paths.get(view.oldLabel.getText()))) {
                JOptionPane.showMessageDialog(view, "please choose older version file", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            } else if (!Files.exists(Paths.get(view.newLabel.getText()))) {
                JOptionPane.showMessageDialog(view, "please choose newer version file", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            } else if (!Files.exists(Paths.get(view.resultLabel.getText()))) {
                JOptionPane.showMessageDialog(view, "please choose save result dir", "Error Message", JOptionPane.INFORMATION_MESSAGE);
            } else {
                compare(Paths.get(view.oldLabel.getText()), Paths.get(view.newLabel.getText()));
            }
        } else if (e.getSource().equals(view.history)) {
            historyView = new HistoryView(this);
        } else if (e.getSource().equals(view.textOnlyBtn)) {
            textOnlyMode();
        } else if (e.getSource().equals(view.overallBtn)) {
            overallMode();
        }
    }
}
