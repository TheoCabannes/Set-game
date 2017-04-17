import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class Jeu extends UnicastRemoteObject implements InterfaceServeur, Runnable {
	private int[] cards; // Represente les cartes du plateau
	private Set set; // Représente la pile de cartes à jouer
	private int N = 15, nbJoueur; // N est le nombre de cartes maximum sur le plateau
	private int[] compte;
	private volatile boolean visible, end; // Besoin de 3 cartes de plus et si la partie est finie
	private static String registry;
	private LinkedList<InterfaceClient> p;
	private static int iemeJoueur = 1;
	
	public static void main(String Args[]) throws RemoteException, MalformedURLException {
		// On crée le serveur qui crée le jeu
		InterfaceServeur plateau = new Jeu((Args.length >= 2) ? Integer.parseInt(Args[1]) : 2);
		registry = (Args.length >= 1) ? Args[0] : "localhost";
		String registration = "rmi://" + registry + "/Jeu";
		Naming.rebind(registration, plateau); // on met le serveur sur le réseau
		System.out.println("En attente de joueurs...");
		Thread t = new Thread((Runnable) plateau);
		t.start(); // on lance le thread du serveur
	}

	Jeu(int nbJoueur) throws java.rmi.RemoteException {
		this.nbJoueur = nbJoueur;
		compte = new int[nbJoueur];
		p = new LinkedList<InterfaceClient>();
		end = false;
		set = new Set();
		cards = new int[N];
		for (int i = 0; i < N - 3; i++)
			cards[i] = set.getCard();
		visible = (nbSet(N - 3) == 0);
		for (int i = N - 3; i < N; i++)
			cards[i] = visible ? set.getCard() : -1;
		if (visible)
			nbSet(N);
	}

	public synchronized void testJeu(int a, int b, int c, InterfaceClient client) throws RemoteException {
		// sleep(); //Permet de vérifier l'absence de race condition
		if (client.getNbCards() != 3) // Un autre joueur a été plus rapide
			return;
		if (testSet(a, b, c)) { // On teste les trois cartes
			gagner(a, b, c);
			compte[client.getNumero()-1]++;
		} else 
			compte[client.getNumero()-1]--;
		for (InterfaceClient cli : p)
			cli.afficherScore(compte[cli.getNumero()-1]+"");
		if (end)
			endPartie();
	}

	private void sleep() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
	}

	private boolean testSet(int a, int b, int c) {
		return isSet(cards[a], cards[b], cards[c]);
	}

	static boolean isSet(int a, int b, int c) {
		if (a < 0 || b < 0 || c < 0)
			return false;
		for (int i = 0; i < 4; ++i) {
			if (((a & 0x3) + (b & 0x3) + (c & 0x3)) % 3 != 0)
				return false;
			a >>= 2;
			b >>= 2;
			c >>= 2;
		}
		return true;
	}

	private void gagner(int a, int b, int c) throws RemoteException {
		if (!set.isEmpty()) { // On regarde s'il reste des cartes
			if (!visible) // Si les cartes 13,14,15 n'étaient pas visibles
				distribuer(a, b, c);
			else // Si les cartes 13,14,15 étaient visibles
				reorganiser(a, b, c); // On replace les cartes sur le plateau
			if (nbSet(N - 3) == 0) { // On teste si on a besoin de nouvelles
				// cartes
				if (!set.isEmpty()) {
					System.out.println("Besoin de plus de cartes");
					distribuer(N - 3, N - 2, N - 1);
					visible = true;
					if (nbSet(N) == 0) {
						String impossible = "Il n'y a plus de set possible même avec " + N + " cartes \n";
						for (InterfaceClient j : p)
							j.afficherScore(impossible);
					}
				} else
					end = true;
			} else
				visible = false;
			for (InterfaceClient j : p)
				j.update(a, b, c);
		} else {
			N = N - 3;
			for (InterfaceClient j : p)
				j.setN(N);
			reorganiser(a, b, c);
			if (nbSet(N - 3) == 0)
				end = true;
		}

	}

	private void distribuer(int a, int b, int c) {
		cards[a] = set.getCard();
		cards[b] = set.getCard();
		cards[c] = set.getCard();
	}

	private void endPartie() throws RemoteException {
		String score = "<br>";
		for (int i = 0; i < nbJoueur; i++)
			score += "p" + (i + 1) + " : " + compte[i] + "<br>";
		score += "PARTIE<br>FINIE";
		for (InterfaceClient j : p)
			j.afficherScore(score);
	}

	private void reorganiser(int a, int b, int c) throws RemoteException {
		// lorsqu'il ne reste plus de cartes dans le set
		boolean c_1 = false, c_2 = false, c_3 = false;
		if (a != N - 3 && b != N - 3 && c != N - 3)
			c_1 = true;
		if (a != N - 2 && b != N - 2 && c != N - 2)
			c_2 = true;
		if (a != N - 1 && b != N - 1 && c != N - 1)
			c_3 = true;
		if (a < N - 3) {
			if (c_1) {
				cards[a] = cards[N - 3];
				c_1 = false;
			} else if (c_2) {
				cards[a] = cards[N - 2];
				c_2 = false;
			} else if (c_3) {
				cards[a] = cards[N - 1];
				c_3 = false;
			}
		}
		if (b < N - 3) {
			if (c_1) {
				cards[b] = cards[N - 3];
				c_1 = false;
			} else if (c_2) {
				cards[b] = cards[N - 2];
				c_2 = false;
			} else if (c_3) {
				cards[b] = cards[N - 1];
				c_3 = false;
			}
		}
		if (c < N - 3) {
			if (c_1) {
				cards[c] = cards[N - 3];
				c_1 = false;
			} else if (c_2) {
				cards[c] = cards[N - 2];
				c_2 = false;
			} else if (c_3) {
				cards[c] = cards[N - 1];
				c_3 = false;
			}
		}
		for (InterfaceClient j : p)
			//j.organiser(a, b, c);
			j.update(a, b, c);
	}

	int nbSet(int n) { // on recalcule à chaque fois les combinaisons possibles
						// en parallèle pas plus rapide
		int i = 0;
		for (int j = 0; j < n - 2; j++)
			for (int k = j + 1; k < n - 1; k++)
				for (int l = k + 1; l < n; l++)
					if (isSet(cards[j], cards[k], cards[l])) {
						i++;
						System.out.println((j + 1) + ", " + (k + 1) + ", " + (l + 1));
					}
		return i;
	}

	@Override
	public void run() {
		synchronized (this) {
			try {
				for (int i = 0; i < nbJoueur; i++) { // on attend que chaque
														// joueur se connecte
					this.wait();
				}
				int i = 1;
				for (InterfaceClient j : p) {
					synchronized (j) { // lorsque tous les joueurs sont
										// connectés, on les prévient
						j.setNumero(i++);
						j.setN(N);
						j.notifier();
					}
				}
				System.out.println("Le jeu commence");
			} catch (InterruptedException | RemoteException e) {
			}
		}

	}

	@Override
	public synchronized void addJoueur(InterfaceClient listener, String registration)throws RemoteException, MalformedURLException, NotBoundException {
		p.add((InterfaceClient) Naming.lookup(registration));
		System.out.println("Joueur " + iemeJoueur + " connecté");
		iemeJoueur++;
		notify();
	}

	@Override
	public int valeur(int i) throws RemoteException {
		return cards[i];
	}

	@Override
	public boolean getVisible() throws RemoteException {
		return visible;
	}
}
