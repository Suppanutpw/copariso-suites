import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;

public class HistoryView extends JFrame {

    private final String column[] = {"Date", "Old File", "New File"};
    public JTable table;
    private String[][] data;

    public HistoryView(CoparisoController controller) {
        this.setLayout(new BorderLayout());
        this.setTitle("Copariso History");

        data = createTable(Setting.getHistory());

        table = new JTable(data, column);
        table.setDefaultEditor(Object.class, null);
        table.getSelectionModel().addListSelectionListener(controller);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);

        this.add(new JPanel(), BorderLayout.NORTH);
        this.add(scrollPane);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(800, 400);
        this.setVisible(true);
    }

    //method for creating data for table
    private String[][] createTable(ArrayList<CmpHistory> history) {
        String[][] a = new String[history.size()][3];
        for (int i = 0; i < history.size(); i++) {
            a[i][0] = history.get(i).getDate();
            a[i][1] = history.get(i).getNewPath();
            a[i][2] = history.get(i).getOldPath();
        }
        return a;
    }
}
