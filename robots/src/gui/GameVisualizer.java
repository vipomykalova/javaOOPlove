package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.persistence.*;

import javax.swing.*;

/**
 * Класс визуализации игры, который наследуется от <b>JPanel</b>
 * @author Клепинин, Помыкалова, Мустафина, Кононских
 * @version 3.0
 */
@Entity
@Table(name = "GAME_STATES")
public class GameVisualizer extends JPanel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * переменная таймер, определяющая время работы
     */
    @Transient
    private Timer m_timer = initTimer();
    /**
     * Статический метод создания таймера
     * @return этот созданый таймер
     */

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    @Column(name = "name_game")
    public String nameOfCurrentGame;

    /**
     * переменная показывающая позицию жука по координате Х
     */
    @Column(name = "bug_x")
    public volatile double m_robotPositionX;
    /**
     * переменная показывающая позицию жука по координате У
     */
    @Column(name = "bug_y")
    public volatile double m_robotPositionY;
    /**
     * переменная показывающая направление жука
     */
    @Column(name = "bug_dir")
    public volatile double m_robotDirection;

    /**
     * переменная показывающая позицию еды по координате Х
     */
    @Column(name = "food_x")
    public volatile int m_targetPositionX;
    /**
     * переменная показывающая позицию еды по координате У
     */
    @Column(name = "food_y")
    public volatile int m_targetPositionY;

    /**
     * Переменная обзначающая ширину игрового поля
     */
    @Column(name = "field_width")
    public volatile double currentWidth;
    /**
     * Переменная обзначающая высоту игрового поля
     */
    @Column(name = "field_height")
    public volatile double currentHeight;

    /**
     * переменная максимальной скорости жука
     */
    private static final double maxVelocity = 0.1;
    /**
     * переменная максимальной скорости жука
     */
    private static final double maxAngularVelocity = 0.001;

    /**
     * Конструктор, который привызове ставить жука и еду на начальные позиции,
     * включает таймер и начинает прослушивать соббытия нажатия кнопок мыши и обрабатывает их
     */
    public GameVisualizer() {
        setStartPosition();
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
            }
        });
        setDoubleBuffered(true);
    }

    /**
     * Метод сброса тайиера, и задавание нового для перерисовки объектов
     */
    public void stopTimer() {
        m_timer.cancel();
        m_timer = initTimer();
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
    }

    /**
     * Обновление событий движения игры
     */
    public void setTimer() {
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
    }

    /**
     * Метод добавляющий в массив начальное местоположение жука и еды, это необходимо для сохранения
     * @return этот массив
     */
    public ArrayList<String> getGameState() {
        ArrayList<String> gameState = new ArrayList<String>();
        gameState.add(String.valueOf(m_robotPositionX));
        gameState.add(String.valueOf(m_robotPositionY));
        gameState.add(String.valueOf(m_robotDirection));
        gameState.add(String.valueOf(m_targetPositionX));
        gameState.add(String.valueOf(m_targetPositionY));
        gameState.add(String.valueOf(super.getSize().width));
        gameState.add(String.valueOf(super.getSize().height));
        return gameState;

    }

    /**
     * Метод задающий начальную позицию игры, то есть где находится еда и жук, и напрвавление жука
     */
    protected void setStartPosition() {

        m_robotPositionX = 100;
        m_robotPositionY = 100;
        m_robotDirection = 0;
        m_targetPositionX = 150;
        m_targetPositionY = 100;
    }

    /**
     * Метод добавляющий в массив текущее местоположение жука и еды, это необходимо для сохранения
     * @param gameState название массива где храняться данные игры
     * @throws NumberFormatException выбрасывается если неудалось распарсить строку в число
     */
    public void setPosition(ArrayList<String> gameState) throws NumberFormatException{
        m_robotPositionX = Double.parseDouble(gameState.get(0));
        m_robotPositionY = Double.parseDouble(gameState.get(1));
        m_robotDirection = Double.parseDouble(gameState.get(2));
        m_targetPositionX = Integer.parseInt(gameState.get(3));
        m_targetPositionY = Integer.parseInt(gameState.get(4));

    }

    /**
     * Метод меняющий положение еды
     * @param p приходящая точка, когда мы нажали на кнопку мыши
     */
    protected void setTargetPosition(Point p) {
        m_targetPositionX = p.x;
        m_targetPositionY = p.y;
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    /**
     * Метод вычисляющий расстояние от жука до еды
     * @param x1 координата жука
     * @param y1 координата жука
     * @param x2 координата еды
     * @param y2 координата еды
     * @return расстояние от жука до еды
     */
    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * Метод вычисляющий направление жука, через тангенс угла между жуком и едой
     * @param fromX позиция жука
     * @param fromY позиция жука
     * @param toX позиция еды
     * @param toY позиция жука
     * @return возращает угол в радианах
     */
    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    /**
     * Метод проверяющий что жук выходит за границу поля, если он выходит то умирает, и высвечивается соообщение о новой игре
     * @param x позиция жука
     * @param y позиция жука
     * @param direction направление жука
     * @return есди жук врезался то возрашает <b>True</b>, иначе <b>False</b>
     */
    public boolean isRobotAbroad(double x, double y, double direction) {

        double distToLeftBoarder = x - Math.abs(Math.cos(direction)) * 15;
        double distToUpBoarder = y - Math.abs(Math.sin(direction)) * 15;
        double distToRightBoarder = x + Math.abs(Math.cos(direction)) * 15;
        double distToDownBoarder = y + Math.abs(Math.sin(direction)) * 15;

        if (currentWidth != 0 && currentHeight != 0) {

            if ((distToLeftBoarder <= 0 || distToLeftBoarder >= currentWidth) ||
                    (distToRightBoarder <= 0 || distToRightBoarder >= currentWidth) ||
                    (distToDownBoarder <= 0 || distToDownBoarder >= currentHeight) ||
                    (distToUpBoarder <= 0 || distToUpBoarder >= currentHeight)) {

                JOptionPane.showConfirmDialog(super.getParent(),
                        "Кликни, если хочешь еще поиграть :)",
                        "RIP жучок", JOptionPane.DEFAULT_OPTION);
                return true;
            }
        }
        return false;
    }

    /**
     * Метод обновления событий, движения объектов
     * если жук вышел за границы поля, вызывается метод {@link GameVisualizer#setStartPosition()}
     * иначе вычисляет расстояние до еды, и от его значения еняет свои координаты
     */
    public void onModelUpdateEvent() {
        currentWidth = super.getSize().width;
        currentHeight = super.getSize().height;
        if (isRobotAbroad(m_robotPositionX, m_robotPositionY, m_robotDirection)) {
            setStartPosition();
        }

        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance < 0.7) {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double angularVelocity = 0;
        double angleBetweenTargetRobot = asNormalizedRadians(angleToTarget - m_robotDirection);
        if (angleBetweenTargetRobot < Math.PI) {
            angularVelocity = maxAngularVelocity;
        } else {
            angularVelocity = -maxAngularVelocity;
        }

        if (Math.abs(angleToTarget - m_robotDirection) < 0.05) {

            moveRobot(velocity, angularVelocity, 10);
        } else {
            if (distance < 15) {
                moveRobot(0, angularVelocity, 10);
            } else {
                moveRobot(velocity / 2, angularVelocity, 10);
            }
        }
    }

    /**
     * Метод изменяющий координаты жука, вызывается в {@link GameVisualizer#onModelUpdateEvent()}
     * @param velocity скорость жука
     * @param angularVelocity угловая скорость жука
     * @param duration направление жука
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        double newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        double newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);

        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = m_robotDirection + angularVelocity * duration * 4;
        m_robotDirection = asNormalizedRadians(newDirection);
    }

    /**
     * Статический метод преобразования угла в радианы от 0 до 2P
     * @param angle угол между жуком и едой {@link GameVisualizer#angleTo(double, double, double, double)}
     * @return нормализованный угол между жуком и едой
     */
    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    /**
     * Статический метод, увеличивающий переменную на 0.5
     * @param value значение которое нужно увеличить
     * @return возращает принятое значение + 0.5
     */
    private static int round(double value) {
        return (int) (value + 0.5);
    }

    /**
     * Переопределяем метод рисование объектов
     * @param g графика отрисовки
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
    }

    /**
     * Метод рисования закрашенного
     * @param g графика отрисовки
     * @param centerX центр тела по Х
     * @param centerY центр тела по У
     * @param diam1 диаметрг овала для Х
     * @param diam2 диаметр овала для У
     */
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Метод рисования овала
     * @param g графика отрисовки
     * @param centerX центр тела по Х
     * @param centerY центр тела по У
     * @param diam1 диаметр овала для Х
     * @param diam2 диаметр овала для У
     */
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Метод отрисовки жука
     * @param g графика отрисовки
     * @param x координата жука Х
     * @param y координата жука У
     * @param direction направление жука, его разворот на плоскости от 0 до 2Р
     */
    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = round(m_robotPositionX);
        int robotCenterY = round(m_robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    /**
     * Метод отрисовки еды по заданным координатам
     * @param g графика отрисовки
     * @param x координата еды по Х
     * @param y координата еды по У
     */
    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}