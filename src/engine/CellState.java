/* Don-CODE */
package engine;

public enum CellState {
    
    COMPUTER("X"), USER("O"), EMPTY("");

    private final String text;

    private CellState(String text) {
        this.text = text;
    }
    
    @Override
    public String toString() {
        return this.text;
    }
}
