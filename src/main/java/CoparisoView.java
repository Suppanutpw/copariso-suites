import javax.swing.*;

public class CoparisoView extends JFrame {
    private JButton oldBtn;
    private JButton newBtn;
    private JButton resultBtn;
    private JButton compareButton;
    private JTextArea textArea1;
    private JPanel interfacePanel;
    private JPanel topBtnPanel;
    private JPanel topLabelPanel;
    private JPanel oldLPanel;
    private JPanel newLPanel;
    private JPanel resultLPanel;
    private JPanel comparePanel;
    private JScrollPane pdfViewerPanel;
    private JScrollPane logPanel;

    public CoparisoView() {
        add(interfacePanel);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
}
