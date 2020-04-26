package FlashCards;
import java.io.*;
import java.util.*;

public class FlashCards {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        String term;
        String definition;
        String selection;
        String fileName;
        File file;
        int count;
        int cardsAdded = 0;
        LinkedHashMap<String, String> mapCard = new LinkedHashMap<>();

        do {
            displayMenu();
            selection = scanner.nextLine();
            switch (selection) {
                case "add":
                    System.out.println("The card:");
                    term = scanner.nextLine();
                    if (!checkTermExist(term, mapCard)) {
                        System.out.println("The definition of card:");
                        definition = scanner.nextLine();
                        if (!checkDefinitionExist(definition, mapCard)) {
                            if (definition.contains("update") || definition.contains("Update")) {
                                Card card = new Card(term, definition);
                                System.out.printf("The pair (\"%s\":\"%s\") has been added.%n", card.term, card.definition);
                            } else {
                                Card card = new Card(term, definition);
                                mapCard.put(card.definition, card.term);
                                cardsAdded++;
                                System.out.printf("The pair (\"%s\":\"%s\") has been added.%n", card.term, card.definition);
                            }
                        }
                        else {
                            System.out.printf("The definition \"%s\" already exists.%n", definition);
                        }
                    } else {
                        System.out.printf("The card \"%s\" already exists.%n", term);
                    }
                    break;
                case "remove":
                    System.out.println("The card:");
                    final String checkTerm = scanner.nextLine();
                    if (checkTermExist(checkTerm, mapCard)){
                        mapCard.values().removeIf(value -> value.contains(checkTerm));
                        System.out.println("The card has been removed.");
                    } else {
                        System.out.printf("Can't remove \"%s\": there is no such card.%n", checkTerm);
                    }
                    break;
                case "import":
                    count = 0;
                    String[] cardTermDefinition;
                    System.out.println("File name:");
                    fileName = scanner.nextLine();
                    file = new File(".\\" + fileName);
                    try (Scanner readFile = new Scanner(file)) {
                        while (readFile.hasNext()) {
                            cardTermDefinition = readFile.nextLine().split("&");
                            mapCard.put(cardTermDefinition[1], cardTermDefinition[0]);
                            count++;
                        }
                        System.out.println(count + " cards have been loaded.");
                    } catch (FileNotFoundException e) {
                        System.out.print("File not found.");
                    }
                    break;
                case "export":
                    System.out.println("File name:");
                    fileName = scanner.nextLine();
                    file = new File(".\\" + fileName);
                    try (PrintWriter printWriter = new PrintWriter(file)) {
                        for (var entry: mapCard.entrySet()) {
                            printWriter.println(entry.getValue() + "&" + entry.getKey());
                        }
                        System.out.println(cardsAdded + " cards have been saved.");
                    } catch (IOException e) {
                        System.out.println("An exception occurs: " + e.getMessage());
                    }
                    break;
                case "ask":
                    System.out.println("How many times to ask?");
                    int numberOfCards = Integer.parseInt(scanner.nextLine());
                    askQuestion(mapCard, numberOfCards);
                    break;
                case "check":
                    System.out.println(mapCard);
                    break;
                case "exit":
                    System.out.println("Bye bye!");
                    break;
                default:
                    System.out.print("No such action.");
                    break;
            }
            System.out.println();
        } while (!"exit".equals(selection));
    }

    public static boolean checkTermExist (String term, LinkedHashMap<String, String> mapCard) {
        return mapCard.containsValue(term);
    }

    public static boolean checkDefinitionExist (String definition, LinkedHashMap<String, String> mapCard) {
        return mapCard.containsKey(definition);
    }

    public static void displayMenu() {
        System.out.println("Input the action (add, remove, import, export, ask, exit):");
    }

    public static void askQuestion(LinkedHashMap<String, String> mapCard, int numberOfQuestions) {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);
        String userAns;
        String actualAnswer;
        int cardNumber = 0;
        int questionNumber;
        HashMap<Integer, String> randomizeCards = new HashMap<>();

        for (String answer: mapCard.keySet()) {
            randomizeCards.put(cardNumber, answer);
            cardNumber++;
        }
        for (int i = 0; i < numberOfQuestions; i++) {
            questionNumber = random.nextInt(cardNumber);
            actualAnswer = randomizeCards.get(questionNumber);
            System.out.printf("Print the definition of \"%s\":%n", mapCard.get(actualAnswer));
            userAns = scanner.nextLine();
            if ((actualAnswer).equals(userAns)) {
                System.out.println("Correct answer.");
            } else if (mapCard.containsKey(userAns)) {
                System.out.printf("Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\".%n", actualAnswer, mapCard.get(userAns));
            } else {
                System.out.printf("wrong answer. The correct one is \"%s\".%n", actualAnswer);
            }
        }
    }
}

class Card {
    String term;
    String definition;

    public Card(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }
}

