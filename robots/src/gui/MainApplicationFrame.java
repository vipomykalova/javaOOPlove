package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import javax.swing.*;

import log.Logger;
import hibernate.DatabaseManager;

/**
 * Класс, отвечающий за создание главного окна и обеспечивающий взаимодействие всех окон
 * @author Александр Клепинин, Помыкалова Виктория, Кононский Павел
 * @version 1.2
 * @see GameVisualizer
 * @see GameWindow
 */

public class MainApplicationFrame extends JFrame {

    private HashMap<String, Long> gameStates = new HashMap<String, Long>();

    private volatile DatabaseManager databaseManager = new DatabaseManager();
    /** Главное окно */
    private final JDesktopPane desktopPane = new JDesktopPane();
    /** Поле игры */
    public GameWindow gameWindow;
    /**
     * Конструктор - создает объект главного окна, закрепляет остальные окна на главное
     */
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        databaseManager.loadStatesFromDatabase(gameStates);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width - inset * 2,
                screenSize.height - inset * 2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Метод создает окно для логгирования
     * @return окно логгирования
     * @see MainApplicationFrame#MainApplicationFrame()
     */
    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Метод добавляет окно на {@link MainApplicationFrame#desktopPane}
     * @param frame окно, которое следует закрепить на главном окне
     */
    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Метод создает Menu Bar с режимами отображения окон, с тестами и с настройками логгирования,
     * с кнопками сохранить и загрузить
     * @return Menu Bar в {@link MainApplicationFrame#MainApplicationFrame()}
     */

    private JMenuBar generateMenuBar() {
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
                saveState();
            });
            saveMenu.add(save);
        }
        {
            JMenuItem load = new JMenuItem("Загрузить", KeyEvent.VK_S);
            load.addActionListener((event) -> {
                loadState();
            });
            saveMenu.add(load);
        }

        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        menuBar.add(saveMenu);
        return menuBar;
    }

    /**
     * Метод сохраняет состояние игры при нажатии на соответствующую кнопку,
     * пользователь сам выбирает место и файл для сохранения
     */
    private void saveState() {
        //databaseManager.addGameState(gameWindow.getVisualizer());
        gameWindow.getVisualizer().stopTimer();
        JInternalFrame saveWindow = new JInternalFrame("Введите название", false, true,false,false);
        saveWindow.setLocation(420,0);
        saveWindow.setSize(300, 300);

        JPanel panel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField();
        JButton button = new JButton("Сохранить");

        DefaultListModel listModel = new DefaultListModel();
        JList list = new JList(listModel);
        for(String el: gameStates.keySet()){
            listModel.addElement(el);
        }
        list.setLayoutOrientation(JList.VERTICAL);
        list.setLocation(420, 95);
        list.setSize(245, 150);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String gameName = textField.getText();
                if (gameName.equals("")) {
                    textField.setText("Введи имя!");
                } else {

                    gameWindow.getVisualizer().nameOfCurrentGame = gameName;
                    Long id = databaseManager.addGameState(gameWindow.getVisualizer());
                    gameStates.put(gameName, id);
                    gameWindow.getVisualizer().setTimer();
                    saveWindow.dispose();
                }
            }
        });
        panel.add(textField, BorderLayout.NORTH);
        panel.add(new JLabel("Сохранённые игры:"), BorderLayout.BEFORE_LINE_BEGINS);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
        saveWindow.add(panel);
        addWindow(saveWindow);

    }

    /**
     * Метод загружает состояние игры при нажатии на соответствующую кнопку,
     * пользователь сам выбирает место и файл для загрузки
     */
    private void loadState() {

        JInternalFrame loadWindow = new JInternalFrame("Выберите одну из игр", false, true,false,false);
        loadWindow.setLocation(420,0);
        loadWindow.setSize(300, 100);
        String[] some = new String[gameStates.size()];
        int i = 0;
        for (String el: gameStates.keySet()) {
            some[i] = el;
            i++;
        }

        JComboBox comboBox = new JComboBox(some);

        JPanel loadPanel = new JPanel();
        JButton button = new JButton("Загрузить");
        loadPanel.setLayout(new BorderLayout());
        loadPanel.add(comboBox, BorderLayout.NORTH);
        loadPanel.add(button, BorderLayout.SOUTH);
        loadWindow.add(loadPanel);
        addWindow(loadWindow);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String gameName = comboBox.getSelectedItem().toString();
                GameVisualizer newGame = databaseManager.getGameState(gameStates.get(gameName));
                gameWindow.setSize((int)newGame.currentWidth, (int)newGame.currentHeight);
                gameWindow.getVisualizer().m_robotPositionX = newGame.m_robotPositionX;
                gameWindow.getVisualizer().m_robotPositionY = newGame.m_robotPositionY;
                gameWindow.getVisualizer().m_robotDirection = newGame.m_robotDirection;
                gameWindow.getVisualizer().m_targetPositionX = newGame.m_targetPositionX;
                gameWindow.getVisualizer().m_targetPositionY = newGame.m_targetPositionY;
                loadWindow.dispose();
            }
        });

    }

    /**
     * Устанавливает режим отображения окон при нажатии пользователем соответсвующей кнопки
     * @param className режим отображения, передается из {@link MainApplicationFrame#generateMenuBar()}
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // just ignore
        }
    }
}
