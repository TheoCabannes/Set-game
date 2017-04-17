import java.rmi.RemoteException;

public interface InterfaceClient extends java.rmi.Remote{
	//Trois méthodes pour commencer le jeu
	public void notifier() throws java.rmi.RemoteException;
	public void setNumero(int n) throws java.rmi.RemoteException;
	public void setN(int n) throws java.rmi.RemoteException;
	
	public int getNbCards() throws java.rmi.RemoteException;
	public int getNumero() throws RemoteException;
	
	public void update(int a, int b, int c) throws java.rmi.RemoteException;
	public void afficherScore(String score) throws RemoteException;
}
