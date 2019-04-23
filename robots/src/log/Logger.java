package log;

/**
 * Неизменяемый класс, предоставляющий объекты для работы с логгированием
 * @author Александр Клепинин
 * @version 1.0
 */
public final class Logger
{
    /**
     * Неизменное статическое поле - окошко логирования
     */
    private static final LogWindowSource defaultLogSource;

    /**
     * Статический конструктор, создаюший окошко с максимальной длиной лог-сообщений
     */
    static {
        defaultLogSource = new LogWindowSource(100);
    }

    /**
     * Частный конструктор класса
     */
    private Logger()
    {
    }

    /**
     * Статический метод сохраняющий сообщения логирования
     * @param strMessage сообщение в лог
     */
    public static void debug(String strMessage)
    {
        defaultLogSource.append(LogLevel.Debug, strMessage);
    }

    /**
     * Статический метод возвращающий окошко лог-сообщений
     * @return окно лог-сообщений
     */
    public static LogWindowSource getDefaultLogSource()
    {
        return defaultLogSource;
    }
}