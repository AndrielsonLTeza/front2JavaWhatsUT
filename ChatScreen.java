import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.util.List;

public class ChatScreen extends JFrame {
    private String usuarioLogado;
    private String ipServidor;
    private Object servidorRMI; // Referência do seu Back-end do Git
    private WhatsUTCallback callbackLocal;

    private JLabel lblStatusChat;
    private JTextArea areaMensagens;
    private JTextField txtNovaMensagem;
    private DefaultListModel<String> modelUsuarios;
    private DefaultListModel<String> modelGrupos;
    private JList<String> listaUsuarios;
    private JList<String> listaGrupos;

    public ChatScreen(String usuario, Object servidor, String ip) {
        this.usuarioLogado = usuario;
        this.servidorRMI = servidor;
        this.ipServidor = ip;

        setTitle("WhatsUT - Usuário: " + usuarioLogado);
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- INTERFACE GRÁFICA ---
        JTabbedPane abas = new JTabbedPane();
        
        modelUsuarios = new DefaultListModel<>();
        modelUsuarios.addElement(usuarioLogado + " [Você]");
        listaUsuarios = new JList<>(modelUsuarios);
        abas.addTab("Usuários Online", new JScrollPane(listaUsuarios));

        modelGrupos = new DefaultListModel<>();
        modelGrupos.addElement("Grupo Geral WhatsUT");
        modelGrupos.addElement("Projeto UTFPR");
        listaGrupos = new JList<>(modelGrupos);
        abas.addTab("Grupos", new JScrollPane(listaGrupos));

        JPanel painelCentral = new JPanel(new BorderLayout());
        lblStatusChat = new JLabel("Selecione um contato ou grupo para conversar.");
        lblStatusChat.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        painelCentral.add(lblStatusChat, BorderLayout.NORTH);

        areaMensagens = new JTextArea();
        areaMensagens.setEditable(false);
        painelCentral.add(new JScrollPane(areaMensagens), BorderLayout.CENTER);

        // Alternar modos de chat de forma dinâmica
        listaUsuarios.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaUsuarios.getSelectedValue() != null) {
                listaGrupos.clearSelection();
                lblStatusChat.setText("💬 Chat Privado com: " + listaUsuarios.getSelectedValue());
            }
        });

        listaGrupos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaGrupos.getSelectedValue() != null) {
                listaUsuarios.clearSelection();
                lblStatusChat.setText("👥 Chat em Grupo: " + listaGrupos.getSelectedValue());
            }
        });

        JPanel painelEnvio = new JPanel(new BorderLayout(5, 5));
        painelEnvio.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton btnAnexar = new JButton("📎");
        txtNovaMensagem = new JTextField();
        JButton btnEnviar = new JButton("Enviar");

        painelEnvio.add(btnAnexar, BorderLayout.WEST);
        painelEnvio.add(txtNovaMensagem, BorderLayout.CENTER);
        painelEnvio.add(btnEnviar, BorderLayout.EAST);
        painelCentral.add(painelEnvio, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, abas, painelCentral);
        split.setDividerLocation(240);
        add(split);

        btnEnviar.addActionListener(e -> transmitirMensagem());
        txtNovaMensagem.addActionListener(e -> transmitirMensagem());
        btnAnexar.addActionListener(e -> transmitirArquivo());

        vincularCallbackNoServidor();
    }

    private void vincularCallbackNoServidor() {
        try {
            // Instancia o callback que vai ficar ouvindo a rede neste PC
            callbackLocal = new WhatsUTCallbackImpl(this);
            
            // Exemplo de como registrar o callback usando reflexão ou chamada direta se souber o método do seu back:
            // ((SuaInterface)servidorRMI).registrarCallback(usuarioLogado, callbackLocal);
            
            areaMensagens.append("[Sistema]: Conectado ao servidor remoto em " + ipServidor + "\n");
        } catch (Exception e) {
            areaMensagens.append("[Aviso]: Rodando com chamadas locais (Callback pendente de mapeamento).\n");
        }
    }

    private void transmitirMensagem() {
        String texto = txtNovaMensagem.getText().trim();
        if (texto.isEmpty()) return;

        boolean ehGrupo = listaGrupos.getSelectedValue() != null;
        String destino = ehGrupo ? listaGrupos.getSelectedValue() : listaUsuarios.getSelectedValue();

        if (destino == null) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário ou grupo nas abas laterais!");
            return;
        }

        // Mostra o envio na própria tela
        areaMensagens.append("Você para [" + destino + "]: " + texto + "\n");
        txtNovaMensagem.setText("");

        // Aqui você faz o roteamento chamando os métodos do seu Back-End compilado no JAR:
        // Exemplo: ((SuaInterface)servidorRMI).enviarMensagem(usuarioLogado, destino, texto, ehGrupo);
    }

    private void transmitirArquivo() {
        if (listaUsuarios.getSelectedValue() == null) {
            JOptionPane.showMessageDialog(this, "O envio de arquivos é permitido apenas em chats privados.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = fc.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(arquivo)) {
                byte[] buffer = fis.readAllBytes();
                areaMensagens.append("📎 Arquivo '" + arquivo.getName() + "' (" + buffer.length + " bytes) enviado para " + listaUsuarios.getSelectedValue() + "\n");
                
                // Envia os bytes brutos pela rede usando a assinatura do método do seu back-end:
                // ((SuaInterface)servidorRMI).enviarArquivo(usuarioLogado, listaUsuarios.getSelectedValue(), arquivo.getName(), buffer);
            } catch (Exception ex) {
                System.out.println("Erro ao ler arquivo.");
            }
        }
    }

    public void adicionarMensagemNaTela(String emissor, String mensagem, boolean emGrupo) {
        areaMensagens.append((emGrupo ? "[GRUPO] " : "") + emissor + ": " + mensagem + "\n");
    }

    public void atualizarListaUsuariosUI(List<String> usuarios) {
        modelUsuarios.clear();
        modelUsuarios.addElement(usuarioLogado + " [Você]");
        for (String u : usuarios) {
            if (!u.equals(usuarioLogado)) modelUsuarios.addElement(u);
        }
    }
}