package log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он 
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено 
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений 
 * ограниченного размера)
 * @author Клепинин
 * @version 1.0
 */
public class LogWindowSource
{
    /**
     * переменная обозначающая длину очерди сообщений
     */
    private int m_iQueueLength;
    /**
     * Переменная хранящая все сообщения
     */
    private ArrayList<LogEntry> m_messages;
    /**
     * Переменая хранящая все логи
     */
    private final ArrayList<LogChangeListener> m_listeners;
    /**
     * переменная хранящая все активные логи
     */
    private volatile LogChangeListener[] m_activeListeners;

    /**
     * Конструктор класса, создаюший массив логов и массив изменений
     * @param iQueueLength переданная длина сообщени
     * @see Logger
     */
    public LogWindowSource(int iQueueLength) 
    {
        m_iQueueLength = iQueueLength;
        m_messages = new ArrayList<LogEntry>(iQueueLength);
        m_listeners = new ArrayList<LogChangeListener>();
    }

    /**
     * Метод обновления активных логов
     * @param listener
     */
    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }

    /**
     * Метод добавления сообщения логирования
     * @param logLevel уровень логирования
     * @param strMessage сообщение логирования
     */
    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        m_messages.add(entry);
        LogChangeListener [] activeListeners = m_activeListeners;
        if (activeListeners == null)
        {
            synchronized (m_listeners)
            {
                if (m_activeListeners == null)
                {
                    activeListeners = m_listeners.toArray(new LogChangeListener [0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }

    /**
     * Метод прохода по коллекции сообщений
     * @return сообщение из колекции
     */
    public Iterable<LogEntry> all()
    {
        return m_messages;
    }
}
