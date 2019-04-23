package log;

/**
 * Делает новые записи в журнал логгирования
 * @author Александр Клепинин
 * @version 1.0
 * @see LogWindowSource
 */
public class LogEntry
{
    /**
     * Поле, содержащее уровень логгирование из {@link LogLevel}
     */
    private LogLevel m_logLevel;
    /**
     * Поле, содержащее последнюю запись в журнале
     */
    private String m_strMessage;

    /**
     * Конструктор - устанавливает уровень логгирования и сообщение, которое нужно зафиксировать в журнале
     * @param logLevel уровень логгирования
     * @param strMessage сообщение о работе программы
     */
    public LogEntry(LogLevel logLevel, String strMessage)
    {
        m_strMessage = strMessage;
        m_logLevel = logLevel;
    }

    /**
     * Возвращает текущую запись в журнале
     * @return текущая запись в журнале
     */
    public String getMessage()
    {
        return m_strMessage;
    }

    /**
     * Возвращает текущий уровень логгирования
     * @return уровень логгирования
     */
    public LogLevel getLevel()
    {
        return m_logLevel;
    }
}

