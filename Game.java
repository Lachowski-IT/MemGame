import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Game
{
    static int x;
    static int y;
    static String field;
    static int chanceLeft;
    static int marginWidth;
    static long timeStart;
    static int roundNum = 0;
    static List<String> wordList;
    static List<Integer> gameParamsList;
    static List<Integer> revealList = new ArrayList<Integer>();
    static List<String> pairedWordsList = new ArrayList<String>();

    public static List<String> readFileInList(String fileName)
    {

        List<String> lines = Collections.emptyList();

        try
        {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        }

        catch (IOException e)
        {
            // do something
            e.printStackTrace();
        }

        return lines;
    }

    public static void startGame()
    {

        Scanner scan = new Scanner(System.in);
        System.out.println("Type game difficulty \n 1: Easy \n 2: Hard");
        int difficultLevel = scan.nextInt();

        if (difficultLevel == 1) {

            gameParamsList = Arrays.asList(4, 10, 3);

        } else if (difficultLevel == 2) {

            gameParamsList = Arrays.asList(8, 15, 5);

        } else {

            System.out.println("Please select correct game difficulty");
            startGame();

        }

        fetchWords(gameParamsList.get(0));
        drawBoard();
        chanceLeft = gameParamsList.get(1);
        timeStart = System.currentTimeMillis();
        roundSystem();

    }

    private static void roundSystem() {

        int pairLeft = (pairedWordsList == null) ?
                gameParamsList.get(0) : gameParamsList.get(0) - pairedWordsList.size();

        if ( pairLeft > 0 && chanceLeft > 0 ) {

            for (int subRound = 0; subRound < 2; subRound++) {

                System.out.printf("\n\nPaired words %d, Chance left %d",
                        gameParamsList.get(0)-pairLeft, chanceLeft );

                do {

                    System.out.printf("\n\nRound %d. Select item position (%d of 2): \n",
                            ( roundNum + 1 ), subRound + 1);

                    Scanner scan = new Scanner(System.in);
                    field = scan.next();

                    if (field.length() == 2) {
                        x = ((int) (field.charAt(0))) - 64;
                        y = Character.getNumericValue(field.charAt(1));
                    }

                } while ( ( field.length() != 2 ) &&
                        ( ( x < 0 && x > (gameParamsList.get(0) / 2) ) || y > 4 ) &&
                        ( (subRound == 1) && ( revealList.get(0) != revealList.get(0) ) ) );

                int index =  ( (x * 4) + y ) - 5;
                revealList.add(index);

                if ( subRound == 1 &&
                        wordList.get( revealList.get(0) ).equals( wordList.get( revealList.get(1) ) ) ) {

                    pairedWordsList.add(wordList.get(index));

                }

            }

            revealList.clear();
            roundNum++;
            chanceLeft--;
            drawBoard();
            roundSystem();

        } else if (pairLeft > 0 && chanceLeft == 0) {

            System.out.println("Game Over");

        } else {

            System.out.printf("\n\nWell done! Game complete in %d moves, Elapsed time %d seconds",
                    roundNum,  ( ( System.currentTimeMillis() - timeStart ) / 1000 ) );

        }

    }

    public static void fetchWords(int amount) {

        List<String> words = readFileInList("C:\\moto\\Words.txt");
        Collections.shuffle(words);
        List<String> finalWords = words.subList(0, amount).stream()
                .flatMap(u -> Stream.of(u,u))
                .collect(Collectors.toList());
        Collections.shuffle(finalWords);

        marginWidth = ( finalWords.stream().max(Comparator.comparingInt(String::length)).get() ).length() + 1;
        wordList = finalWords;

    }

    public static void drawBoard() {

        int iterator = 0;

        for ( int row = 0; row < gameParamsList.get(2) ; row++ ) {

            System.out.println("");

            for ( int col = 0; col < 5; col++ ) {

                if (row == 0 && col == 0) {

                    System.out.print(" \t");

                } else if (row == 0 && col > 0) {

                    System.out.printf("%-" + marginWidth + "d\t", col);

                } else if (row > 0 && col == 0) {

                    System.out.printf("%s\t", (char) (64 + row));

                } else {

                    if ( pairedWordsList != null
                            && pairedWordsList.contains(wordList.get(iterator)) ) {

                        int marginLeft = marginWidth - wordList.get(iterator).length();
                        System.out.printf("%-" + marginLeft + "s\t", wordList.get(iterator++));

                    } else if ( !revealList.isEmpty() && revealList.contains(iterator) ) {

                        int marginLeft = marginWidth - wordList.get(iterator).length();
                        System.out.printf("%-" + marginLeft + "s\t", wordList.get(iterator++));

                    } else {

                        System.out.printf("%-" + marginWidth + "s\t", "x");

                    }

                }

            }

        }

    }

    public static void main(String[] args) {

        startGame();

    }

}