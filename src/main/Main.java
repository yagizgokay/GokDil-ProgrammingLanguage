// === main/Main.java ===
package main;

import interpreter.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.swing.*;
import lexer.*;
import parser.*;

public class Main {
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("GökDil - Türkçe Programlama Dili");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon logoIcon = null;
        try {
            // Logo dosyasını yükle
            Path logoPath = Path.of("src/resources/logo.png");
            if (Files.exists(logoPath)) {
                byte[] imageData = Files.readAllBytes(logoPath);
                logoIcon = new ImageIcon(imageData);
                Image img = logoIcon.getImage();
                // Logo boyutunu ayarla
                Image newImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(newImg);
            } else {
                System.err.println("Logo dosyası bulunamadı: " + logoPath.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Logo yüklenemedi: " + e.getMessage());
        }

        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            topPanel.add(logoLabel);
            topPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Logo ile başlık arasına boşluk ekle
        }

        JLabel titleLabel = new JLabel("GökDil");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Türkçe Programlama Dili");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        topPanel.add(subtitleLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JButton openButton = new JButton("Test Dosyası Seç");
        openButton.setPreferredSize(new Dimension(200, 40));
        openButton.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel statusLabel = new JLabel("Lütfen bir test dosyası seçin", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            int result = fileChooser.showOpenDialog(frame);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                executeProgram(selectedFile.getPath(), statusLabel);
            }
        });

        centerPanel.add(openButton, gbc);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        centerPanel.add(statusLabel, gbc);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void executeProgram(String filePath, JLabel statusLabel) {
        try {
            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                statusLabel.setText("⚠️ Dosya bulunamadı: " + path.toAbsolutePath());
                return;
            }

            String source = Files.readString(path);
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.tokenize(source);

            if (tokens.isEmpty()) {
                statusLabel.setText("❌ Lexer hiç token üretemedi.");
                return;
            }

            // Token hatası kontrolü
            for (Token token : tokens) {
                if (token.type == TokenType.ERROR) {
                    statusLabel.setText("❌ Geçersiz karakter: '" + token.value + "' (Satır: " + token.line + ")");
                    return;
                }
            }

            Parser parser = new Parser();
            ASTNode ast = parser.parse(tokens);
            
            // Çıktıları yakalamak için PrintStream'i değiştir
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));
            
            try {
                Interpreter interpreter = new Interpreter();
                interpreter.execute(ast);
                
                // Çıktıları al ve göster
                String output = outputStream.toString();
                if (!output.isEmpty()) {
                    JTextArea outputArea = new JTextArea(output);
                    outputArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(outputArea);
                    scrollPane.setPreferredSize(new Dimension(400, 300));
                    
                    JDialog dialog = new JDialog();
                    dialog.setTitle("Program Çıktısı");
                    dialog.setLayout(new BorderLayout());
                    dialog.add(scrollPane, BorderLayout.CENTER);
                    dialog.pack();
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                }
                
                statusLabel.setText("✅ Program başarıyla çalıştırıldı.");
            } finally {
                System.setOut(originalOut);
            }

        } catch (IOException e) {
            statusLabel.setText("❌ Dosya okuma hatası: " + e.getMessage());
        } catch (RuntimeException e) {
            statusLabel.setText("🚨 Hata: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            // Eğer komut satırı argümanı varsa, eski şekilde çalıştır
        try {
            Path path = Path.of(args[0]);
            if (!Files.exists(path)) {
                System.err.println("⚠️ Dosya bulunamadı: " + path.toAbsolutePath());
                return;
            }

            String source = Files.readString(path);
            System.out.println("Kaynak kod:");
            System.out.println(source);
            System.out.println("-------------------");

            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.tokenize(source);

            System.out.println("Token sayısı: " + tokens.size());

            if (tokens.isEmpty()) {
                    System.err.println("❌ Lexer hiç token üretemedi.");
                return;
            }

            System.out.println("== TOKENLER ==");
            for (Token token : tokens) {
                System.out.println(token);
            }
            System.out.println("================");

            Parser parser = new Parser();
            ASTNode ast = parser.parse(tokens);
            System.out.println("== AST ==");
            System.out.println(ast);    

            Interpreter interpreter = new Interpreter();
            interpreter.execute(ast);

            System.out.println("✅ Program başarıyla çalıştırıldı.");

        } catch (IOException e) {
            System.err.println("❌ Dosya okuma hatası: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("🚨 Hata: " + e.getMessage());
            }
        } else {
            // GUI modunda çalıştır
            SwingUtilities.invokeLater(() -> createAndShowGUI());
        }
    }
}
