import java.util.Scanner;
import java.util.Arrays;
public class PhoneBook {
    /*
    if you want to add one more book, you will need:
        1)increase the length of the 'sortByName' array (17th line)
        2)fill the book with empty strings (19th line)
        3)put the new book in verification (33rd line)
        4)put the new book in verification (72nd line)
        5)sort the new book (81st line)
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        String name;
        String phoneNumber;
        String[][] phoneBook = new String[1][2];
        String[][] phoneBookCopy = new String[2][2]; // if there are no empty slots in our book, we create a new one
        String[] sortByName = new String[phoneBook.length + phoneBookCopy.length]; // here we put all the names we have in alphabetical order ('list' method)

        // here we fill our phoneBook with empty strings (because by default empty strings are filled with 'null' value)
        for (String[] row : phoneBook) {
            Arrays.fill(row, "");
        }
        // same here
        for (String[] row : phoneBookCopy) {
            Arrays.fill(row, "");
        }
        // and here
        Arrays.fill(sortByName, "");

        // ask the user for his name and phone number
        while (true) {
            // if we`re out of space in the phone books
            if (!phoneBookCopy[phoneBookCopy.length - 1][phoneBookCopy[phoneBookCopy.length - 1].length - 1].equals("")) { // check the last slot in the book
                System.out.println("\nSorry, there are no more empty slots in our phone book :(\nMaybe it`s time to enlarge the phone book...");
                break;
            }
            System.out.println("Enter your name:");
            name = s.nextLine();
            if (name.equals("q")) { // to exit the program
                break;
            } else if (checkName(name)) { // checking if the name was entered correctly
                name = formatName(name); // format our name
                String giveNumber = ifNameInBook(phoneBook, name); // checking if the name is in the phone book
                if (giveNumber.equals("")) { // if the necessary name is not in the first book, we look for it in the second book
                    giveNumber = ifNameInBook(phoneBookCopy, name);
                }
                if (giveNumber.length() > 0) { // if yes, phone number length > 0
                    System.out.println("Such a contact exists:" + giveNumber);
                } else { // if the name is not in our phone book, we ask for a phone number
                    while (true) {
                        System.out.println("Enter a phone number:");
                        phoneNumber = s.nextLine();
                        phoneNumber = formatPhoneNumber(phoneNumber); // format our number
                        if (phoneNumber.length() > 0) { // if the phone number was entered correctly
                            if (phoneBook[phoneBook.length - 1][phoneBook[phoneBook.length - 1].length - 1].equals("")) { // if the last slot is empty, we can add contacts to this book
                                System.out.println("Your contact has been successfully added: " + add(phoneBook, name, phoneNumber));
                                break;
                            } else if (phoneBookCopy[phoneBookCopy.length - 1][phoneBookCopy[phoneBookCopy.length - 1].length - 1].equals("")){ // if the last slot isn`t empty, then it`s time to add new contacts to a new book
                                System.out.println("Your contact has been successfully added(another phonebook): " + add(phoneBookCopy, name, phoneNumber));
                                break;
                            }
                        } else { // if incorrectly
                            System.out.println("Your phone number was entered incorrectly");
                        }
                    }
                }
            } else {
                phoneNumber = formatPhoneNumber(name); // checking if the user entered the phone number
                if (!phoneNumber.equals("")) { // if it is a phone number
                    String giveName = ifPhoneNumberInBook(phoneBook, phoneNumber);
                    if (giveName.equals("")) { // if the phone number the user is looking for is not in 'phoneBook', we check 'phoneBookCopy', if not there too, we check the next array
                        giveName = ifPhoneNumberInBook(phoneBookCopy, phoneNumber);
                    }
                    System.out.println("Such a number exists: " + giveName);
                } else { // if it is not
                    System.out.println("Your name was entered incorrectly");
                }
            }
        }
        // collect our names in the 'sortByName' array in alphabetical order
        list(phoneBook, sortByName); // 1 argument - phone book where we have names; 2 argument - the array that we will print alphabetically
        list(phoneBookCopy, sortByName);

        // print the sorted 'sortByName' array
        printBook(sortByName);
    }

    // check name length (we need 3: firstname, lastname, middle name)
    public static boolean checkName(String name) {
        String[] correctName = name.split(" ");
        return correctName.length == 3;
    }

    // lead our name to the following form: Firstname Lastname Middle name
    public static String formatName(String name) {
        /*
        this method is not ideal, if the length of firstname and surname are equal, there may be a chance that surname stands before firstname
            (if user enters surname first and its length equals to the length of the firstname)
         */
        String[] correctName = name.split(" "); // divide our name into three parts
        for (int i = 0; i < correctName.length; i++) { // remove everything from each part except words
            correctName[i] = correctName[i].replaceAll("[^a-zA-Zа-яА-Я]", "");
        }
        String fullString = "";
        for (int i = 0; i < correctName.length - 1; i++) { // the longest part goes to the end (middle name)
            if (correctName[i].length() > correctName[i + 1].length()) {
                String temp = correctName[i];
                correctName[i] = correctName[i + 1];
                correctName[i + 1] = temp;
            }
        }
        if (correctName[1].length() < correctName[0].length()) { // the shortest part goes to the beginning (firstname)
            String temp = correctName[1];
            correctName[1] = correctName[0];
            correctName[0] = temp;
        }

