package java2js;

public interface Printer {
    Printer tab(String tab);
    void print(String message, Object... args);
    void println(String message, Object... args);
}
