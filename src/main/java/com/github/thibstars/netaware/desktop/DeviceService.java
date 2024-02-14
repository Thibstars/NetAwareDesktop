package com.github.thibstars.netaware.desktop;

/**
 * Service contract for discovering devices.
 *
 * @author Thibault Helsmoortel
 */
public interface DeviceService {

    /**
     * Method starting the scan to discover devices.
     */
    void discover();

}
