/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pros5121poe;

import java.util.Scanner;

public class Main {

    private static Object pros5121poe;
    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println(
                    "===== Registration and Login System =====");
            
            System.out.print("Enter first name: ");
            String firstName = scanner.nextLine();
            
            System.out.print("Enter last name: ");
            String lastName = scanner.nextLine();
            
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            
            System.out.print(
                    "Enter South African cellphone number: ");
            
            String cellPhoneNumber =
                    scanner.nextLine();
            
            Login user = new Login(
                    firstName,
                    lastName,
                    username,
                    password,
                    cellPhoneNumber
            );
            
            String registrationMessage =
                    user.registerUser();
            
            System.out.println(
                    "\n" + registrationMessage);
            
            if (!registrationMessage.equals(
                    "User successfully registered.")) {
                
                scanner.close();
                
                return;
            }
            
            // ==============================
            // LOGIN
            // ==============================
            
            System.out.println(
                    "\n===== LOGIN =====");
            
            System.out.print(
                    "Enter username: ");
            
            String enteredUsername =
                    scanner.nextLine();
            
            System.out.print(
                    "Enter password: ");
            
            String enteredPassword =
                    scanner.nextLine();
            
            boolean loginSuccessful =
                    user.loginUser(
                            enteredUsername,
                            enteredPassword);
            
            System.out.println(
                    "\n"
                            + user.returnLoginStatus(
                                    loginSuccessful));
            
            if (!loginSuccessful) {
                
                scanner.close();
                
                return;
            }
            
            // Load stored messages from JSON
            Message.loadStoredMessages();
            
            boolean running = true;
            
            while (running) {
                
                System.out.println(
                        "\n===== QUICKCHAT MENU =====");
                
                System.out.println(
                        "1. Send Messages");
                
                System.out.println(
                        "2. Coming Soon.");
                
                System.out.println(
                        "3. Quit");
                
                System.out.println(
                        "4. Stored Messages");
                
                System.out.print(
                        "Choose an option: ");
                
                String choice =
                        scanner.nextLine();
                
                switch (choice) {
                    
                    case "1":
                        
                        sendMessages(scanner);
                        
                        break;
                        
                    case "2":
                        
                        System.out.println(
                                "Coming Soon.");
                        
                        break;
                        
                    case "3":
                        
                        running = false;
                        
                        System.out.println(
                                "Thank you for using QuickChat.");
                        
                        break;
                        
                    case "4":
                        
                        storedMessagesMenu(scanner);
                        
                        break;
                        
                    default:
                        
                        System.out.println(
                                "Invalid option. "
                                        + "Please choose 1, 2, 3 or 4.");
                }
            }
        }
    }

    // =========================================================
    // SEND MESSAGES
    // =========================================================

    private static void sendMessages(
            Scanner scanner) {

        System.out.print(
                "\nHow many messages would you like to send? ");

        int numberOfMessages;

        try {

            numberOfMessages =
                    Integer.parseInt(
                            scanner.nextLine());

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid number.");

            return;
        }

        if (numberOfMessages <= 0) {

            System.out.println(
                    "Number of messages must be greater than zero.");

            return;
        }

        for (int i = 1;
             i <= numberOfMessages;
             i++) {

            System.out.println(
                    "\n===== MESSAGE "
                    + i
                    + " =====");

            System.out.print(
                    "Enter recipient cellphone number: ");

            String recipient =
                    scanner.nextLine();

            if (!recipient.matches(
                    "^\\+27\\d{9}$")
                    && !recipient.matches(
                    "^0\\d{9}$")) {

                System.out.println(
                        "Invalid cellphone number.");

                i--;

                continue;
            }

            System.out.print(
                    "Enter your message: ");

            String text =
                    scanner.nextLine();

            if (text.length() > 250) {

                System.out.println(
                        "Message exceeds 250 characters.");

                i--;

                continue;
            }

            Message message =
                    new Message(
                            i,
                            recipient,
                            text);

            System.out.println(
                    "\nMessage ID: "
                    + message.getMessageID());

            System.out.println(
                    "Message Hash: "
                    + message.getMessageHash());

            System.out.println(
                    "\nChoose what to do with the message:");

            System.out.println(
                    "1. Send");

            System.out.println(
                    "2. Disregard");

            System.out.println(
                    "3. Store");

            System.out.print(
                    "Choice: ");

            int action;

            try {

                action =
                        Integer.parseInt(
                                scanner.nextLine());

            } catch (NumberFormatException e) {

                action = 0;
            }

            String result =
                    message.SentMessage(action);

            System.out.println(result);
        }

        System.out.println(
                "\nTotal messages sent in this session: "
                + Message.returnTotalMessages());
    }

    // =========================================================
    // STORED MESSAGES MENU
    // =========================================================

    private static void storedMessagesMenu(
            Scanner scanner) {

        boolean back = false;

        while (!back) {

            System.out.println(
                    "\n===== STORED MESSAGES =====");

            System.out.println(
                    "1. Display sender and recipient "
                    + "of all stored messages");

            System.out.println(
                    "2. Display the longest stored message");

            System.out.println(
                    "3. Search by message ID");

            System.out.println(
                    "4. Search messages for a recipient");

            System.out.println(
                    "5. Delete a message using message hash");

            System.out.println(
                    "6. Display message report");

            System.out.println(
                    "7. Back to main menu");

            System.out.print(
                    "Choose an option: ");

            String choice =
                    scanner.nextLine();

            switch (choice) {

                case "1":

                    Message.displayStoredSendersAndRecipients();

                    break;

                case "2":

                    System.out.println(
                            "\nLongest stored message:");

                    System.out.println(
                            Message.getLongestStoredMessage());

                    break;

                case "3":

                    System.out.print(
                            "Enter message ID: ");

                    String id =
                            scanner.nextLine();

                    System.out.println(
                            Message.searchByMessageID(id));

                    break;

                case "4":

                    System.out.print(
                            "Enter recipient cellphone number: ");

                    String recipient =
                            scanner.nextLine();

                    System.out.println(
                            Message.searchByRecipient(
                                    recipient));

                    break;

                case "5":

                    System.out.print(
                            "Enter message hash: ");

                    String hash =
                            scanner.nextLine();

                    System.out.println(
                            Message.deleteByHash(
                                    hash));

                    break;

                case "6":

                    System.out.println(
                            Message.displayReport());

                    break;

                case "7":

                    back = true;

                    break;

                default:

                    System.out.println(
                            "Invalid option.");
            }
        }
    }
}