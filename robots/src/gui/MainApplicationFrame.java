package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import log.Logger;

/**
 * Класс, отвечающий за создание главного окна и обеспечивающий взаимодействие всех окон
 * @author Александр Клепинин, Помыкалова Виктория, Кононский Павел
 * @version 1.2
 * @see GameVisualizer
 * @see GameWindow
 */

public class MainApplicationFrame extends JFrame {
    /** Главное окно */
    private final JDesktopPane desktopPane = new JDesktopPane();
    /** Поле игры */
    public GameWindow gameWindow;
    /** Менджер сохранения и загрузки */
    private SaveAndLoadGame saveLoadManager = new SaveAndLoadGame();
    /**
     * Конструктор - создает объект главного окна, закрепляет остальные окна на главное
     */
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
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
                saveState(saveMenu);
            });
            saveMenu.add(save);
        }
        {
            JMenuItem load = new JMenuItem("Загрузить", KeyEvent.VK_S);
            load.addActionListener((event) -> {
                loadState(saveMenu);
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
     * @param saveMenu окно, отвечающее за сохранение и загрузку состояния, передается из {@link MainApplicationFrame#generateMenuBar()}
     */
    private void saveState(JMenu saveMenu) {
        gameWindow.getVisualizer().stopTimer();
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("robots//src//saves"));
        int ret = jFileChooser.showSaveDialog(saveMenu);
        if (ret == jFileChooser.APPROVE_OPTION) {
            try {
                String FileName = "";
                if (!jFileChooser.getSelectedFile().toString().endsWith(".txt")){
                    FileName = jFileChooser.getSelectedFile() + ".txt";
                }
                else {
                    FileName = jFileChooser.getSelectedFile().toString();
                }
                String dataForSave = saveLoadManager.getDataForSave(gameWindow);
                saveLoadManager.saveData(dataForSave, FileName);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        gameWindow.getVisualizer().setTimer();
    }

    /**
     * Метод загружает состояние игры при нажатии на соответствующую кнопку,
     * пользователь сам выбирает место и файл для загрузки
     * @param saveMenu окно, отвечающее за сохранение и загрузку состояния, передается из {@link MainApplicationFrame#generateMenuBar()}
     */
    private void loadState(JMenu saveMenu) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("robots//src//saves"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("Only text files", "txt"));
        int ret = jFileChooser.showOpenDialog(saveMenu);
        if (ret == jFileChooser.APPROVE_OPTION) {
            try {
                File file = jFileChooser.getSelectedFile();
                String loadState = saveLoadManager.getDataFromSource(file);
                if (!saveLoadManager.setLoadData(gameWindow, loadState)) {
                    JOptionPane.showMessageDialog(jFileChooser, "Файл повреждён!",
                            "Ошибка загрузки", JOptionPane.WARNING_MESSAGE);
                }

            } catch (FileNotFoundException e2) {
                JOptionPane.showMessageDialog(jFileChooser, "Файла с таким именем нет!",
                        "Ошибка загрузки", JOptionPane.WARNING_MESSAGE);
            }
            catch (Exception e1) {
                JOptionPane.showMessageDialog(jFileChooser, "Файл повреждён!",
                        "Ошибка загрузки", JOptionPane.WARNING_MESSAGE);
            }
        }
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
