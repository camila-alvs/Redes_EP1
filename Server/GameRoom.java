import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    private String name;

    private Game game;
    private List<ServerSocketHandler> players;

    public GameRoom(String name){
        this.name = name;
        this.game = new Game();
        this.players = new ArrayList<>(2);

        System.out.println("Sala " + this.name + " criada");
    }

    public void listPlayers(){
        if(players.size() == 0){
            System.out.println("Sala vazia");
        }else if (players.size() == 1){
            System.out.print(players.get(0) + ".");
        } else {
            System.out.print(players.get(0) + ", " + players.get(1) + ".");
        }
    }

    public void addPlayers(ServerSocketHandler players) {
        this.players.add(players);
    }

    public String getName() {
        return name;
    }

    public ServerSocketHandler getPlayers(int i){return players.get(i);}

    public int getPlayersSize(){return players.size();}

    public void removePlayer(ServerSocketHandler player) {
        players.remove(player);
    }
}