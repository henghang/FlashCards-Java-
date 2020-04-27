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
        ArrayList<String> logs = new ArrayList<>();
        LinkedHashMap<String, String> mapCard = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> mistakeStats = new LinkedHashMap<>();

        do {
            displayMenu();
            selection = scanner.nextLine();
            switch (selection) {
                case "add":
                    System.out.println("The card:");
                    term = scanner.nextLine();
                    if (!checkCardExist(term, mapCard)) {
                        System.out.println("The definition of card:");
                        definition = scanner.nextLine();
                        Card card = new Card(definition, term);
                        if (!checkDefinitionExist(definition, mapCard)) {
                            mapCard.put(card.definition, card.term);
                            mistakeStats.put(card.term, 0);
                            cardsAdded++;
                            System.out.printf("The pair (\"%s\":\"%s\") has been added.%n", card.term, card.definition);
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
                    final String cardToRemove = scanner.nextLine();
                    if (checkCardExist(cardToRemove, mapCard)){
                        mapCard.values().removeIf(value -> value.contains(cardToRemove));
                        mistakeStats.keySet().removeIf(key -> key.contains(cardToRemove));
                        System.out.println("The card has been removed.");
                    } else {
                        System.out.printf("Can't remove \"%s\": there is no such card.%n", cardToRemove);
                    }
                    break;
                case "import":
                    count = 0;
                    String[] cardDetails;
                    System.out.println("File name:");
                    fileName = scanner.nextLine();
                    file = new File(".\\" + fileName);
                    try (Scanner readFile = new Scanner(file)) {
                        while (readFile.hasNext()) {
                            cardDetails = readFile.nextLine().split("&");
                            mapCard.put(cardDetails[0], cardDetails[1]);
                            mistakeStats.put(cardDetails[1], Integer.parseInt(cardDetails[2]));
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
                            printWriter.println(entry.getKey() + "&" + entry.getValue() + "&" + mistakeStats.get(entry.getValue()));
                        }
                        System.out.println(cardsAdded + " cards have been saved.");
                    } catch (IOException e) {
                        System.out.println("An exception occurs: " + e.getMessage());
                    }
                    break;
                case "ask":
                    System.out.println("How many times to ask?");
                    int numberOfCards = Integer.parseInt(scanner.nextLine());
                    askQuestion(mapCard, mistakeStats, numberOfCards);
                    break;
                case "check":
                    System.out.println(mapCard);
                    System.out.println(mistakeStats);
                    break;
                case "exit":
                    System.out.println("Bye bye!");
                    break;
                case "hardest card":
                    ArrayList<String> hardestCards = new ArrayList<>();
                    int max = 0;
                    for (var entry: mistakeStats.entrySet()) {
                        if (entry.getValue() > max) {
                            max = entry.getValue();
                            hardestCards.clear();
                            hardestCards.add(entry.getKey());
                        } else if (entry.getValue() == max && entry.getValue() != 0){
                            hardestCards.add(entry.getKey());
                        }
                    }
                    if (hardestCards.size() == 1) {
                        System.out.printf("The hardest card is \"%s\". You have %d errors answering it.%n", hardestCards.get(0), max);
                    } else if (hardestCards.size() > 1) {
                        StringBuilder out = new StringBuilder();
                        for (String card: hardestCards) {
                            out.append("\"" + card + "\", ");
                        }
                        out.setCharAt(out.length() - 2, '.');
                        System.out.println("The hardest cards are " + out + " You have " + max + " errors answering them.");
                    } else {
                        System.out.println("There are no cards with errors.");
                    }
                    break;
                case "reset stats":
                    mistakeStats.replaceAll((k, v) -> 0);
                    System.out.println("Card statistics has been reset.");
                    break;
                case "log":
                    System.out.println("File name:");
                    fileName = scanner.nextLine();
                    file = new File(".\\" + fileName);
                    try (PrintWriter printWriter = new PrintWriter(file)) {
                        for (String log: logs) {
                            printWriter.println(log);
                        }
                        System.out.println("The log has been saved.");
                    } catch (IOException e) {
                        System.out.println("An exception occurs: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.print("No such action.");
                    break;
            }
            System.out.println();
            if (!"exit".equals(selection)) {
                logs.add(selection);
            }
        } while (!"exit".equals(selection));
    }

    public static boolean checkCardExist (String card, LinkedHashMap<String, String> mapCard) {
        return mapCard.containsValue(card);
    }

    public static boolean checkDefinitionExist (String definition, LinkedHashMap<String, String> mapCard) {
        return mapCard.containsKey(definition);
    }

    public static void displayMenu() {
        System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
    }

    public static void askQuestion(LinkedHashMap<String, String> mapCard, LinkedHashMap<String, Integer> mistakeStats, int numberOfQuestions) {
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
                System.out.printf("Wrong answer. (The correct one is \"%s\", you've just written the definition of \"%s\".)%n", actualAnswer, mapCard.get(userAns));
                mistakeStats.put(mapCard.get(actualAnswer), mistakeStats.get(mapCard.get(actualAnswer)) + 1);
            } else {
                System.out.printf("wrong answer. The correct one is \"%s\".%n", actualAnswer);
                mistakeStats.put(mapCard.get(actualAnswer), mistakeStats.get(mapCard.get(actualAnswer)) + 1);
            }
        }
    }
}

class Card {
    String term;
    String definition;

    public Card(String definition, String term) {
        this.definition = definition;
        this.term = term;
    }
}

