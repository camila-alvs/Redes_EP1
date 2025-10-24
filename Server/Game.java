import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Game {

    public List<List<Boolean>> layers = new ArrayList<>();
    public static final int LAYER_COUNT = 4;

    // Inicializa cada uma das camadas do NIM. Se o Layer Count for maior, também
    // aumenta a quantidade de linhas
    private void _setupGame() {
        int sticks = 1;
        for (int i = 0; i < LAYER_COUNT; i++) {
            layers.add(new ArrayList<>());
            for (int j = 0; j < sticks; j++) {
                layers.get(i).add(true);
            }
            sticks = sticks + 2;
        }

    }

    // É uma função que imprime os gravetos de cada fileira e retorna se a seleção
    // feita pelo usuário é contínua
    public Boolean printSticks(List<Boolean> list, Boolean breakLine, Boolean showNumber, int highLightStart,
            int highlightEnd) {
        Boolean isContinuous = true;
        for (int stick = 0; stick < list.size(); stick++) {
            if (list.get(stick)) {
                if (showNumber)
                    System.out.printf(stick + 1 + " ");
                else
                    System.out.printf(
                            (stick >= highLightStart - 1 && stick <= highlightEnd - 1 ? "\u001B[32m" : "") + "| ");
                System.out.printf("\u001B[0m");
            } else {
                System.out.printf("  ");
                if (stick >= highLightStart - 1 && stick <= highlightEnd - 1) {
                    isContinuous = false;
                }
            }
        }
        if (breakLine) {
            System.out.printf("\n");
        }
        return isContinuous;
    }

    public List<Boolean> removeSticks(List<Boolean> list, int initialItem, int lastItem) {
        for (int stick = initialItem - 1; stick < lastItem; stick++) {
            list.set(stick, false);
        }
        return list;
    }

    // Função que mostra o turno do jogador
    private Boolean _playerTurn(int player, List<List<Boolean>> layers, Scanner scanner) {

        System.out.println("\u001B[33mPlayer " + (player + 1) + "'s turn\u001B[0m");

        Boolean finishedTurn = false;

        // Enquanto o turno do jogador não acabar, ele repete as instruções (para caso o
        // jogador faça alguma ação ilegal)
        while (!finishedTurn) {

            // Mostra todos os gravetos
            int layerNumber = 1;
            for (List<Boolean> layer : layers) {
                System.out.printf(layerNumber + ". ");
                printSticks(layer, layerNumber == LAYER_COUNT ? true : false, false, -1, -1);
                layerNumber++;
                System.out.printf("\n");
            }

            // Seleciona uma das fileiras representada por um número
            System.out.println("Escreva o número do graveto que você deseja remover:");
            int layerInt = scanner.nextInt();
            if (layerInt < 1 || layerInt > 4) {
                System.out.println("Valor fora do limite. Tente novamente.");
                continue;
            } else {
                Boolean isLineUsable = false;
                for (Boolean stick : layers.get(layerInt - 1)) {
                    if (stick) {
                        isLineUsable = true;
                        break;
                    }
                }
                if (!isLineUsable) {
                    System.out.println("Essa linha já está vazia, tente novamente.");
                    continue;
                }
            }

            System.out.println(' ');
            printSticks(layers.get(layerInt - 1), true, false, -1, -1);
            printSticks(layers.get(layerInt - 1), true, true, -1, -1);

            System.out.printf("\n");

            // O jogador então seleciona o primeiro graveto que quer remover
            System.out.println("Escreva qual o primeiro graveto que você quer remover.");
            int firstStick = scanner.nextInt();

            if (firstStick < 1 || firstStick > layers.get(layerInt - 1).size()) {
                System.out.println("Valor fora do limite. Tente novamente.");
                continue;
            }

            System.out.println(' ');
            printSticks(layers.get(layerInt - 1), true, false, firstStick, firstStick);
            printSticks(layers.get(layerInt - 1), true, true, -1, -1);

            System.out.printf("\n");

            // E depois seleciona o último. Esse deve ser um número maior que o anterior
            // para que seja permitido.
            System.out.println("Escreva qual o último graveto que você quer remover.");
            int lastStick = scanner.nextInt();

            Boolean isContinuous = printSticks(layers.get(layerInt - 1), true, false, firstStick, lastStick);

            if (lastStick < 1 || lastStick > layers.get(layerInt - 1).size()) {
                System.out.println("Valor fora do limite. Tente novamente.");
                continue;
            } else if (lastStick < firstStick) {
                System.out.println("O último graveto deve ter um valor maior que o primeiro. Tente novamente.");
                continue;
            } else if (!isContinuous) {
                System.out.println("Linha não contínua. Tente novamente");
                continue;
            }

            // Após isso faz a remoção dos gravetos
            System.out.println("Removendo gravetos...");
            layers.set(layerInt - 1, removeSticks(layers.get(layerInt - 1), firstStick, lastStick));

            System.out.printf("\n");

            finishedTurn = true;
        }

        for (List<Boolean> layer : layers) {
            for (Boolean stick : layer) {
                if (stick) {
                    return false;
                }
            }
        }

        return true;

    }

    public static void main(String[] args) {
        Game game = new Game();
        game._setupGame();
        Boolean hasWon = false;
        int player = 0;

        Scanner scanner = new Scanner(System.in);
        // Enquanto um jogador não vencer, o código continua repetindo os turnos e
        // alternando entre jogadores.
        while (!hasWon) {
            hasWon = game._playerTurn(player, game.layers, scanner);
            player = Math.abs(player - 1);
        }
        System.out.println("\u001B[32mO Jogador " + (player + 1) + " venceu. Parabéns!\u001B[0m");
        scanner.close();
    }
}