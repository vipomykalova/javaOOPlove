package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается.
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private GameWindow gameWindow;
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

//    protected JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        //Set up the lone menu.
//        JMenu menu = new JMenu("Document");
//        menu.setMnemonic(KeyEvent.VK_D);
//        menuBar.add(menu);
//
//        //Set up the first menu item.
//        JMenuItem menuItem = new JMenuItem("New");
//        menuItem.setMnemonic(KeyEvent.VK_N);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_N, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("new");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        //Set up the second menu item.
//        menuItem = new JMenuItem("Quit");
//        menuItem.setMnemonic(KeyEvent.VK_Q);
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(
//                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
//        menuItem.setActionCommand("quit");
////        menuItem.addActionListener(this);
//        menu.add(menuItem);
//
//        return menuBar;
//    }

    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");

        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }

        JMenu saveMenu = new JMenu("Сохранить/Загрузить");
        saveMenu.setMnemonic(KeyEvent.VK_T);
        {
            JMenuItem save = new JMenuItem("Сохранить", KeyEvent.VK_S);
            save.addActionListener((event) -> {
                addWindow(createSaveWindow());
            });
            saveMenu.add(save);
        }
        {
            JMenuItem load = new JMenuItem("Загрузить", KeyEvent.VK_S);
            load.addActionListener((event) -> {
                addWindow(createLoadWindow());
            });
            saveMenu.add(load);
        }

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(saveMenu);
        return menuBar;
    }

    private JInternalFrame createSaveWindow(){
        gameWindow.getVisualizer().stopTimer();
        gameWindow.getVisualizer().isEditor = true;
        ArrayList<String> stateBeforeSave = gameWindow.getVisualizer().getGameState();
        JInternalFrame saveWindow = new JInternalFrame("Введите название", false, true,false,false);
        saveWindow.setLocation(420,0);
        saveWindow.setSize(300, 100);

        JPanel panel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField();
        JButton button = new JButton("Сохранить");

        saveWindow.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                gameWindow.getVisualizer().isEditor = false;
                gameWindow.getVisualizer().setPosition(stateBeforeSave);
                gameWindow.getVisualizer().setTimer();

            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    String fileName = "robots//src//saves//"+ textField.getText() + ".txt";
                    File file = new File(fileName);
                    try {
                        file.createNewFile();
                        FileWriter writer = new FileWriter(fileName, false);
                        ArrayList<String> state = gameWindow.getVisualizer().getGameState();
                        for(int i = 0; i < state.size(); i++){
                            writer.write(state.get(i));
                            writer.write("\n");
                        }
                        writer.flush();
                        writer.close();
                        saveWindow.dispose();
                        gameWindow.getVisualizer().isEditor = false;
                        gameWindow.getVisualizer().setPosition(stateBeforeSave);
                        gameWindow.getVisualizer().setTimer();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
            }

        });

        panel.add(button, BorderLayout.SOUTH);
        panel.add(textField, BorderLayout.NORTH);
        saveWindow.add(panel);
        return saveWindow;

    }

    private JInternalFrame createLoadWindow(){
        JInternalFrame saveWindow = new JInternalFrame("Введите название", false, true,false,false);
        saveWindow.setLocation(420,0);
        saveWindow.setSize(300, 100);

        JPanel panel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField();
        JButton button = new JButton("Загрузить");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = "robots//src//saves//"+ textField.getText() + ".txt";
                try {
                    FileReader reader = new FileReader(fileName);
                    Scanner scan = new Scanner(reader);
                    ArrayList<String> gameState = new ArrayList<String>();
                    while (scan.hasNextLine()) {
                        gameState.add(scan.nextLine());
                    }
                    reader.close();
                    gameWindow.setSize(Integer.parseInt(gameState.get(5)), Integer.parseInt(gameState.get(6)));
                    gameWindow.getVisualizer().setPosition(gameState);
                    saveWindow.dispose();
                }catch (FileNotFoundException e2) {
                    JOptionPane.showMessageDialog(saveWindow, "Файла с таким именем нет!",
                            "Ошибка загрузки", JOptionPane.WARNING_MESSAGE);
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        panel.add(button, BorderLayout.SOUTH);
        panel.add(textField, BorderLayout.NORTH);
        saveWindow.add(panel);
        return saveWindow;

    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
