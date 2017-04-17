import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//Ne reste plus qu'à afficher le score sur la fenetre graphique
@SuppressWarnings("serial")
public class Joueur extends UnicastRemoteObject implements MouseListener, InterfaceClient, Runnable {
	private SetCard[] cards;
	private volatile int nbCards, a, b, c; // nombre de cartes sélectionnées et
											// les cartes sélectionnées
	public static final int cellLength = 150, cellWidth = 100;
	private JPanel[] content;
	private JLabel affichage;
	volatile String temps="00 : 00", score="0";
	long t0;
	volatile long t1;
	private JFrame frame;
	private int N, numero, nbRows; // Le nombre de cartes sur le plateau et le
									// numero du
	// joueur
	private static InterfaceServeur jeu; // Le serveur du jeu

	public static void main(String Args[]) throws NotBoundException, IOException {
		String registry = (Args.length >= 1) ? Args[0] : "localhost";
		String registration = "rmi://" + registry + "/Jeu";
		jeu = (InterfaceServeur) Naming.lookup(registration); // On se connecte
																// au jeu
		Joueur joueur = new Joueur();
		registry = (Args.length >= 2) ? Args[1] : registry;
		registration = "rmi://" + registry + "/Joueur";
		Naming.rebind(registration, joueur); // On se met sur le réseau
		System.out.print("Vous êtes sur le réseau, vous vous connectez au serveur");
		synchronized (jeu) {
			jeu.addJoueur(joueur, registration);; // On previent le serveur de sa présence
		}
		System.out.println("Vous êtes connecté");
		Thread t = new Thread((Runnable) joueur);
		t.start(); // on lance le thread
	}

	private Joueur() throws java.rmi.RemoteException {
	}

	public void initialize() throws RemoteException {
		// On crée l'interface graphique
		nbRows = 1 + (int) (Math.sqrt(N));
		nbCards = 0;
		frame = new JFrame("Vous êtes le joueur " + numero);
		frame.setBounds(400, 100, (1 + (N / nbRows)) * cellWidth, nbRows * (cellLength + 10));
		content = new JPanel[N+1];
		cards = new SetCard[N];
		for (int i = 0; i < N - 3; i++)
			cards[i] = new SetCard(jeu.valeur(i));
		for (int i = N - 3; i < N; i++) {// Les 3 dernières cartes sont
											// invisibles
			cards[i] = new SetCard(jeu.getVisible() ? jeu.valeur(i) : -1);
			cards[i].setVisible(jeu.getVisible());
		}
		for (int i = 0; i < N; i++) {
			content[i] = new JPanel();
			content[i].setBounds((i % (1 + (N / nbRows))) * cellWidth, (i / (1 + (N / nbRows))) * cellLength, cellWidth,
					cellLength);
			content[i].add(cards[i]);
			cards[i].addMouseListener(this); // On peut cliquer sur les cartes
			frame.add(content[i]);
		}
		int i = N;
		content[i] = new JPanel();
		content[i].setBounds((1 / 5 + (i % (1 + (N / nbRows)))) * cellWidth, (1/4 + (i / (1 + (N / nbRows)))) * cellLength, cellWidth, cellLength);
		affichage = new JLabel();
		content[i].add(affichage);
		content[i].add(new JLabel());
		frame.add(content[i]);
		frame.add(new JPanel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		t0 = System.currentTimeMillis();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for (int i = 0; i < N; i++) {
			if (e.getSource() == cards[i]) {
				try {
					select(i);
				} catch (RemoteException e1) {
				}
				break;
			}
		}
	}

	private void afficherTemps() {
		t1 = System.currentTimeMillis();
		long t = t1-t0;
		temps = t/60000+" : "+((t%60000)/1000);
		setText();
	}

	private void update(int i) throws RemoteException {
		if (nbCards == 1) {
			a = i;
		} else if (nbCards == 2) {
			b = i;
		} else if (nbCards == 3) {
			c = i;
			jeu.testJeu(a, b, c, this);
			deselect();
		}
	}

	private void select(int i) throws RemoteException {
		if (cards[i].selected) {
			cards[i].setSelected(false);
			nbCards--;
			if (i == a)
				a = b;
		} else {
			cards[i].setSelected(true);
			nbCards++;
			update(i);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public synchronized void update(int a, int b, int c) throws RemoteException {
		if (this.a == a || this.a == b || this.a == c || this.b == a || this.b == b || this.b == c || this.c == a
				|| this.c == b || this.c == c)
			deselect(); // Ici on deselectionne les cartes si une des cartes
						// modifiées était sélectionnée
		set(a, b, c);
		if (jeu.getVisible()) {
			set(N - 3, N - 2, N - 1);
			cards[N - 3].setVisible(true);
			cards[N - 2].setVisible(true);
			cards[N - 1].setVisible(true);
			frame.setVisible(true); // Permet d'éviter un petit bug
		} else {
			cards[N - 3].setVisible(false);
			cards[N - 2].setVisible(false);
			cards[N - 1].setVisible(false);
		}
	}

	private void set(int a, int b, int c) throws RemoteException {
		cards[a].set(new SetCard(jeu.valeur(a)));
		cards[b].set(new SetCard(jeu.valeur(b)));
		cards[c].set(new SetCard(jeu.valeur(c)));
	}

	public void organiser(int a, int b, int c) throws RemoteException {
		deselect();
		cards[N - 3].setVisible(false);
		cards[N - 2].setVisible(false);
		cards[N - 1].setVisible(false);
		set(a, b, c);
	}

	public void deselect() throws RemoteException {
		if (nbCards == 1)
			select(a);
		else if (nbCards == 2) {
			select(b);
			select(a);
		} else if (nbCards == 3) {
			select(c);
			select(b);
			select(a);
		}
	}

	@Override
	public void run() {
		synchronized (this) {
			try {
				wait(); // on attend le serveur (qui attend les autres joueurs)
						// pour lancer l'interface graphique
				initialize();
			} catch (RemoteException | InterruptedException e) {
			}
		}
		while(true){
			afficherTemps();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public synchronized void notifier() throws RemoteException {
		this.notify();
	}

	@Override
	public void setNumero(int n) throws RemoteException {
		numero = n;
	}

	@Override
	public void setN(int n) throws RemoteException {
		N = n;
	}

	@Override
	public synchronized int getNbCards() throws RemoteException {
		// Synchronized nous permet d'éviter les races conditions
		return nbCards;
	}

	@Override
	public void afficherScore(String score) throws RemoteException {
		this.score = score;
		setText();
	}

	private void setText() {
		affichage.setText("<HTML>"+temps+"<br> Score : "+score+"</HTML>");
	}

	@Override
	public int getNumero() throws RemoteException {
		return numero;
	}
}
