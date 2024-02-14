package com.github.thibstars.netaware.desktop;

import com.github.thibstars.netaware.events.IpAddressFoundEvent;
import com.github.thibstars.netaware.events.MacFoundEvent;
import com.github.thibstars.netaware.events.TcpIpPortFoundEvent;
import com.github.thibstars.netaware.events.core.EventHandler;
import com.github.thibstars.netaware.events.core.EventManager;
import com.github.thibstars.netaware.scanners.IpScanner;
import com.github.thibstars.netaware.scanners.IpScannerInput;
import com.github.thibstars.netaware.scanners.MacScanner;
import com.github.thibstars.netaware.scanners.PortScanner;
import com.github.thibstars.netaware.utils.OptimalThreadPoolSizeCalculator;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import javax.swing.JProgressBar;
import javax.swing.table.DefaultTableModel;

/**
 * Service able to discover devices on the local network.
 * This service will scan for IP address, open TCP/IP ports and MAC address.
 *
 * @author Thibault Helsmoortel
 */
public class LocalDeviceService implements DeviceService {

    private final MainFrame mainFrame;
    private final EventManager eventManager;

    public LocalDeviceService(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.eventManager = new EventManager();
        DefaultTableModel tableModel = mainFrame.getTableModel();

        eventManager.registerHandler(TcpIpPortFoundEvent.class, event -> {
            InetAddress ipAddress = event.getIpAddress();
            Integer port = event.getTcpIpPort();

            IntStream.range(0, tableModel.getRowCount())
                    .filter(index -> tableModel.getValueAt(index, 0).equals(ipAddress.getHostAddress()))
                    .findFirst()
                    .ifPresent(index -> {
                        Object valueAt = tableModel.getValueAt(index, 1);
                        String newValue = valueAt != null && !((String) valueAt).isBlank() ? valueAt + ", " + port
                                : String.valueOf(port);
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
    }

    @Override
    public void discover() {
        IpScanner ipScanner = new IpScanner(eventManager);
        EventHandler<IpAddressFoundEvent> ipAddressFoundEventEventHandler = initIpAddressFoundEventEventHandler(ipScanner);
        eventManager.registerHandler(IpAddressFoundEvent.class, ipAddressFoundEventEventHandler);

        JProgressBar ipProgressBar = mainFrame.getIpProgressBar();
        ipProgressBar.setVisible(true);

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
    }

    private EventHandler<IpAddressFoundEvent> initIpAddressFoundEventEventHandler(IpScanner ipScanner) {
        PortScanner portScanner = new PortScanner(eventManager);
        MacScanner macScanner = new MacScanner(eventManager);

        return event -> {
            if (event instanceof IpAddressFoundEvent ipAddressFoundEvent && ipAddressFoundEvent.getSource().equals(ipScanner)) {
                InetAddress ipAddress = ipAddressFoundEvent.getIpAddress();
                String hostAddress = ipAddress.getHostAddress();
                mainFrame.getTableModel().addRow(new Object[]{hostAddress, "", ""});
                portScanner.scan(ipAddress);
                macScanner.scan(ipAddress);
            }
        };
    }
}
