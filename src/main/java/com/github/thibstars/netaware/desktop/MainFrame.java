package com.github.thibstars.netaware.desktop;

import com.github.thibstars.netaware.events.IpAddressFoundEvent;
import com.github.thibstars.netaware.events.MacFoundEvent;
import com.github.thibstars.netaware.events.TcpIpPortFoundEvent;
import com.github.thibstars.netaware.events.core.EventManager;
import com.github.thibstars.netaware.scanners.IpScanner;
import com.github.thibstars.netaware.scanners.IpScannerInput;
import com.github.thibstars.netaware.scanners.MacScanner;
import com.github.thibstars.netaware.scanners.PortScanner;
import com.github.thibstars.netaware.utils.OptimalThreadPoolSizeCalculator;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

/**
 * @author Thibault Helsmoortel
 */
public class MainFrame extends JFrame {

    private final DefaultTableModel tableModel;
    private final JProgressBar iPProgressBar;

    public MainFrame() throws HeadlessException {
        setTitle("NetAware Desktop");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension dimension = new Dimension(750, 500);
        setSize(dimension);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("IP Address");
        tableModel.addColumn("Open Ports");
        tableModel.addColumn("MAC Address");
        JTable resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(dimension);
        mainPanel.setLayout(new BorderLayout());
        iPProgressBar = new JProgressBar();
        iPProgressBar.setToolTipText("Scan in progress...");
        iPProgressBar.setIndeterminate(true);
        iPProgressBar.setVisible(false);
        JPanel pnlProgressBars = new JPanel();
        pnlProgressBars.add(iPProgressBar);
        mainPanel.add(pnlProgressBars, BorderLayout.PAGE_START);
        mainPanel.add(scrollPane, BorderLayout.PAGE_END);
        add(mainPanel);

        setVisible(true);

        discoverDevices();
    }

    private void discoverDevices() {
        EventManager eventManager = new EventManager();

        eventManager.registerHandler(TcpIpPortFoundEvent.class, event -> {
            InetAddress ipAddress = event.getIpAddress();
            Integer port = event.getTcpIpPort();

            IntStream.range(0, tableModel.getRowCount())
                    .filter(index -> tableModel.getValueAt(index, 0).equals(ipAddress.getHostAddress()))
                    .findFirst()
                    .ifPresent(index -> {
                        Object valueAt = tableModel.getValueAt(index, 1);
                        String newValue = valueAt != null && !((String) valueAt).isBlank() ? valueAt + ", " + port : String.valueOf(port);
                        tableModel.setValueAt(newValue, index, 1);
                    });
        });

        eventManager.registerHandler(MacFoundEvent.class, event -> {
            InetAddress ipAddress = event.getIpAddress();
            String macAddress = event.getMacAddress();

            IntStream.range(0, tableModel.getRowCount())
                    .filter(index -> tableModel.getValueAt(index, 0).equals(ipAddress.getHostAddress()))
                    .findFirst()
                    .ifPresent(index -> tableModel.setValueAt(macAddress, index, 2));
        });

        IpScanner ipScanner = new IpScanner(eventManager);
        PortScanner portScanner = new PortScanner(eventManager);
        MacScanner macScanner = new MacScanner(eventManager);

        eventManager.registerHandler(
                IpAddressFoundEvent.class,
                event -> {
                    if (event instanceof IpAddressFoundEvent ipAddressFoundEvent) {
                        InetAddress ipAddress = ipAddressFoundEvent.getIpAddress();
                        String hostAddress = ipAddress.getHostAddress();
                        tableModel.addRow(new Object[]{hostAddress, "", ""});
                        portScanner.scan(ipAddress);
                        macScanner.scan(ipAddress);
                    }
                });

        iPProgressBar.setVisible(true);

        OptimalThreadPoolSizeCalculator optimalThreadPoolSizeCalculator = new OptimalThreadPoolSizeCalculator();
        int optimalThreadPoolSize = optimalThreadPoolSizeCalculator.get(0.9, 1000, 2);
        try (ExecutorService executorService = Executors.newFixedThreadPool(optimalThreadPoolSize)) {
            executorService.submit(() -> {
                String classABase = "10.0.";
                int amountOfIpsToScan = 255;
                for (int i = 0; i < amountOfIpsToScan; i++) {
                    ipScanner.scan(new IpScannerInput(classABase + i + ".0", amountOfIpsToScan));
                }
            });
            executorService.submit(() -> {
                String classBBase = "172.16.";
                int amountOfIpsToScan = 255;
                for (int i = 0; i < 16; i++) {
                    ipScanner.scan(new IpScannerInput(classBBase + i + ".0", amountOfIpsToScan));
                }
            });
            executorService.submit(() -> {
                String classCBase = "192.168.";
                int amountOfIpsToScan = 255;
                for (int i = 0; i < amountOfIpsToScan; i++) {
                    ipScanner.scan(new IpScannerInput(classCBase + i + ".0", amountOfIpsToScan));
                }
            });
            executorService.shutdown();
        }

        iPProgressBar.setVisible(false);
    }
}
