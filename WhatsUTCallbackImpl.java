import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class WhatsUTCallbackImpl extends UnicastRemoteObject implements WhatsUTCallback {
    private ChatScreen chatScreen;

    public WhatsUTCallbackImpl(ChatScreen chatScreen) throws RemoteException {
        super();
        this.chatScreen = chatScreen;
    }

    @Override
    public void receberMensagem(String emissor, String mensagem, boolean emGrupo) throws RemoteException {
        chatScreen.adicionarMensagemNaTela(emissor, mensagem, emGrupo);
    }

    @Override
    public void atualizarUsuarios(List<String> usuarios) throws RemoteException {
        chatScreen.atualizarListaUsuariosUI(usuarios);
    }
}