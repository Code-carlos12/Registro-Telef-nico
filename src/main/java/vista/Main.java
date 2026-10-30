package vista;

import backend.Database;
import backend.FormatoTelefonoException;
import backend.Lexer;
import backend.Parser;
import backend.Persona;
import backend.Token;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Main extends JFrame {
    private JTextField nombreField;
    private JTextField numeroField;
    private JTextField edadField;
    private JTextArea resultArea;
    private JButton guardarButton;

    public Main() {
        setTitle("Registro Telefónico");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        JLabel nombreLabel = new JLabel("Nombre:");
        nombreField = new JTextField();
        JLabel edadLabel = new JLabel("Edad:");
        edadField = new JTextField();
        JLabel numeroLabel = new JLabel("Número Telefónico:");
        numeroField = new JTextField();
        numeroField.setToolTipText("Formato: (123) 456-7890");
        guardarButton = new JButton("Guardar");
        JButton mostrarRegistrosButton = new JButton("Mostrar Registros");

        guardarButton.addActionListener(new GuardarButtonListener());
        mostrarRegistrosButton.addActionListener(new MostrarRegistrosButtonListener());

        inputPanel.add(nombreLabel);
        inputPanel.add(nombreField);
        inputPanel.add(edadLabel);
        inputPanel.add(edadField);
        inputPanel.add(numeroLabel);
        inputPanel.add(numeroField);
        inputPanel.add(guardarButton);
        inputPanel.add(mostrarRegistrosButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    private class GuardarButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String nombre = nombreField.getText().trim();
            String numero = numeroField.getText().trim();
            String edadStr = edadField.getText().trim();

            if (nombre.isEmpty() || numero.isEmpty() || edadStr.isEmpty()) {
                resultArea.setText("Todos los campos son obligatorios.");
                return;
            }

            int edad;
            try {
                edad = Integer.parseInt(edadStr);
            } catch (NumberFormatException ex) {
                resultArea.setText("La edad debe ser un número.");
                return;
            }
            if (edad < 0 || edad > 150) {
                resultArea.setText("La edad debe estar entre 0 y 150.");
                return;
            }

            try {
                Lexer lexer = new Lexer(numero);
                List<Token> tokens = lexer.tokenize();
                new Parser(tokens).parse();
            } catch (FormatoTelefonoException ex) {
                resultArea.setText("Formato de número telefónico incorrecto (use (123) 456-7890): "
                        + ex.getMessage());
                return;
            }

            Persona persona = new Persona(nombre, edad, numero);
            guardarButton.setEnabled(false);
            resultArea.setText("Guardando...");

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws SQLException {
                    saveToDatabase(persona);
                    return null;
                }

                @Override
                protected void done() {
                    guardarButton.setEnabled(true);
                    try {
                        get();
                        resultArea.setText("Datos guardados exitosamente:\n" + persona);

                        // Vaciar los campos después de guardar
                        nombreField.setText("");
                        edadField.setText("");
                        numeroField.setText("");
                    } catch (InterruptedException | ExecutionException ex) {
                        Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                        resultArea.setText("Error al guardar en la base de datos: " + causa.getMessage());
                    }
                }
            }.execute();
        }
    }

    private class MostrarRegistrosButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame registrosFrame = new JFrame("Registros Guardados");
            registrosFrame.setSize(400, 300);
            registrosFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JTextArea registrosArea = new JTextArea("Cargando...");
            registrosArea.setEditable(false);
            registrosFrame.add(new JScrollPane(registrosArea), BorderLayout.CENTER);
            registrosFrame.setVisible(true);

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws SQLException {
                    return loadRegistros();
                }

                @Override
                protected void done() {
                    try {
                        String registros = get();
                        registrosArea.setText(registros.isEmpty() ? "No hay registros." : registros);
                    } catch (InterruptedException | ExecutionException ex) {
                        Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                        registrosArea.setText("Error al obtener registros: " + causa.getMessage());
                    }
                }
            }.execute();
        }
    }

    private String loadRegistros() throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, nombre, edad, telefono FROM persona");
             ResultSet rs = pstmt.executeQuery()) {

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                  .append(", Nombre: ").append(rs.getString("nombre"))
                  .append(", Edad: ").append(rs.getInt("edad"))
                  .append(", Teléfono: ").append(rs.getString("telefono"))
                  .append("\n");
            }
            return sb.toString();
        }
    }

    private void saveToDatabase(Persona persona) throws SQLException {
        String sql = "INSERT INTO persona (nombre, edad, telefono) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, persona.getNombre());
            pstmt.setInt(2, persona.getEdad());
            pstmt.setString(3, persona.getNumeroTelefono());
            pstmt.executeUpdate();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
