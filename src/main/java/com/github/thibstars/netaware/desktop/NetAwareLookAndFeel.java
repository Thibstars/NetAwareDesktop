package com.github.thibstars.netaware.desktop;

import javax.swing.UIDefaults;
import javax.swing.plaf.BorderUIResource.EmptyBorderUIResource;
import javax.swing.plaf.BorderUIResource.LineBorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Custom Look and Feel for the NetAware Desktop application.
 * This class extends MetalLookAndFeel to provide a consistent visual style
 * using the application's color palette.
 *
 * @author Thibault Helsmoortel
 */
public class NetAwareLookAndFeel extends MetalLookAndFeel {

    /**
     * Returns the ID of this look and feel.
     *
     * @return the ID "NetAware"
     */
    @Override
    public String getID() {
        return "NetAware";
    }

    /**
     * Returns the name of this look and feel.
     *
     * @return the name "NetAware Look and Feel"
     */
    @Override
    public String getName() {
        return "NetAware Look and Feel";
    }

    /**
     * Returns a description of this look and feel.
     *
     * @return the description
     */
    @Override
    public String getDescription() {
        return "A custom look and feel for the NetAware Desktop application";
    }

    /**
     * Initialize the 'defaults' table with custom colors and settings.
     */
    @Override
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        // Add any custom UI delegates here if needed
    }

    /**
     * Initialize the component defaults with custom colors and settings.
     *
     * @param table the 'defaults' table to initialize
     */
    @Override
    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);

        // Convert regular Colors to ColorUIResource for Look and Feel
        ColorUIResource backgroundColor = new ColorUIResource(ColorPalette.BACKGROUND_COLOR);
        ColorUIResource textColor = new ColorUIResource(ColorPalette.TEXT_COLOR);
        ColorUIResource progressBarColor = new ColorUIResource(ColorPalette.PROGRESS_BAR_COLOR);
        ColorUIResource tableHeaderBackground = new ColorUIResource(ColorPalette.TABLE_HEADER_BACKGROUND);
        ColorUIResource tableHeaderForeground = new ColorUIResource(ColorPalette.TABLE_HEADER_FOREGROUND);
        ColorUIResource tableSelectionBackground = new ColorUIResource(ColorPalette.TABLE_SELECTION_BACKGROUND);
        ColorUIResource tableSelectionForeground = new ColorUIResource(ColorPalette.TABLE_SELECTION_FOREGROUND);
        ColorUIResource mediumBlue = new ColorUIResource(ColorPalette.MEDIUM_BLUE);
        ColorUIResource lightCyan = new ColorUIResource(ColorPalette.LIGHT_CYAN);
        ColorUIResource white = new ColorUIResource(ColorPalette.WHITE);

        // Define custom defaults
        Object[] defaults = {
            // Panel defaults
            "Panel.background", backgroundColor,
            
            // Progress bar defaults
            "ProgressBar.foreground", progressBarColor,
            "ProgressBar.selectionBackground", progressBarColor,
            "ProgressBar.selectionForeground", backgroundColor,
            "ProgressBar.background", backgroundColor,
            
            // Table defaults
            "Table.gridColor", white,
            "Table.foreground", textColor,
            "Table.background", backgroundColor,
            "Table.selectionBackground", tableSelectionBackground,
            "Table.selectionForeground", tableSelectionForeground,
            
            // Table header defaults
            "TableHeader.background", tableHeaderBackground,
            "TableHeader.foreground", tableHeaderForeground,
            "TableHeader.cellBorder", createEmptyBorder(),
            
            // Label defaults
            "Label.foreground", textColor,
            
            // ScrollPane defaults
            "ScrollPane.background", backgroundColor,
            "ScrollPane.border", createLineBorder(mediumBlue),
            
            // ScrollBar defaults - modernized appearance
            "ScrollBar.width", 12,
            "ScrollBar.background", backgroundColor,
            "ScrollBar.foreground", lightCyan,
            "ScrollBar.track", white,
            "ScrollBar.thumb", lightCyan,
            "ScrollBar.thumbDarkShadow", mediumBlue,
            "ScrollBar.thumbHighlight", lightCyan,
            "ScrollBar.thumbShadow", mediumBlue,
            "ScrollBar.trackHighlight", white,
            "ScrollBar.minimumThumbSize", new java.awt.Dimension(12, 24),
            "ScrollBar.maximumThumbSize", new java.awt.Dimension(12, 1000),
            "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE,
            
            // Other component defaults can be added here
        };
        
        table.putDefaults(defaults);
    }
    
    /**
     * Creates an empty border for use in the look and feel.
     *
     * @return an empty border
     */
    private Object createEmptyBorder() {
        return new EmptyBorderUIResource(1, 1, 1, 1);
    }
    
    /**
     * Creates a line border for use in the look and feel.
     *
     * @param color the border color
     * @return a line border
     */
    private Object createLineBorder(ColorUIResource color) {
        return new LineBorderUIResource(color, 1);
    }
}