import backend.Database;
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


public class Main extends JFrame {
    private JTextField nombreField;
    private JTextField numeroField;
    private JTextField edadField;
    private JTextArea resultArea;

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
        JButton guardarButton = new JButton("Guardar");
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
            String nombre = nombreField.getText();
            String numero = numeroField.getText();
            String edadStr = edadField.getText();
            
            if(nombre.isEmpty() || numero.isEmpty() || edadStr.isEmpty()){
                resultArea.setText("Todos los campos son oblicatrorios.");
                return;
            }
            
            try {
                int edad = Integer.parseInt(edadStr);
                Persona persona = new Persona(nombre, edad, numero);
                
                try {
                    Lexer lexer = new Lexer(persona.getNumeroTelefono());
                    List<Token> tokens = lexer.tokenize();
                    Parser parser = new Parser(tokens);
                    parser.parse();
                    
                    saveToDatabase(persona);
                    resultArea.setText("Datos guardados exitosamente:\n" + persona);
                    
                    // Vaciar los campos después de guardar
                    nombreField.setText("");
                    edadField.setText("");
                    numeroField.setText("");
                    
                } catch (Exception ex) {
                    resultArea.setText("Formato de número telefónico incorrecto: " + ex.getMessage());
                }
                
            } catch (NumberFormatException ex) {
                resultArea.setText("La edad debe ser un numero.");
            }
        }
    }

    private class MostrarRegistrosButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame registrosFrame = new JFrame("Registros Guardados");
            registrosFrame.setSize(400, 300);
            registrosFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JTextArea registrosArea = new JTextArea();
            registrosArea.setEditable(false);

            try (Connection conn = Database.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM persona");
                 ResultSet rs = pstmt.executeQuery()) {

                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt("id"))
                      .append(", Nombre: ").append(rs.getString("nombre"))
                      .append(", Edad: ").append(rs.getInt("edad"))
                      .append(", Teléfono: ").append(rs.getString("telefono"))
                      .append("\n");
                }
                registrosArea.setText(sb.toString());
            } catch (SQLException ex) {
                registrosArea.setText("Error al obtener registros: " + ex.getMessage());
            }

            registrosFrame.add(new JScrollPane(registrosArea), BorderLayout.CENTER);
            registrosFrame.setVisible(true);
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