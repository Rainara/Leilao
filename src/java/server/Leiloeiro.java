package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import interfaces.LeiloeiroInterface;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.*;
import javax.naming.NamingException;
import publisher.JmsPublisher;
import view.ServidorInterface;
import view.Constants;

public class Leiloeiro extends UnicastRemoteObject implements LeiloeiroInterface {

    private static final long serialVersionUID = 1L;
    private List<Lote> lotes;
    private int cod_count;
    private ServidorInterface SI;

    public Leiloeiro(ServidorInterface SI) throws RemoteException {
        this.lotes = new ArrayList<Lote>();
        this.cod_count = 1;
        this.SI = SI;
    }

    @Override
    public void atualizaLotes() throws RemoteException, JMSException {
        for (Lote lote : lotes) {
            if (!lote.isEncerrado()) {
                if ((System.currentTimeMillis() - lote.getInicio()) >= lote.getDuracao()) {
                    // Encerra Lote
                    lote.encerra();

                    // Atualiza View Cliente
                    notificaEncerramento(lote);

                    // Atualiza View Servidor
                    String row = lote.getId() + "#"
                            + lote.getNome() + "#"
                            + lote.getComprador() + "#"
                            + lote.getPreco() + "#false";
                    SI.updateTable(row); // SI: ID#Produto#Comprador#preco#ativo;
                }
            }
        }
    }

    @Override
    public boolean enviarLance(double valor, int cod_lote, String comprador) throws RemoteException, JMSException {
        for (int i = 0; i < lotes.size(); i++) {
            if (lotes.get(i).getId() == cod_lote) {
                if (lotes.get(i).isEncerrado() || valor <= lotes.get(i).getPreco()) {
                    return false;
                }
                lotes.get(i).setComprador(comprador);
                lotes.get(i).lance(valor);

                // Atualiza View Cliente através de JMS
                notificaAtualizacao(lotes.get(i));

                // Atualiza View Servidor
                String row = lotes.get(i).getId() + "#"
                        + lotes.get(i).getNome() + "#"
                        + lotes.get(i).getComprador() + "#"
                        + lotes.get(i).getPreco();
                if (lotes.get(i).isEncerrado()) {
                    row += "#false";
                } else {
                    row += "#true";
                }
                SI.updateTable(row); // SI: ID#Produto#Comprador#preco#ativo;
                return true;
            }
        }
        return false;
    }

    @Override
    public void criarLote(String nome, double preco, long duracao) throws RemoteException, JMSException {
        Lote lote = new Lote(nome, preco, duracao, this.cod_count);
        lote.setInicio();
        lotes.add(lote);
        notificaNovoLote(lote);
        this.cod_count++;
    }

    @Override
    public void notificaNovoLote(Lote lote) throws RemoteException, JMSException {
        try {
            JmsPublisher publisher;
            publisher = new JmsPublisher(Constants.JMS_FACTORY, Constants.JMS_TOPIC);
            String text = lote.getId() + "#" + lote.getNome() + "#" + lote.getPreco();
            publisher.publish(text);
            publisher.close();

        } catch (NamingException ex) {
            Logger.getLogger(Leiloeiro.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(Leiloeiro.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void notificaAtualizacao(Lote lote) throws RemoteException, JMSException {
        try {
            JmsPublisher publisher;
            publisher = new JmsPublisher(Constants.JMS_FACTORY, Constants.JMS_TOPIC);
            String text = lote.getId() + "#" + lote.getNome() + "#" + lote.getPreco();
            publisher.publish(text);
            publisher.close();

        } catch (NamingException ex) {
            Logger.getLogger(Leiloeiro.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(Leiloeiro.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void notificaEncerramento(Lote lote) throws RemoteException, JMSException {
        try {
            JmsPublisher publisher;
            publisher = new JmsPublisher(Constants.JMS_FACTORY, Constants.JMS_TOPIC);
            String text = lote.getId() + "#" + lote.getNome()
                    + "#" + lote.getComprador() + "#" + lote.getPreco();
            publisher.publish(text);
            publisher.close();

        } catch (NamingException ex) {
            Logger.getLogger(Leiloeiro.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(Leiloeiro.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getNextId() throws RemoteException {
        return cod_count;
    }

    @Override
    public String existeLote(int id) throws RemoteException {
        for (int i = 0; i < lotes.size(); i++) {
            if (lotes.get(i).getId() == id) {
                String row = lotes.get(i).getId() + "#" + lotes.get(i).getPreco();
                if (lotes.get(i).isEncerrado()) {
                    row += "#true";
                } else {
                    row += "#false";
                }
                return row;
            }
        }
        return null;
    }

    @Override
    public boolean encerraLote(int id) throws RemoteException, JMSException {
        for (Lote lote : lotes) {
            if (lote.getId() == id && !lote.isEncerrado()) {
                lote.encerra();

                // Atualiza View Cliente
                notificaEncerramento(lote);
                
                // Atualiza View Servidor
                String row = lote.getId() + "#"
                        + lote.getNome() + "#"
                        + lote.getComprador() + "#"
                        + lote.getPreco() + "#false";
                SI.updateTable(row); // SI: ID#Produto#Comprador#preco#ativo;
                return true;
            }
        }
        return false;
    }
}
