package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 * Класс, создающий окно для игры.
 * Экземпляр этого класса используется в {@link MainApplicationFrame}, там же и закрепляется за главным окном
 * @author Александр Клепинин
 * @version 1.0
 */
public class GameWindow extends JInternalFrame
{
    /**
     * Экзмепляр класса игровой логики {@link GameVisualizer}
     */
    private final GameVisualizer m_visualizer;

    /** Конструктор - создает игровое окно */
    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    /**
     * Публичный метод для возвращения приватного поля текущей игры
     * @return текущая игра
     */
    public GameVisualizer getVisualizer(){
        return m_visualizer;
    }
}
