import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PDFViewer {

    private PDFRender renderer;
    public JPanel panelSelectedPage;

    private int numberOfPages;
    private int currentPageIndex = 0;

    private int width;
    private int height;

    public JTextField txtPageNumber;
    public JButton btnLastPage;
    public JButton btnNextPage;
    public JButton btnPreviousPage;
    public JButton btnFirstPage;

    private void enableDisableButtons(int actionIndex) {
        switch (actionIndex) {
            case 0:
                btnFirstPage.setEnabled(false);
                btnPreviousPage.setEnabled(false);
                btnNextPage.setEnabled(true);
                btnLastPage.setEnabled(true);
                break;
            case 1:
                btnFirstPage.setEnabled(true);
                btnPreviousPage.setEnabled(true);
                btnNextPage.setEnabled(false);
                btnLastPage.setEnabled(false);
                break;
            default:
                btnFirstPage.setEnabled(true);
                btnPreviousPage.setEnabled(true);
                btnNextPage.setEnabled(true);
                btnLastPage.setEnabled(true);
        }
    }

    public PDFViewer(File document) throws Exception {
        initialize(document);
    }

    private void selectPage(int pageIndex) {
        BufferedImage renderImage = null;

        try {
            renderImage = renderer.renderImage(pageIndex, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        panelSelectedPage.removeAll(); // Remove children

        PDFImagePanel imagePanel = new PDFImagePanel(renderImage, width, height);
        imagePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        imagePanel.setLayout(new CardLayout(0, 0));
        imagePanel.setPreferredSize(new Dimension(width, height));
        panelSelectedPage.add(imagePanel, BorderLayout.CENTER);
        currentPageIndex = pageIndex;

        String pageText = String.format("Page: %d / %d", pageIndex + 1, numberOfPages);
        txtPageNumber.setText(pageText);

        if (pageIndex == 0) {
            enableDisableButtons(0);
        } else if (pageIndex == (numberOfPages - 1)) {
            enableDisableButtons(1);
        } else {
            enableDisableButtons(-1);
        }

        panelSelectedPage.revalidate();
        panelSelectedPage.repaint();

        System.out.println(imagePanel.getPreferredSize().width + " : " + imagePanel.getPreferredSize().height);
    }

    private void initialize(File file) throws Exception {

        PDDocument doc = PDDocument.load(file);

        // Getting/calculating screen dimensions...
        Float realWidth = new Float(doc.getPage(0).getMediaBox().getWidth());
        Float realHeight = new Float(doc.getPage(0).getMediaBox().getHeight());

        System.out.println(realWidth + ", " + realHeight);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Double ratio = 0.8;

        height = (int) (screenSize.getHeight() * ratio);
        width = (int) ((height * realWidth) / realHeight);

        numberOfPages = doc.getNumberOfPages();

        renderer = new PDFRender(doc);

        System.out.println("Number of pages = " + numberOfPages);

        btnFirstPage = new JButton("First Page");
        btnFirstPage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                selectPage(0);
            }
        });

        btnPreviousPage = new JButton("Previous Page");
        btnPreviousPage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPageIndex > 0) {
                    selectPage(currentPageIndex - 1);
                }
            }
        });

        txtPageNumber = new JTextField();
        txtPageNumber.setHorizontalAlignment(SwingConstants.CENTER);
        txtPageNumber.setEditable(false);
        txtPageNumber.setPreferredSize(new Dimension(50, txtPageNumber.getPreferredSize().width));
        txtPageNumber.setColumns(10);

        btnNextPage = new JButton("Next Page");
        btnNextPage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPageIndex < (numberOfPages - 1)) {
                    selectPage(currentPageIndex + 1);
                }
            }
        });

        btnLastPage = new JButton("Last Page");
        btnLastPage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectPage(numberOfPages - 1);
            }
        });

        panelSelectedPage = new JPanel();
        panelSelectedPage.setBackground(Color.LIGHT_GRAY);
        panelSelectedPage.setPreferredSize(new Dimension(width, height));
        panelSelectedPage.setBorder(new EmptyBorder(0, 0, 0, 0));
        panelSelectedPage.setLayout(new BorderLayout(0, 0));

        selectPage(0);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}