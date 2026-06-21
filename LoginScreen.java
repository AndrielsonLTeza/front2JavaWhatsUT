import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.security.MessageDigest;

public class LoginScreen extends JFrame {
    private JTextField txtUsuario;
    private JTextField txtIP;
    private JPasswordField txtSenha;
    private JButton btnEntrar;

    public LoginScreen() {
        setTitle("WhatsUT - Conectar em Rede");
        setSize(380, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitulo = new JLabel("WhatsUT Network");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setForeground(new Color(33, 150, 243));
        
        txtIP = new JTextField("127.0.0.1"); // Padrão local, mude para o IP do outro PC no teste
        txtIP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        txtUsuario = new JTextField();
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        txtSenha = new JPasswordField();
        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        btnEntrar = new JButton("Conectar e Entrar");
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnEntrar.addActionListener(e -> conectarERodar());

        panel.add(lblTitulo);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(new JLabel("IP do Servidor Back-End:"));
        panel.add(txtIP);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(new JLabel("Usuário / RA:"));
        panel.add(txtUsuario);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(new JLabel("Senha:"));
        panel.add(txtSenha);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(btnEntrar);

        add(panel);
    }

    private void conectarERodar() {
        String ip = txtIP.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (usuario.isEmpty() || ip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha o IP e o Usuário!");
            return;
        }

        try {
            // Criptografia da senha (Requisito 1)
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            String senhaCripto = sb.toString();

            // CONEXÃO RMI REAL: Busca o objeto remoto no IP digitado
            String urlRMI = "rmi://" + ip + ":1099/WhatsUT";
            Object servidorRemoto = Naming.lookup(urlRMI); 
            
            this.dispose();
            // Abre o chat passando a conexão real estabelecida
            new ChatScreen(usuario, servidorRemoto, ip).setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível conectar ao servidor RMI no IP: " + ip + "\nErro: " + ex.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }
}