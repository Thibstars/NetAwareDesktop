package com.github.thibstars.netaware.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

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
    
    // Track which column header is being hovered over
    private int hoveredColumn = -1;

    public MainFrame() throws HeadlessException {
        // Set custom NetAware look and feel
        try {
            UIManager.setLookAndFeel(new NetAwareLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            // Fall back to default look and feel if custom look and feel is not supported
            System.err.println("Could not set NetAware look and feel: " + e.getMessage());
        }
        
        setTitle("NetAware Desktop");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension dimension = new Dimension(750, 525);
        setSize(dimension);
        setLocationRelativeTo(null);
        
        // Set frame background color
        getContentPane().setBackground(ColorPalette.BACKGROUND_COLOR);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("IP Address");
        tableModel.addColumn("Open Ports");
        tableModel.addColumn("MAC Address");
        
        // Configure table with more native look
        resultTable = new JTable(tableModel);
        // Keep minimal styling to maintain consistency with color scheme
        resultTable.setGridColor(ColorPalette.LIGHT_CYAN);
        // Set the table text color to match the "Scanning network" text
        resultTable.setForeground(ColorPalette.TEXT_COLOR);
        
        // Configure table header with hover effect
        JTableHeader header = resultTable.getTableHeader();
        
        // Set the grid color between header cells to white
        header.setBackground(ColorPalette.TABLE_HEADER_BACKGROUND);
        resultTable.setGridColor(ColorPalette.WHITE);
        
        // Create and set the custom header renderer
        HoverHeaderRenderer headerRenderer = new HoverHeaderRenderer();
        for (int i = 0; i < resultTable.getColumnCount(); i++) {
            TableColumn column = resultTable.getColumnModel().getColumn(i);
            column.setHeaderRenderer(headerRenderer);
        }
        
        // Add mouse listener to track hover state
        header.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point point = e.getPoint();
                int column = resultTable.columnAtPoint(point);
                if (column != hoveredColumn) {
                    hoveredColumn = column;
                    header.repaint();
                }
            }
        });
        
        // Reset hover state when mouse exits the header
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredColumn = -1;
                header.repaint();
            }
        });
        
        // Configure scroll pane with custom colors
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setMaximumSize(new Dimension((int) dimension.getWidth(), (int) dimension.getHeight() - 100));
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorPalette.MEDIUM_BLUE, 1));
        scrollPane.getViewport().setBackground(ColorPalette.BACKGROUND_COLOR);
        
        // Configure main panel with custom colors and vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(dimension);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(ColorPalette.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Configure progress bar with custom colors
        ipProgressBar = new JProgressBar();
        ipProgressBar.setToolTipText("Scan in progress...");
        ipProgressBar.setIndeterminate(true);
        ipProgressBar.setVisible(false);
        ipProgressBar.setForeground(ColorPalette.PROGRESS_BAR_COLOR);
        ipProgressBar.setBackground(ColorPalette.BACKGROUND_COLOR);
        
        // Configure progress panels with custom colors
        JPanel pnlProgressBars = new JPanel();
        pnlProgressBars.setBackground(ColorPalette.BACKGROUND_COLOR);
        pnlProgressBars.setLayout(new BorderLayout());
        
        JPanel pnlProgressBarsContent = new JPanel(new BorderLayout());
        pnlProgressBarsContent.setBackground(ColorPalette.BACKGROUND_COLOR);
        pnlProgressBarsContent.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel scanLabel = new JLabel("Scanning network. This may take a while.");
        scanLabel.setForeground(ColorPalette.TEXT_COLOR);
        
        pnlProgressBarsContent.add(scanLabel, BorderLayout.PAGE_START);
        pnlProgressBarsContent.add(ipProgressBar, BorderLayout.PAGE_END);
        pnlProgressBars.add(pnlProgressBarsContent, BorderLayout.CENTER);
        
        // Create a container panel for both progress bar and table
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ColorPalette.BACKGROUND_COLOR);
        
        // Set a fixed height for the progress bar panel to prevent it from being hidden
        // Reduced height from 80 to 60 to remove some space above the progress bar
        pnlProgressBars.setPreferredSize(new Dimension(dimension.width, 60));
        
        // Add progress bar at the top and table below it
        contentPanel.add(pnlProgressBars, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
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
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
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
    
    /**
     * Custom renderer for table headers that changes colors on hover.
     */
    private class HoverHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            // Center the header text
            setHorizontalAlignment(SwingConstants.CENTER);
            
            // Apply hover effect if this is the hovered column
            if (column == hoveredColumn) {
                component.setBackground(ColorPalette.TABLE_HEADER_HOVER_BACKGROUND);
                component.setForeground(ColorPalette.TABLE_HEADER_HOVER_FOREGROUND);
            } else {
                component.setBackground(ColorPalette.TABLE_HEADER_BACKGROUND);
                component.setForeground(ColorPalette.TABLE_HEADER_FOREGROUND);
            }
            
            return component;
        }
    }
}
