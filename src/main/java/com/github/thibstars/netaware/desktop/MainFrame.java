package com.github.thibstars.netaware.desktop;

import com.github.thibstars.netaware.events.IpAddressFoundEvent;
import com.github.thibstars.netaware.events.core.EventManager;
import com.github.thibstars.netaware.scanners.IpScanner;
import com.github.thibstars.netaware.scanners.IpScannerInput;
import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

/**
 * @author Thibault Helsmoortel
 */
public class MainFrame extends JFrame {

    private final DefaultTableModel tableModel;

    public MainFrame() throws HeadlessException {
        setTitle("NetAware Desktop");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension dimension = new Dimension(750, 500);
        setSize(dimension);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("IP Address");
        JTable resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(dimension);
        mainPanel.add(scrollPane);
        add(mainPanel);

        discoverDevices();

        setVisible(true);
    }

    private void discoverDevices() {
        EventManager eventManager = new EventManager();

        eventManager.registerHandler(
                IpAddressFoundEvent.class,
                event -> {
                    // Add result to table
                    tableModel.addRow(new Object[]{event.getIpAddress().getHostAddress()});
                });

        IpScanner ipScanner = new IpScanner(eventManager);
        ipScanner.scan(new IpScannerInput("192.168.1.0", 254));
    }
}
