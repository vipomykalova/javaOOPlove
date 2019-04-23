package Tests;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import gui.GameVisualizer;

/**
 * Тесты на правильность алгоритма в игровой логике
 * @author Виктория Помыкалова, Мустафина Екатерина
 * @version 2.0
 * @see gui.GameVisualizer
 */
public class LogicTest {

    /** Экземпляр игры*/
    private GameVisualizer game = new GameVisualizer();

    /**
     * Метод тестирует то, что робот не двинется с места, если еда рядом с ним
     */
    @Test
    void defaultTest(){
        game.m_robotPositionX = 150.8;
        game.onModelUpdateEvent();
        assertEquals(game.m_robotPositionX, 150.8);
    }

    /**
     * Метод тестирует то, что робот меняет угол поворота при нахождении еды не на одной прямой с роботом
     */
    @Test
    void runDirectTest(){
        game.m_robotPositionX = 100;
        game.m_robotPositionY = 100;
        game.m_robotDirection = 0;
        game.onModelUpdateEvent();
        assertEquals(Math.round(game.m_robotDirection), 0);
        game.m_robotDirection = 0.6;
        assertEquals(Math.ceil(Math.toDegrees(game.m_robotDirection)), 35);
    }

    /**
     * Метод тестирует то, что робот движется к еде при нахождении еды не рядом с роботом
     */
    @Test
    void runNoDirectTest(){
        game.m_robotDirection = 3.1415926;
        game.m_robotPositionY = 90;
        game.m_robotPositionX = 90;
        game.onModelUpdateEvent();
        assertEquals(game.m_robotPositionX, 89.5);
        assertEquals(Math.ceil(game.m_robotPositionY), 91);
    }

    /**
     * Метод тестирует то, что игра закончится при выходе за границы игрового поля
     */
    @Test
    void runBorderTest(){
        game.currentHeight = 50;
        game.currentWidth = 20;
        assertTrue(game.isRobotAbroad(10, 20, 0.1));
    }

    /**
     * Метод тестирует то, что игра не закончится, если робот не зайдет за границы игрового поля
     */
    @Test
    void runNoBorderTest(){
        game.currentHeight = 50;
        game.currentWidth = 20;
        assertFalse(game.isRobotAbroad(10, 20, 1.6));
        game.currentWidth = 50;
        assertFalse(game.isRobotAbroad(30, 20, 0.1));
    }
}