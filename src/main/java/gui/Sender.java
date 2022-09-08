package gui;

import java.awt.EventQueue;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Properties;

import org.apache.log4j.BasicConfigurator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Sender extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JTextField textSend;
	private JButton btnSend;
	private MessageProducer producer;
	private Session session;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sender frame = new Sender();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws NamingException 
	 * @throws JMSException 
	 */
	public Sender() throws NamingException, JMSException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Người gửi");
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Nhập tin nhắn vào khung bên dưới");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(110, 11, 300, 22);
		contentPane.add(lblNewLabel);

		textSend = new JTextField();
		textSend.setBounds(29, 58, 366, 26);
		contentPane.add(textSend);
		textSend.setColumns(10);

		// config environment for JMS
		BasicConfigurator.configure();
		// config environment for JNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		// create context
		Context ctx = new InitialContext(settings);
		// lookup JMS connection factory
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		// lookup destination. (If not exist-->ActiveMQ create once)
		Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
		// get connection using credential
		Connection con = factory.createConnection("admin", "admin");
		// connect to MOM
		con.start();
		// create session
		session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
		// create producer
		producer = session.createProducer(destination);
		// create text message
		Message msg = session.createTextMessage("hello mesage from ActiveMQ");
		producer.send(msg);
		
			
		btnSend = new JButton("Gứi");
		btnSend.setBounds(165, 158, 89, 23);
		contentPane.add(btnSend);
		btnSend.addActionListener(this);
		textSend.addActionListener(this);
		btnSend.addKeyListener(null);
	}
	

	public void actionPerformed(ActionEvent e) {
		try {
			String txt = textSend.getText().trim();
			Message msg = session.createTextMessage(txt);
			producer.send(msg);
			textSend.setText("");
			System.err.print("OKOK");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	
}
