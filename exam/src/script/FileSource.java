package script;

import java.io.*;

public class FileSource {

    private final BufferedReader reader;


    private String curLine;
    private Command curCom;
    private int curPos = 0;
    private int lineNum = 1;

    private final int MAX_FILENAME = 260;

    public FileSource(FileReader f) {
        reader = new BufferedReader(f);
    }


    private boolean test(String str) {
        if (curLine.startsWith(str)) {
            curPos = str.length();
            return true;
        }
        return false;
    }


    public Command nextCommand() throws IOException {
        lineNum++;
        curLine = reader.readLine();
        if (curLine == null) {
            return curCom = Command.END;
        }
        // :NOTE: - Пробелы вместо произвольных пробельных символов
        if (test("add ")) {
            return curCom = Command.ADD;
        } else if (test("remove ")) {
            return curCom = Command.REMOVE;
        } else if (test("print ")) {
            return curCom = Command.PRINT;
        } else {
            curCom = Command.ERR;
            throw err("Expected <command>, found " + curLine);
        }
    }

    private void skipBlanks() {
        while (curPos < curLine.length() && Character.isWhitespace(curLine.charAt(curPos))) {
            curPos++;
        }
    }

    private boolean isCorrectEnd() {
        while (curPos < curLine.length()) {
            if (!Character.isWhitespace(curLine.charAt(curPos))) {
                return false;
            }
            curPos++;
        }
        return true;
    }

    private int getIndex() {
        skipBlanks();
        StringBuilder intStr = new StringBuilder();
        // :NOTE: * Не поддерживаются отрицательные числа
        while (curPos < curLine.length() && Character.isDigit(curLine.charAt(curPos))) {
            intStr.append(curLine.charAt(curPos));
            curPos++;
        }
        String str = intStr.toString();

        // :NOTE: - .isEmpty()
        if (str.equals("")) {
            throw err("Incorrect index");
        } else {
            return Integer.parseInt(str);
        }
    }

    private String getString() {
        skipBlanks();
        StringBuilder name = new StringBuilder();
        // :NOTE: # Не поддерживаются строки с пробелами
        while (curPos < curLine.length() && !Character.isWhitespace(curLine.charAt(curPos))) {
            name.append(curLine.charAt(curPos));
            curPos++;
        }
        String str = name.toString();
        // :NOTE: * Не все строки имена файлов
        if (str.equals("") || str.length() > MAX_FILENAME) {
            throw err("Incorrect file name");
        }
        return str;
    }

    public int nextIndex() {
        if (curCom != Command.REMOVE) {
            curCom = Command.ERR;
            throw err("Incorrect command before");
        }
        int index = getIndex();
        if (isCorrectEnd()) {
            return index;
        } else {
            throw err("Incorrect index end");
        }
    }

    public String nextFile() {
        if (curCom != Command.PRINT) {
            curCom = Command.ERR;
            throw err("Incorrect command");
        }
        String name = getString();
        if (isCorrectEnd()) {
            return name;
        } else {
            throw err("Incorrect end of file name");
        }
    }

    public Pair nextPair() {
        int index = getIndex();
        String ans = getString();
        if (!isCorrectEnd()) {
            throw err("Incorrect end of command");
        }
        return new Pair(index, ans);
    }

    private ParseException err(String msg) {
        return new ParseException("Exception in line " + (lineNum - 1) + " " + msg);
    }
}