        for (int i = 0; i < correctName.length; i++) { // make the first letter big
            correctName[i] = correctName[i].replaceFirst(correctName[i].substring(0, 1), correctName[i].substring(0, 1).toUpperCase());
        }
        return fullString.concat(correctName[0]).concat(" ").concat(correctName[1]).concat(" ").concat(correctName[2]);
    }

    // lead our phone number to the following form: +7 ХХХ ХХХ ХХ ХХ
    public static String formatPhoneNumber(String number) {
        String result = "+7 ХХХ ХХХ ХХ ХХ";
        String clean = number.replaceAll("[^0-9]", ""); // remove everything except numbers
        String[] cleanArray = clean.split(""); // divide the number
        if (cleanArray.length == 11) { // if the phone number was entered correctly
            // here we need to find each 'X' index and save it in an array
            int[] indexValues = new int[10]; // 10 - because we have ten digits after '+7'
            int j = 0;
            for (int i = 0; i < result.length(); i++) {
                if (result.charAt(i) == 'Х') { // if we find 'X', we save its index in the 'IndexValue' array
                    indexValues[j] = i;
                    j++;
                }
            }
            // here we replace each 'X' in the 'result' variable with numbers from the 'cleanArray' array
            int s = 1;
            for (int i = 0; i < indexValues.length; i++) {
                result = result.replaceFirst(Character.toString(result.charAt(indexValues[i])), String.valueOf(cleanArray[s]));
                s++;
            }
        } else { // if the phone number was entered incorrectly
            result = "";
        }
        return result;
    }

    public static String ifNameInBook(String[][] book, String name) {
        String phoneNumber = "";
        for (int i = 0; i < book.length; i++) { // check all the phone book
            for (int j = 0; j < book[i].length; j++) {
                if (book[i][j].equals(name)) { // if such a name exists, we take its phone number
                    phoneNumber = book[i][j + 1];
                    i = book.length; // if we find the necessary name, we exit the loop
                    break; // exit inner loop
                }
            }
        }
        return phoneNumber;
    }

    public static String ifPhoneNumberInBook(String[][] book, String phoneNumber) {
        String name = "";
        for (int i = 0; i < book.length; i++) { // check all the phone book
            for (int j = 0; j < book[i].length; j++) {
                if (book[i][j].equals(phoneNumber)) { // if such a number exists, we take its phone number
                    name = book[i][j - 1];
                    i = book.length; // if we find the necessary number, we exit the loop
                    break; // exit inner loop
                }
            }
        }
        return name;
    }

    // add a contact
    public static String add(String[][] book, String name, String number) {
        for (int i = 0; i < book.length; i++) {
            for (int j = 0; j < book[i].length; j++) {
                if (book[i][j].equals("")) { // if we find an empty slot, we add a new contact there
                    book[i][j] = name;
                    book[i][j + 1] = number;
                    i = book.length; // after adding, exit the loop
                    break; // exit inner loop
                }
            }
        }
        return name + ": " + number;
    }

    // collect our names in the 'sortByName' array in alphabetical order
    public static void list(String[][] book, String[] sortByName) {
        int s = 0;
        for (int i = 0; i < book.length; i++) {
            for (int j = 0; j < book[i].length; j++) {
                if (!book[i][j].equals("")) { // if we find a contact (not an empty slot), we add it to the 'sortByName' array
                    if (sortByName[s].equals("")) {
                        sortByName[s] = book[i][j] + ": " + book[i][j + 1];
                        s++;
                    }
                    break; // exit inner loop
                }
            }
        }
        Arrays.sort(sortByName); // sort in alphabetical order
    }

    public static void printBook(String[] sortedByName) {
        System.out.println("\n\nThe phone book:"); // indent
        for (int i = 0; i < sortedByName.length; i++) {
            if (!sortedByName[i].equals("")) {
                System.out.println(i);
            }
        }
    }
}