package script;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/*
    File name could be only unbroken sequence of letters or digits
 */


public class Script {

    private static final Map<Integer, List<String>> map = new TreeMap<>();


    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected 1 argument: file name, found: " + Arrays.toString(args));
        }
        // :NOTE: * Не рекомендованный способ управления ресурсами
        FileReader f = null;
        try {
            try {
                // :NOTE: * Ввод-вывод текста без указания кодировки
                f = new FileReader(args[0]);
                FileSource source = new FileSource(f);

                while (true) {
                    Command com;

                    try {
                        com = source.nextCommand();
                    } catch (ParseException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }

                    if (com == Command.END) {
                        break;
                    }
                    try {
                        make(com, source);
                    } catch (ParseException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
            } finally {
                if (f != null) {
                    f.close();
                }
            }
        } catch (IOException e) {
            System.out.println("incorrect file closing " + e.getMessage());
        }
    }

    public static void print(String fileName) {
        try {
            FileWriter writer = null;
            try {
                // :NOTE: * Небуферизованный вывод
                writer = new FileWriter(fileName);
                for (Integer integer : map.keySet()) {
                    for (String s : map.get(integer)) {
                        writer.write(integer + " ");
                        writer.write(s + "\n");
                    }
                }
            } catch (FileNotFoundException e) {
                // :NOTE: - Неверное сообщение об ошиюке
                System.out.println("file not found " + e.getMessage());
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } catch (IOException e) {
            System.out.println("Did not close the file: " + e.getMessage());
        }
    }

    private static void make(Command com, FileSource source) {
        switch (com) {
            case ADD:
                Pair pair = source.nextPair();
                List<String> value = map.get(pair.getFirst());
                if (value == null) {
                    List<String> arr = new ArrayList<>();
                    arr.add(pair.getSecond());
                    map.put(pair.getFirst(), arr);
                } else {
                    // :NOTE: - Дублирование arr.add(pair.getSecond());
                    value.add(pair.getSecond());
                }
                break;
            case REMOVE:
                int index = source.nextIndex();
                map.remove(index);
                break;
            case PRINT:
                String fileName = source.nextFile();
                print(fileName);
                break;
            case ERR:
                System.out.println("Unsupported command");
        }
    }
}
