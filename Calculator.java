import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame implements ActionListener {

    JTextField display;
    JLabel history;

    double num1 = 0, num2 = 0;
    String op = "";
    boolean newInput = true;

    public Calculator() {

        // ── WINDOW ──
        setTitle("Advanced Calculator Pro");
        setSize(460, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // ── BACKGROUND + BORDER ──
        getContentPane().setBackground(new Color(18, 20, 30));

        ((JComponent) getContentPane()).setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 75, 95), 2, true),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        );

        // ── HISTORY ──
        history = new JLabel("", SwingConstants.RIGHT);
        history.setBounds(25, 20, 400, 30);
        history.setForeground(new Color(170, 170, 170, 180));
        history.setFont(new Font("Arial", Font.PLAIN, 14));
        add(history);

        // ── DISPLAY ──
        display = new JTextField("0");
        display.setBounds(25, 60, 400, 80);
        display.setFont(new Font("Arial", Font.BOLD, 40));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setEditable(false);
        display.setBackground(new Color(12, 14, 24));
        display.setForeground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(display);

        // ── BUTTON PANEL ──
        JPanel panel = new JPanel();
        panel.setBounds(25, 160, 400, 540);
        panel.setLayout(new GridLayout(6, 4, 12, 12));
        panel.setBackground(new Color(18, 20, 30));

        Font btnFont = new Font("Arial", Font.BOLD, 20);

        panel.add(btn("AC", Color.RED, btnFont));
        panel.add(btn("⌫", new Color(80, 80, 90), btnFont));
        panel.add(btn("%", new Color(80, 80, 90), btnFont));
        panel.add(btn("/", new Color(0, 180, 160), btnFont));

        panel.add(btn("7", dark(), btnFont));
        panel.add(btn("8", dark(), btnFont));
        panel.add(btn("9", dark(), btnFont));
        panel.add(btn("*", new Color(0, 180, 160), btnFont));

        panel.add(btn("4", dark(), btnFont));
        panel.add(btn("5", dark(), btnFont));
        panel.add(btn("6", dark(), btnFont));
        panel.add(btn("-", new Color(0, 180, 160), btnFont));

        panel.add(btn("1", dark(), btnFont));
        panel.add(btn("2", dark(), btnFont));
        panel.add(btn("3", dark(), btnFont));
        panel.add(btn("+", new Color(0, 180, 160), btnFont));

        panel.add(btn("√", new Color(60, 60, 70), btnFont));
        panel.add(btn("x²", new Color(60, 60, 70), btnFont));
        panel.add(btn("1/x", new Color(60, 60, 70), btnFont));
        panel.add(btn("=", new Color(0, 210, 180), btnFont));

        panel.add(btn("0", dark(), btnFont));
        panel.add(btn(".", dark(), btnFont));
        panel.add(btn("+/-", new Color(60, 60, 70), btnFont));
        panel.add(new JLabel());

        add(panel);

        // ── AUTHOR (FINAL SIMPLE VERSION) ──
        JLabel author = new JLabel("Developed by Md Rayhan Hossain", SwingConstants.CENTER);
        author.setBounds(25, 720, 400, 25);
        author.setForeground(new Color(140, 140, 140));
        author.setFont(new Font("Arial", Font.PLAIN, 12));
        add(author);

        setVisible(true);
    }

    // ── DARK COLOR ──
    Color dark() {
        return new Color(35, 38, 55);
    }

    // ── BUTTON FACTORY ──
    JButton btn(String text, Color color, Font font) {
        JButton b = new JButton(text);

        b.setFont(font);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);

        b.addActionListener(this);

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(color.brighter());
            }

            public void mouseExited(MouseEvent e) {
                b.setBackground(color);
            }
        });

        return b;
    }

    // ── ACTION ──
    public void actionPerformed(ActionEvent e) {
        handle(e.getActionCommand());
    }

    void handle(String s) {

        String cur = display.getText();

        try {

            if (s.matches("[0-9]")) {
                if (cur.equals("0") || newInput) {
                    display.setText(s);
                    newInput = false;
                } else {
                    display.setText(cur + s);
                }
                return;
            }

            switch (s) {

                case "AC":
                    display.setText("0");
                    history.setText("");
                    num1 = num2 = 0;
                    op = "";
                    break;

                case "⌫":
                    display.setText(cur.length() > 1 ? cur.substring(0, cur.length() - 1) : "0");
                    break;

                case "+/-":
                    display.setText(String.valueOf(-Double.parseDouble(cur)));
                    break;

                case "%":
                    display.setText(String.valueOf(Double.parseDouble(cur) / 100));
                    break;

                case "√":
                    display.setText(String.valueOf(Math.sqrt(Double.parseDouble(cur))));
                    break;

                case "x²":
                    double x = Double.parseDouble(cur);
                    display.setText(String.valueOf(x * x));
                    break;

                case "1/x":
                    double v = Double.parseDouble(cur);
                    display.setText(v == 0 ? "Error" : String.valueOf(1 / v));
                    break;

                case "+":
                case "-":
                case "*":
                case "/":
                    num1 = Double.parseDouble(cur);
                    op = s;
                    newInput = true;
                    break;

                case "=":
                    num2 = Double.parseDouble(cur);

                    double res = 0;
                    switch (op) {
                        case "+": res = num1 + num2; break;
                        case "-": res = num1 - num2; break;
                        case "*": res = num1 * num2; break;
                        case "/": res = num2 == 0 ? 0 : num1 / num2; break;
                    }

                    history.setText(num1 + " " + op + " " + num2);
                    display.setText(String.valueOf(res));
                    newInput = true;
                    break;
            }

        } catch (Exception ex) {
            display.setText("Error");
        }
    }

    // ── MAIN ──
    public static void main(String[] args) {
        new Calculator();
    }
}