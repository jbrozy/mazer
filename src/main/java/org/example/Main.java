package org.example;

import com.illposed.osc.OSCBadDataEvent;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacketEvent;
import com.illposed.osc.OSCPacketListener;
import com.illposed.osc.transport.OSCPortIn;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final AtomicBoolean isStarted = new AtomicBoolean(false);
    private static Thread listenerThread;
    private static OSCPortIn oscPortIn;
    private static final InternalState internalState = new InternalState();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    public static void addRandomColors(GridCell[][] grid) {
        Random random = new Random();
        var colors = new Color[]{Color.ORANGE, Color.BLUE, Color.GREEN};

        for (var color : colors) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(grid.length); // Random row
                int col = random.nextInt(grid[0].length); // Random column

                // Check if the cell is empty (assuming `getColor` returns null if unoccupied)
                if (grid[row][col].getCellColor() == Color.WHITE) {
                    grid[row][col].setCellColor(color);        // Set the logical color
                    grid[row][col].setBackground(color);       // Update the visual background
                    grid[row][col].repaint();                 // Ensure Swing repaints the cell
                    placed = true; // Exit the loop
                }
            }
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Grid visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textPane);
        tabbedPane.addTab("Console", scrollPane);

        JPanel visualizationPanel = new JPanel();
        visualizationPanel.setLayout(new BorderLayout());
        tabbedPane.addTab("Visualize", visualizationPanel);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("OSC");
        JMenuItem toggleButton = new JMenuItem("Start OSC Listener");
        toggleButton.addActionListener((ActionEvent e) -> {
            if (isStarted.compareAndSet(false, true)) {
                toggleButton.setText("Stop");
                appendColoredText(textPane, "Started listening.\n", Color.BLACK);
                try {
                    oscPortIn = new OSCPortIn(new InetSocketAddress("127.0.0.1", 9000));
                    oscPortIn.addPacketListener(new OSCPacketListener() {
                        @Override
                        public void handlePacket(OSCPacketEvent oscPacketEvent) {
                            if(oscPacketEvent.getPacket() instanceof OSCMessage msg){
                                SwingUtilities.invokeLater(() -> {
                                    appendColoredText(textPane, "Received Message on: %s\n".formatted(msg.getAddress()), Color.GREEN);
                                    if(msg.getAddress().equals("/state/init/")){
                                        int rows = (int)msg.getArguments().get(0);
                                        int cols = (int)msg.getArguments().get(1);
                                        internalState.field = new int[rows][cols];

                                        visualizationPanel.removeAll();

                                        var generator = new LabyrinthGenerator(rows, cols);
                                        var gridPanel = generator.generateLabyrinth();
                                        var grid = gridPanel.getGameCells();
                                        addRandomColors(grid);

                                        new Thread(() -> {
                                            var optimalPath = AStarPathfinder.findOptimalPath(grid[0][0]);
                                            assert optimalPath != null;
                                            AStarPathfinder.visualizePath(optimalPath);
                                        }).start();

                                        visualizationPanel.add(gridPanel, BorderLayout.CENTER);
                                        visualizationPanel.revalidate();
                                        visualizationPanel.repaint();
                                    }
                                    if(msg.getAddress().equals("/state/update")){
                                        // Handle state updates here
                                    }
                                });
                            }
                        }

                        @Override
                        public void handleBadData(OSCBadDataEvent oscBadDataEvent) {
                            SwingUtilities.invokeLater(() ->
                                    appendColoredText(textPane, oscBadDataEvent.toString(), Color.RED)
                            );
                        }
                    });
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                listenerThread = new Thread(() -> {
                    oscPortIn.startListening();
                    oscPortIn.run();
                });
                listenerThread.start();
            } else {
                toggleButton.setText("Start OSC Listener");
                isStarted.set(false);
                appendColoredText(textPane, "Stopped listening.\n", Color.BLACK);
                try {
                    oscPortIn.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        toggleButton.doClick();

        fileMenu.add(toggleButton);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private static void appendColoredText(JTextPane textPane, String text, Color color) {
        StyledDocument doc = textPane.getStyledDocument();
        Style style = textPane.addStyle("ColoredStyle", null);
        StyleConstants.setForeground(style, color);

        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}