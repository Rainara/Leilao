package interfaces;

import server.Lote;
import java.rmi.Remote;
import java.rmi.RemoteException;
import javax.jms.JMSException;

public interface LeiloeiroInterface extends Remote {
	public void criarLote(String nome, double preco, long duracao) throws RemoteException, JMSException; // Cria lote e o inicia
	public boolean encerraLote(int id) throws RemoteException, JMSException;
        public String existeLote(int id) throws RemoteException;
        public void atualizaLotes() throws RemoteException, JMSException;
        public boolean enviarLance(double valor, int cod_lote, String comprador) throws RemoteException, JMSException; // Envia lance
	public void notificaNovoLote(Lote lote) throws RemoteException, JMSException;
        public void notificaAtualizacao(Lote lote) throws RemoteException, JMSException;
        public void notificaEncerramento(Lote lote) throws RemoteException, JMSException;
        public int getNextId() throws RemoteException;
        
}