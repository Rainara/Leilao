package publisher;

import interfaces.LeiloeiroInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicPublisher;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import server.Leiloeiro;
import view.ServidorInterface;
import view.Constants;

public class JmsPublisher {
    private TopicPublisher publisher;
    private TopicSession session;
    private TopicConnection connect;

    public JmsPublisher(String factoryName, String topicName) throws JMSException, NamingException {
        Context jndiContext = new InitialContext();
        TopicConnectionFactory factory = (TopicConnectionFactory) jndiContext.lookup(factoryName);
        Topic topic = (Topic) jndiContext.lookup(topicName);
        this.connect = factory.createTopicConnection();
        this.session = connect.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        this.publisher = session.createPublisher(topic);
    }

    public void publish(String message) throws JMSException {
        TextMessage textMsg = this.session.createTextMessage();
        textMsg.setText(message);
        System.out.println("Publicando: " + message);
        this.publisher.publish(textMsg);
    }

    public void close() throws JMSException {
        this.connect.close();
    }

    public static void main(String[] args) throws Exception {
        ServidorInterface SI;
        try {
            SI = new ServidorInterface();
            Leiloeiro obj = new Leiloeiro(SI);
            UnicastRemoteObject.unexportObject(obj, true);
            LeiloeiroInterface stub = (LeiloeiroInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(Constants.RMI_PORT);
            registry.bind(Constants.RMI_REMOTE_REFERENCE, stub);
            
            System.out.println("Server ready");
            SI.startInterface(stub);
            SI.setVisible(true);
            
            while (true) {
                stub.atualizaLotes();
            }
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
