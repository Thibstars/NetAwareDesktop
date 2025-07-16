package com.github.thibstars.netaware.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

/**
 * The main JFrame used to display the core functionality of the application.
 *
 * @author Thibault Helsmoortel
 */
public class MainFrame extends JFrame {

    private final DefaultTableModel tableModel;
    private final JProgressBar ipProgressBar;
    private final JTable resultTable;
    private final transient DeviceService localDeviceService;

    public MainFrame() throws HeadlessException {
        setTitle("NetAware Desktop");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension dimension = new Dimension(750, 525);
        setSize(dimension);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("IP Address");
        tableModel.addColumn("Open Ports");
        tableModel.addColumn("MAC Address");
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setMaximumSize(new Dimension((int) dimension.getWidth(), (int) dimension.getHeight() - 100));
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(dimension);
        mainPanel.setLayout(new BorderLayout());
        ipProgressBar = new JProgressBar();
        ipProgressBar.setToolTipText("Scan in progress...");
        ipProgressBar.setIndeterminate(true);
        ipProgressBar.setVisible(false);
        JPanel pnlProgressBars = new JPanel();
        JPanel pnlProgressBarsContent = new JPanel(new BorderLayout());
        pnlProgressBarsContent.add(new JLabel("Scanning network. This may take a while."), BorderLayout.PAGE_START);
        pnlProgressBarsContent.add(ipProgressBar, BorderLayout.PAGE_END);
        pnlProgressBars.add(pnlProgressBarsContent);
        ipProgressBar.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                pnlProgressBars.setVisible(true);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                super.componentHidden(e);
                pnlProgressBars.setVisible(false);
            }
        });
        mainPanel.add(pnlProgressBars, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.PAGE_END);
        add(mainPanel);

        try {
            BufferedImage applicationIcon = ImageIO.read(
                    new File(
                            Objects.requireNonNull(
                                    this.getClass().getClassLoader().getResource("NetAware.png")
                            ).getFile()
                    )
            );
            setIconImage(resize(applicationIcon, 64, 64));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setVisible(true);

        this.localDeviceService = new LocalDeviceService(this);

        discoverDevices();
    }

    private void discoverDevices() {
        resultTable.removeAll();
        localDeviceService.discover();

        ipProgressBar.setVisible(false);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JProgressBar getIpProgressBar() {
        return ipProgressBar;
    }

    public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        Image temporaryImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage rescaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = rescaledImage.createGraphics();
        g2d.drawImage(temporaryImage, 0, 0, null);
        g2d.dispose();

        return rescaledImage;
    }
}
