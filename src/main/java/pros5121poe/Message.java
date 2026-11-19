/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pros5121poe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

    private static final String STORE_FILE = "messages.json";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final List<Message> sentList = new ArrayList<>();
    private static final List<Message> disregardedList = new ArrayList<>();
    private static final List<Message> storedList = new ArrayList<>();

    // required arrays
    private static String[] sentMessages = new String[0];
    private static String[] disregardedMessages = new String[0];
    private static String[] storedMessages = new String[0];
    private static String[] messageHashes = new String[0];
    private static String[] messageIDs = new String[0];

    private final String messageID;
    private final int messageNumber;
    private final String recipientCell;
    private final String messageText;
    private final String messageHash;
    private String flag;

    // Constructor used when creating a new message
    public Message(int messageNumber, String recipientCell, String messageText) {

        this.messageID = generateMessageID();
        this.messageNumber = messageNumber;
        this.recipientCell = recipientCell;
        this.messageText = messageText == null ? "" : messageText;
        this.messageHash = createMessageHash();
        this.flag = "Pending";
    }

    // Constructor used when loading stored messages from JSON
    public Message(
            String messageID,
            int messageNumber,
            String recipientCell,
            String messageText,
            String messageHash,
            String flag) {

        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipientCell = recipientCell;
        this.messageText = messageText == null ? "" : messageText;
        this.messageHash = messageHash;
        this.flag = flag;
    }

    // Generates a 10-digit message ID
    private String generateMessageID() {

        long number = 1_000_000_000L
                + RANDOM.nextLong(9_000_000_000L);

        return String.valueOf(number);
    }

    // Checks that the message ID is not longer than 10 characters
    public boolean checkMessageID() {

        return messageID.length() <= 10;
    }

    // Checks the recipient cellphone number
    public boolean checkRecipientCell() {

        return recipientCell != null
                && (recipientCell.matches("^\\+27\\d{9}$")
                || recipientCell.matches("^0\\d{9}$"));
    }

    // Checks that the message is no longer than 250 characters
    public boolean checkMessageLength() {

        return messageText.length() <= 250;
    }

    // Creates the message hash
    public String createMessageHash() {

        String cleaned = messageText.trim();

        if (cleaned.isEmpty()) {

            return messageID.substring(0, 2)
                    + ":"
                    + messageNumber
                    + ":";
        }

        String[] words = cleaned.split("\\s+");

        String firstWord =
                words[0].replaceAll("[^A-Za-z0-9]", "");

        String lastWord =
                words[words.length - 1]
                        .replaceAll("[^A-Za-z0-9]", "");

        return messageID.substring(0, 2)
                + ":"
                + messageNumber
                + ":"
                + (firstWord + lastWord).toUpperCase();
    }

    // 1 = Send
    // 2 = Disregard
    // 3 = Store
    public String SentMessage(int choice) {

        if (choice == 1) {

            flag = "Sent";

            sentList.add(this);

            refreshArrays();

            return "Message successfully sent.";

        } else if (choice == 2) {

            flag = "Disregard";

            disregardedList.add(this);

            refreshArrays();

            return "Message disregarded.";

        } else if (choice == 3) {

            flag = "Stored";

            storedList.add(this);

            saveStoredMessages();

            refreshArrays();

            return "Message stored.";
        }

        return "Invalid choice.";
    }

    // Compatibility method
    public String SentMessage() {

        return SentMessage(1);
    }

    // Displays sent messages
    public static void printMessages() {

        if (sentList.isEmpty()) {

            System.out.println(
                    "No sent messages in this session.");

            return;
        }

        System.out.println(
                "\n===== SENT MESSAGES =====");

        for (Message message : sentList) {

            System.out.println(message);
        }
    }

    // Returns number of sent messages
    public static int returnTotalMessages() {

        return sentList.size();
    }

    // Stores a message
    public void storeMessage() {

        flag = "Stored";

        if (!containsByID(storedList, messageID)) {

            storedList.add(this);
        }

        saveStoredMessages();

        refreshArrays();
    }

    // =========================================================
    // PART 3 ARRAYS
    // =========================================================

    public static void populateArrays() {

        refreshArrays();
    }

    private static void refreshArrays() {

        sentMessages =
                toMessageTextArray(sentList);

        disregardedMessages =
                toMessageTextArray(disregardedList);

        storedMessages =
                toMessageTextArray(storedList);

        List<Message> allMessages =
                new ArrayList<>();

        allMessages.addAll(sentList);
        allMessages.addAll(disregardedList);
        allMessages.addAll(storedList);

        messageHashes =
                new String[allMessages.size()];

        messageIDs =
                new String[allMessages.size()];

        for (int i = 0;
             i < allMessages.size();
             i++) {

            messageHashes[i] =
                    allMessages.get(i).getMessageHash();

            messageIDs[i] =
                    allMessages.get(i).getMessageID();
        }
    }

    private static String[] toMessageTextArray(
            List<Message> messages) {

        String[] result =
                new String[messages.size()];

        for (int i = 0;
             i < messages.size();
             i++) {

            result[i] =
                    messages.get(i).getMessageText();
        }

        return result;
    }

    public static String[] getSentMessages() {

        return sentMessages.clone();
    }

    public static String[] getDisregardedMessages() {

        return disregardedMessages.clone();
    }

    public static String[] getStoredMessages() {

        return storedMessages.clone();
    }

    public static String[] getMessageHashes() {

        return messageHashes.clone();
    }

    public static String[] getMessageIDs() {

        return messageIDs.clone();
    }

    // =========================================================
    // JSON STORAGE
    // =========================================================

    public static void loadStoredMessages() {

        storedList.clear();

        Path path =
                Paths.get(STORE_FILE);

        if (!Files.exists(path)) {

            refreshArrays();

            return;
        }

        try {

            String json =
                    Files.readString(
                            path,
                            StandardCharsets.UTF_8);

            Pattern objectPattern =
                    Pattern.compile(
                            "\\{(.*?)\\}",
                            Pattern.DOTALL);

            Matcher objectMatcher =
                    objectPattern.matcher(json);

            while (objectMatcher.find()) {

                String object =
                        objectMatcher.group(1);

                String id =
                        readJsonString(
                                object,
                                "messageID");

                String numberText =
                        readJsonString(
                                object,
                                "messageNumber");

                String recipient =
                        readJsonString(
                                object,
                                "recipientCell");

                String text =
                        readJsonString(
                                object,
                                "messageText");

                String hash =
                        readJsonString(
                                object,
                                "messageHash");

                String flag =
                        readJsonString(
                                object,
                                "flag");

                if (id == null
                        || recipient == null
                        || text == null
                        || hash == null
                        || flag == null) {

                    continue;
                }

                int number = 0;

                try {

                    number =
                            Integer.parseInt(
                                    numberText);

                } catch (Exception ignored) {
                }

                storedList.add(
                        new Message(
                                id,
                                number,
                                recipient,
                                text,
                                hash,
                                flag));
            }

        } catch (IOException e) {

            System.out.println(
                    "Could not read messages.json: "
                    + e.getMessage());
        }

        refreshArrays();
    }

    private static String readJsonString(
            String object,
            String key) {

        Pattern pattern =
                Pattern.compile(
                        "\""
                        + Pattern.quote(key)
                        + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");

        Matcher matcher =
                pattern.matcher(object);

        if (!matcher.find()) {

            return null;
        }

        return unescapeJson(
                matcher.group(1));
    }

    private static void saveStoredMessages() {

        StringBuilder json =
                new StringBuilder();

        json.append("[\n");

        for (int i = 0;
             i < storedList.size();
             i++) {

            Message message =
                    storedList.get(i);

            json.append("  {\n");

            json.append(
                    "    \"messageID\": \"")
                    .append(
                            escapeJson(
                                    message.messageID))
                    .append("\",\n");

            json.append(
                    "    \"messageNumber\": \"")
                    .append(
                            message.messageNumber)
                    .append("\",\n");

            json.append(
                    "    \"recipientCell\": \"")
                    .append(
                            escapeJson(
                                    message.recipientCell))
                    .append("\",\n");

            json.append(
                    "    \"messageText\": \"")
                    .append(
                            escapeJson(
                                    message.messageText))
                    .append("\",\n");

            json.append(
                    "    \"messageHash\": \"")
                    .append(
                            escapeJson(
                                    message.messageHash))
                    .append("\",\n");

            json.append(
                    "    \"flag\": \"")
                    .append(
                            escapeJson(
                                    message.flag))
                    .append("\"\n");

            json.append("  }");

            if (i < storedList.size() - 1) {

                json.append(",");
            }

            json.append("\n");
        }

        json.append("]\n");

        try {

            Files.writeString(
                    Paths.get(STORE_FILE),
                    json.toString(),
                    StandardCharsets.UTF_8);

        } catch (IOException e) {

            System.out.println(
                    "Could not save messages.json: "
                    + e.getMessage());
        }
    }

    private static String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(
            String value) {

        return value
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }

    // =========================================================
    // PART 3 STORED MESSAGE FUNCTIONS
    // =========================================================

    // Displays sender and recipient
    public static void displayStoredSendersAndRecipients() {

        loadStoredMessages();

        if (storedList.isEmpty()) {

            System.out.println(
                    "No stored messages found.");

            return;
        }

        System.out.println(
                "\n===== STORED SENDERS AND RECIPIENTS =====");

        for (Message message : storedList) {

            System.out.println(
                    "Sender: QuickChat User");

            System.out.println(
                    "Recipient: "
                    + message.recipientCell);

            System.out.println();
        }
    }

    // Displays longest stored message
    public static String getLongestStoredMessage() {

        loadStoredMessages();

        if (storedList.isEmpty()) {

            return "No stored messages found.";
        }

        Message longest =
                storedList.get(0);

        for (Message message : storedList) {

            if (message.messageText.length()
                    > longest.messageText.length()) {

                longest = message;
            }
        }

        return longest.messageText;
    }

    // Search by message ID
    public static String searchByMessageID(
            String id) {

        loadStoredMessages();

        for (Message message : storedList) {

            if (message.messageID.equals(id)) {

                return "Recipient: "
                        + message.recipientCell
                        + "\nMessage: "
                        + message.messageText;
            }
        }

        return "Message ID not found.";
    }

    // Search messages by recipient
    public static String searchByRecipient(
            String recipient) {

        loadStoredMessages();

        StringBuilder result =
                new StringBuilder();

        for (Message message : storedList) {

            if (message.recipientCell.equals(
                    recipient)) {

                if (result.length() > 0) {

                    result.append("\n");
                }

                result.append(
                        message.messageText);
            }
        }

        if (result.length() == 0) {

            return "No messages found for recipient.";
        }

        return result.toString();
    }

    // Delete message using hash
    public static String deleteByHash(
            String hash) {

        loadStoredMessages();

        for (int i = 0;
             i < storedList.size();
             i++) {

            Message message =
                    storedList.get(i);

            if (message.messageHash.equals(hash)) {

                String deletedText =
                        message.messageText;

                storedList.remove(i);

                saveStoredMessages();

                refreshArrays();

                return "Message: "
                        + deletedText
                        + " successfully deleted.";
            }
        }

        return "Message hash not found.";
    }

    // Display full report
    public static String displayReport() {

        loadStoredMessages();

        if (storedList.isEmpty()) {

            return "No stored messages found.";
        }

        StringBuilder report =
                new StringBuilder();

        report.append(
                "\n===== STORED MESSAGE REPORT =====\n");

        for (Message message : storedList) {

            report.append(
                    "Message ID: ")
                    .append(
                            message.messageID)
                    .append("\n");

            report.append(
                    "Message Hash: ")
                    .append(
                            message.messageHash)
                    .append("\n");

            report.append(
                    "Recipient: ")
                    .append(
                            message.recipientCell)
                    .append("\n");

            report.append(
                    "Message: ")
                    .append(
                            message.messageText)
                    .append("\n");

            report.append(
                    "Flag: ")
                    .append(
                            message.flag)
                    .append("\n");

            report.append(
                    "------------------------------\n");
        }

        return report.toString();
    }

    // =========================================================
    // TEST SUPPORT
    // =========================================================

    public static void clearInMemoryData() {

        sentList.clear();

        disregardedList.clear();

        storedList.clear();

        refreshArrays();
    }

    public static void clearStoredFileForTesting() {

        try {

            Files.deleteIfExists(
                    Paths.get(STORE_FILE));

        } catch (IOException ignored) {
        }

        storedList.clear();

        refreshArrays();
    }

    private static boolean containsByID(
            List<Message> list,
            String id) {

        for (Message message : list) {

            if (message.messageID.equals(id)) {

                return true;
            }
        }

        return false;
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public String getMessageID() {

        return messageID;
    }

    public int getMessageNumber() {

        return messageNumber;
    }

    public String getRecipientCell() {

        return recipientCell;
    }

    public String getMessageText() {

        return messageText;
    }

    public String getMessageHash() {

        return messageHash;
    }

    public String getFlag() {

        return flag;
    }

    @Override
    public String toString() {

        return "Message ID: "
                + messageID
                + "\nMessage Hash: "
                + messageHash
                + "\nRecipient: "
                + recipientCell
                + "\nMessage: "
                + messageText
                + "\nFlag: "
                + flag;
    }
}