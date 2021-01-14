package script;

public enum Command {
    ADD(3), REMOVE(6), PRINT(5), ERR(3), END(3);

    Command(int length) {
        this.length = length;
    }

    public int length;

}
