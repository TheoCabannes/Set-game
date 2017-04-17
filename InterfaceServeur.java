import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface InterfaceServeur extends java.rmi.Remote{
	public void testJeu(int a, int b, int c, InterfaceClient client) throws java.rmi.RemoteException;
	public int valeur(int i) throws java.rmi.RemoteException;
	public boolean getVisible() throws RemoteException;
	public void addJoueur(InterfaceClient listener, String registration) throws java.rmi.RemoteException, MalformedURLException, NotBoundException;
}
