package hibernate;

import org.hibernate.Session;
import gui.GameVisualizer;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.HashMap;
import java.util.List;

public class DatabaseManager {

    public Long addGameState(GameVisualizer currentGame) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Long id = (Long) session.save(currentGame);
        session.getTransaction().commit();
        return id;
    }

    public GameVisualizer getGameState(Long gameStateId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        GameVisualizer result = session.get(GameVisualizer.class, gameStateId);
        session.getTransaction().commit();
        return result;
    }

    public void loadStatesFromDatabase(HashMap<String, Long> gameStates) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        NativeQuery queries = session.createSQLQuery("select name_game, id from game_states")
                .addScalar("name_game", new StringType())
                .addScalar("id", new LongType());
        List<Object[]> rows = queries.list();
        for (Object[] row : rows) {
            gameStates.put(row[0].toString(), (Long) row[1]);
        }
        session.getTransaction().commit();

    }

}
