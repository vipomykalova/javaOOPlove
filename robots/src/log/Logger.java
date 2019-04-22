package log;

/**
 * Неизменяемый класс
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
     * @param strMessage
     */
    public static void debug(String strMessage)
    {
        defaultLogSource.append(LogLevel.Debug, strMessage);
    }

    /**
     * Статический метод возвращающий
     * @return
     */
    public static LogWindowSource getDefaultLogSource()
    {
        return defaultLogSource;
    }
}
