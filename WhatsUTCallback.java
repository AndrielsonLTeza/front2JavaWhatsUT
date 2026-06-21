import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface WhatsUTCallback extends Remote {
    void receberMensagem(String emissor, String mensagem, boolean emGrupo) throws RemoteException;
    void atualizarUsuarios(List<String> usuarios) throws RemoteException;
}