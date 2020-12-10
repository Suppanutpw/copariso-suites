import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CoparisoView extends JFrame {
    public JButton oldBtn;
    public JButton newBtn;
    public JButton resultBtn;
    public JButton compareBtn;
    public JTextArea logArea;
    public JLabel oldLabel;
    public JLabel newLabel;
    public JLabel resultLabel;
    public JFileChooser fileChooser;
    public JButton textOnlyBtn, overallBtn;
    public JMenuItem history;
    private JScrollPane logScroll;
    private JPanel PDFPanel, topPanel, bottomPanel;
    private JMenuBar menuBar;
    private JMenu copariso;

    // Main GUI
    public CoparisoView() {
        setTitle("Copariso Suites");

        // menu bar
        menuBar = new JMenuBar();
        copariso = new JMenu("Copariso");
        history = new JMenuItem("History");

        copariso.add(history);
        menuBar.add(copariso);
        setJMenuBar(menuBar);

        // top Panel
        topPanel = new JPanel(new GridLayout(2, 3));

        oldBtn = new JButton("Choose old pdf file");
        newBtn = new JButton("Choose new pdf file");
        resultBtn = new JButton("Choose result save folder");

        oldLabel = new JLabel("Choose .pdf older version file", SwingConstants.CENTER);
        newLabel = new JLabel("Choose .pdf newer version file", SwingConstants.CENTER);
        resultLabel = new JLabel("Choose dir for save result file", SwingConstants.CENTER);

        topPanel.add(oldLabel);
        topPanel.add(newLabel);
        topPanel.add(resultLabel);

        topPanel.add(oldBtn);
        topPanel.add(newBtn);
        topPanel.add(resultBtn);

        // PDFPanel center
        PDFPanel = new JPanel(new BorderLayout());
        PDFPanel.setBorder(new TitledBorder("PDFViewer"));

        // bottom panel
        bottomPanel = new JPanel(new BorderLayout());

        logArea = new JTextArea();
        logScroll = new JScrollPane();
        logScroll.setViewportView(logArea);

        compareBtn = new JButton("Compare");
        bottomPanel.add(compareBtn, BorderLayout.CENTER);
        bottomPanel.add(logScroll, BorderLayout.SOUTH);

        // any config
        fileChooser = new JFileChooser();

        logArea.setEditable(false);
        logScroll.setPreferredSize(new Dimension(-1, 150));

        add(topPanel, BorderLayout.NORTH);
        add(PDFPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public CoparisoView(PDFViewer pdfViewer) {
        this();

        JScrollPane scrollPane;
        JPanel manageBtnPanel, pageBtnPanel;

        scrollPane = new JScrollPane(pdfViewer.panelSelectedPage);
        scrollPane.setPreferredSize(new Dimension(pdfViewer.getWidth(), 500));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        manageBtnPanel = new JPanel(new GridLayout(2, 1));

        pageBtnPanel = new JPanel(new GridLayout(1, 5));
        pageBtnPanel.add(pdfViewer.btnFirstPage);
        pageBtnPanel.add(pdfViewer.btnPreviousPage);
        pageBtnPanel.add(pdfViewer.txtPageNumber);
        pageBtnPanel.add(pdfViewer.btnNextPage);
        pageBtnPanel.add(pdfViewer.btnLastPage);

        textOnlyBtn = new JButton("Text-Only Compare");
        manageBtnPanel.add(pageBtnPanel);
        manageBtnPanel.add(textOnlyBtn);

        PDFPanel.add(scrollPane);
        bottomPanel.add(manageBtnPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public CoparisoView(PDFViewer pdfViewer1, PDFViewer pdfViewer2) {
        this();

        JScrollPane scrollPane1, scrollPane2;
        JPanel twoPDFPanel, manageBtnPanel, pageBtnPanel;

        twoPDFPanel = new JPanel(new GridLayout(1, 2));

        scrollPane1 = new JScrollPane(pdfViewer1.panelSelectedPage);
        scrollPane1.setPreferredSize(new Dimension(pdfViewer1.getWidth(), 500));
        scrollPane1.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane2 = new JScrollPane(pdfViewer2.panelSelectedPage);
        scrollPane2.setPreferredSize(new Dimension(pdfViewer2.getWidth(), 500));
        scrollPane2.getVerticalScrollBar().setUnitIncrement(16);

        manageBtnPanel = new JPanel(new GridLayout(2, 1));

        pageBtnPanel = new JPanel(new GridLayout(1, 10));
        pageBtnPanel.add(pdfViewer1.btnFirstPage);
        pageBtnPanel.add(pdfViewer1.btnPreviousPage);
        pageBtnPanel.add(pdfViewer1.txtPageNumber);
        pageBtnPanel.add(pdfViewer1.btnNextPage);
        pageBtnPanel.add(pdfViewer1.btnLastPage);

        pageBtnPanel.add(pdfViewer2.btnFirstPage);
        pageBtnPanel.add(pdfViewer2.btnPreviousPage);
        pageBtnPanel.add(pdfViewer2.txtPageNumber);
        pageBtnPanel.add(pdfViewer2.btnNextPage);
        pageBtnPanel.add(pdfViewer2.btnLastPage);

        overallBtn = new JButton("Overall Compare");
        manageBtnPanel.add(pageBtnPanel);
        manageBtnPanel.add(overallBtn);

        twoPDFPanel.add(scrollPane1);
        twoPDFPanel.add(scrollPane2);

        PDFPanel.add(twoPDFPanel);
        bottomPanel.add(manageBtnPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
