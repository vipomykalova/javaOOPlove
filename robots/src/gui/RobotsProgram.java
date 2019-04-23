package gui;

/**
 * Библиотеки для создания и управоения графикой
 */
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.Frame;
/**
 * Класс запуска программы.
 * @author Клепинин
 * @version 1.0
 */
public class RobotsProgram
{
  /**
   * Главный метод, который вызывается призапуске
   * Создание инструмента графики, запуск класса MainApplication, который вызывает методы отрисовки
   * синхронное выполнение событий <b>SwingUtilities.invokeLater</b>
   * @see MainApplicationFrame
   */
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    SwingUtilities.invokeLater(() -> {
      MainApplicationFrame frame = new MainApplicationFrame();
      frame.pack();
      frame.setVisible(true);
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    });
  }}