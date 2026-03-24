package group22.interfaces;

public interface Observable {
    public void addObserver(GameObserver observer);
    public void removerObserver(GameObserver observer);
    public void notifyObservers(); 
}

