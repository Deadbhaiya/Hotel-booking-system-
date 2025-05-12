import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HotelBookingSystemGUI extends JFrame {
    private DefaultListModel<String> roomListModel = new DefaultListModel<>();
    private JList<String> roomList;
    private JTextField roomField, nameField;
    private JLabel statusLabel;

    private ArrayList<Room> rooms = new ArrayList<>();

    public HotelBookingSystemGUI() {
        setTitle("BookInn • Hotel Booking System");
        setSize(700, 550);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeRooms();
        initializeUI();
    }

    private void initializeRooms() {
        for (int i = 1; i <= 10; i++) {
            rooms.add(new Room(i, i % 2 == 0 ? "Deluxe" : "Standard"));
        }
        updateRoomList();
    }

    private void initializeUI() {
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel(" BookInn - Hotel Booking System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        mainPanel.add(title, BorderLayout.NORTH);

        // Room List
        roomList = new JList<>(roomListModel);
        roomList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roomList.setSelectionBackground(new Color(255, 204, 0));
        roomList.setCellRenderer(new RoomRenderer());
        JScrollPane scrollPane = new JScrollPane(roomList);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Controls
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(255, 255, 255, 180));
        controlPanel.setBorder(BorderFactory.createTitledBorder("📋 Manage Bookings"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        roomField = new JTextField();
        nameField = new JTextField();

        JButton bookButton = new JButton(" Book Room");
        JButton cancelButton = new JButton(" Cancel Booking");
        JButton saveButton = new JButton(" Save Bookings");

        styleButton(bookButton, new Color(76, 175, 80));
        styleButton(cancelButton, new Color(244, 67, 54));
        styleButton(saveButton, new Color(33, 150, 243));

        // Add Controls
        gbc.gridx = 0; gbc.gridy = 0; controlPanel.add(new JLabel("Room No:"), gbc);
        gbc.gridx = 1; controlPanel.add(roomField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; controlPanel.add(new JLabel("Guest Name:"), gbc);
        gbc.gridx = 1; controlPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        controlPanel.add(bookButton, gbc);
        gbc.gridy++;
        controlPanel.add(cancelButton, gbc);
        gbc.gridy++;
        controlPanel.add(saveButton, gbc);

        mainPanel.add(controlPanel, BorderLayout.EAST);

        // Status
        statusLabel = new JLabel("Welcome to BookInn!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        // Button Actions
        bookButton.addActionListener(e -> bookRoom());
        cancelButton.addActionListener(e -> cancelBooking());
        saveButton.addActionListener(e -> saveBookings());

        add(mainPanel);
    }

    private void updateRoomList() {
        roomListModel.clear();
        for (Room room : rooms) {
            String status = room.isBooked ? "Booked by " + room.guestName : "Available";
            roomListModel.addElement("Room " + room.roomNumber + " [" + room.type + "] - " + status);
        }
    }

    private void bookRoom() {
        try {
            int roomNumber = Integer.parseInt(roomField.getText().trim());
            String guestName = nameField.getText().trim();

            if (guestName.isEmpty()) {
                showStatus("Please enter guest name!", Color.YELLOW);
                return;
            }

            for (Room room : rooms) {
                if (room.roomNumber == roomNumber) {
                    if (!room.isBooked) {
                        room.bookRoom(guestName);
                        showStatus(" Room " + roomNumber + " booked for " + guestName, Color.GREEN);
                    } else {
                        showStatus(" Room already booked.", Color.ORANGE);
                    }
                    updateRoomList();
                    return;
                }
            }
            showStatus(" Room not found.", Color.RED);
        } catch (NumberFormatException e) {
            showStatus(" Invalid room number.", Color.RED);
        }
    }

    private void cancelBooking() {
        try {
            int roomNumber = Integer.parseInt(roomField.getText().trim());

            for (Room room : rooms) {
                if (room.roomNumber == roomNumber) {
                    if (room.isBooked) {
                        room.cancelBooking();
                        showStatus("Booking cancelled for room " + roomNumber, Color.PINK);
                    } else {
                        showStatus(" Room is not booked.", Color.ORANGE);
                    }
                    updateRoomList();
                    return;
                }
            }
            showStatus("Room not found.", Color.RED);
        } catch (NumberFormatException e) {
            showStatus("Invalid room number.", Color.RED);
        }
    }

    private void saveBookings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bookings.txt"))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String time = LocalDateTime.now().format(formatter);

            for (Room room : rooms) {
                if (room.isBooked) {
                    writer.write("Room no: " + room.roomNumber + ", Room type: " + room.type +
                            ", Guest name: " + room.guestName + ", Date & Time: " + time);
                    writer.newLine();
                }
            }
            showStatus("Bookings saved to file!", Color.CYAN);
        } catch (IOException e) {
            showStatus("Error saving file: " + e.getMessage(), Color.RED);
        }
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelBookingSystemGUI().setVisible(true));
    }

    class Room {
        int roomNumber;
        String type;
        boolean isBooked = false;
        String guestName = "";

        Room(int roomNumber, String type) {
            this.roomNumber = roomNumber;
            this.type = type;
        }

        void bookRoom(String guestName) {
            this.isBooked = true;
            this.guestName = guestName;
        }

        void cancelBooking() {
            this.isBooked = false;
            this.guestName = "";
        }
    }

    class GradientPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            Color c1 = new Color(30, 30, 60);
            Color c2 = new Color(60, 60, 90);
            GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    class RoomRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean focus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focus);
            String text = value.toString();
            if (text.contains("Booked")) label.setForeground(Color.RED);
            else label.setForeground(new Color(0, 128, 0));
            return label;
        }
    }
}
