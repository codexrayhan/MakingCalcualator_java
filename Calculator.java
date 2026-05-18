import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Main UI Class
 */
public class Calculator implements ActionListener, KeyListener {

    private final CalculatorEngine engine = new CalculatorEngine();
    private JFrame frame;
    private JTextField display;
    private JPanel panel;

    private static final Font DISPLAY_FONT = new Font("Consolas", Font.PLAIN, 28);
    private static final Font BUTTON_FONT  = new Font("Segoe UI", Font.BOLD, 20);

    private static final String[][] BUTTON_GRID = {
            { "CLR", "DEL", "%", "√" },
            { "7",   "8",   "9", "/" },
            { "4",   "5",   "6", "*" },
            { "1",   "2",   "3", "-" },
            { "(-)", "0",   ".", "+" },
    };

    public Calculator() {
        buildUI();
    }

    private void buildUI() {
        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(380, 520);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout(10, 10));

        display = new JTextField("0");
        display.setFont(DISPLAY_FONT);
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBorder(new EmptyBorder(10, 10, 10, 10));
        display.setBackground(new Color(30, 30, 30));
        display.setForeground(Color.WHITE);
        frame.add(display, BorderLayout.NORTH);

        panel = new JPanel(new GridLayout(5, 4, 6, 6));
        panel.setBorder(new EmptyBorder(6, 10, 10, 10));
        panel.setBackground(new Color(45, 45, 45));

        for (String[] row : BUTTON_GRID) {
            for (String label : row) {
                panel.add(makeButton(label));
            }
        }
        frame.add(panel, BorderLayout.CENTER);

        JButton equalBtn = makeButton("=");
        equalBtn.setBackground(new Color(39, 174, 96));
        equalBtn.setForeground(Color.WHITE);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        bottomPanel.setBackground(new Color(45, 45, 45));
        bottomPanel.add(equalBtn, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        display.addKeyListener(this);
        frame.addKeyListener(this);
        frame.setVisible(true);
    }

    private JButton makeButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(BUTTON_FONT);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);

        switch (label) {
            case "CLR", "DEL"       -> { btn.setBackground(new Color(192, 57, 43)); btn.setForeground(Color.WHITE); }
            case "+", "-", "*", "/" -> { btn.setBackground(new Color(41, 128, 185)); btn.setForeground(Color.WHITE); }
            case "%", "√", "(-)"    -> { btn.setBackground(new Color(142, 68, 173)); btn.setForeground(Color.WHITE); }
            default                 -> { btn.setBackground(new Color(60, 60, 60));  btn.setForeground(Color.WHITE); }
        }

        btn.addActionListener(this);
        return btn;
    }

    private void handleInput(String input) {
        String current = display.getText();

        switch (input) {
            case "CLR" -> {
                engine.clear();
                display.setText("0");
            }
            case "DEL" -> {
                if (engine.isResultShown()) { display.setText("0"); engine.clear(); return; }
                if (current.length() <= 1) { display.setText("0"); return; }
                String trimmed = current.substring(0, current.length() - 1);
                display.setText(trimmed.stripTrailing().isEmpty() ? "0" : trimmed);
            }
            case "=" -> display.setText(engine.calculate(current));
            case "(-)" -> display.setText(engine.negate(lastToken(current)));
            case "%" -> {
                if (!engine.isOperatorPressed()) display.setText(engine.percentage(current));
            }
            case "√" -> {
                if (!engine.isOperatorPressed()) display.setText(engine.squareRoot(current));
            }
            case "." -> {
                if (!lastToken(current).contains("."))
                    display.setText(current.equals("0") ? "0." : current + ".");
            }
            case "+", "-", "*", "/" -> {
                if (engine.isResultShown()) {
                    String numPart = lastToken(current);
                    display.setText(numPart + " " + input + " ");
                    engine.pressOperator(input.charAt(0), numPart);
                } else if (!engine.isOperatorPressed()) {
                    engine.pressOperator(input.charAt(0), current);
                    display.setText(current + " " + input + " ");
                }
            }
            default -> {
                if (current.equals("0") && !engine.isOperatorPressed()) display.setText(input);
                else display.setText(current + input);
            }
        }
    }

    private String lastToken(String text) {
        String[] parts = text.trim().split("\\s+");
        return parts[parts.length - 1];
    }

    @Override public void actionPerformed(ActionEvent e) { handleInput(((JButton) e.getSource()).getText()); }
    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        char c   = e.getKeyChar();
        if (Character.isDigit(c)) handleInput(String.valueOf(c));
        else if ("+-*/".indexOf(c) >= 0) handleInput(String.valueOf(c));
        else if (c == '.') handleInput(".");
        else if (c == '\n' || c == '=') handleInput("=");
        else if (c == '\b' || code == KeyEvent.VK_DELETE) handleInput("DEL");
        else if (code == KeyEvent.VK_ESCAPE) handleInput("CLR");
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}

/**
 * Logic Engine
 */
class CalculatorEngine {
    private BigDecimal num1 = BigDecimal.ZERO;
    private char operator = ' ';
    private boolean operatorPressed = false;
    private boolean resultShown = false;

    public void clear() {
        num1 = BigDecimal.ZERO;
        operator = ' ';
        operatorPressed = false;
        resultShown = false;
    }

    public void pressOperator(char op, String currentNum) {
        num1 = new BigDecimal(currentNum);
        operator = op;
        operatorPressed = true;
        resultShown = false;
    }

    public String calculate(String currentDisplay) {
        if (!operatorPressed) return currentDisplay;
        try {
            String[] parts = currentDisplay.trim().split("\\s+");
            BigDecimal num2 = new BigDecimal(parts[parts.length - 1]);
            BigDecimal result = switch (operator) {
                case '+' -> num1.add(num2);
                case '-' -> num1.subtract(num2);
                case '*' -> num1.multiply(num2);
                case '/' -> num1.divide(num2, 10, RoundingMode.HALF_UP);
                default -> num2;
            };
            resultShown = true;
            operatorPressed = false;
            return result.stripTrailingZeros().toPlainString();
        } catch (Exception e) { return "Error"; }
    }

    public String negate(String current) { return new BigDecimal(current).negate().toPlainString(); }
    public String percentage(String current) { return new BigDecimal(current).divide(new BigDecimal("100")).toPlainString(); }
    public String squareRoot(String current) { return String.valueOf(Math.sqrt(Double.parseDouble(current))); }
    public boolean isOperatorPressed() { return operatorPressed; }
    public boolean isResultShown() { return resultShown; }
}
