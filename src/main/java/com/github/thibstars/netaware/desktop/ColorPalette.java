package com.github.thibstars.netaware.desktop;

import java.awt.Color;

/**
 * Color palette constants for the NetAware Desktop application.
 * 
 * @author Thibault Helsmoortel
 */
public class ColorPalette {
    
    // Main colors
    public static final Color WHITE = Color.decode("#ffffff");  // White (background)
    public static final Color MEDIUM_BLUE = Color.decode("#0756bb");  // Medium blue

    // Variations of blue
    public static final Color DEEP_BLUE = Color.decode("#004eb8");  // Deep blue

    // Variations of cyan
    public static final Color LIGHT_CYAN = Color.decode("#00bfd8");  // Slightly lighter cyan
    
    // Derived colors for UI components
    public static final Color BACKGROUND_COLOR = WHITE;
    public static final Color TEXT_COLOR = DEEP_BLUE;
    public static final Color PROGRESS_BAR_COLOR = MEDIUM_BLUE;
    public static final Color TABLE_HEADER_BACKGROUND = LIGHT_CYAN;
    public static final Color TABLE_HEADER_FOREGROUND = DEEP_BLUE;
    public static final Color TABLE_HEADER_HOVER_BACKGROUND = MEDIUM_BLUE;
    public static final Color TABLE_HEADER_HOVER_FOREGROUND = WHITE;
    public static final Color TABLE_SELECTION_BACKGROUND = LIGHT_CYAN;
    public static final Color TABLE_SELECTION_FOREGROUND = DEEP_BLUE;
}