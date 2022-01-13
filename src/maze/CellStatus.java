package maze;

public enum CellStatus {

    WALL("\u2588\u2588"),
    WALL_OR_EMPTY("\u2588\u2588"),
    EMPTY("  "),
    CHECKED("  "),
    PATH("//"),
    END("//");

    public final String value;

    CellStatus(String value) {
        this.value = value;
    }
}