package pl.hycom.mokka.util.query;

import org.springframework.stereotype.Service;
import pl.hycom.mokka.emulator.mock.model.MockConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tomasz Wozniak (tomasz.wozniak@hycom.pl)
 */
@Service
@Transactional
public class MockSearch {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String DESCRIPTION = "description";
    public static final String PATTERN = "pattern";
    public static final String ENABLED = "enabled";
    private static final String PERCENT = "%";
    private static final String WHERE = "where ";

    @PersistenceContext
    private EntityManager em;

    private Integer startingIndex;
    private Integer maxResults;
    private Map<String, String> parameterMap = new HashMap<>();

    public List<MockConfiguration> find(){
        Query query;
        StringBuilder base = new StringBuilder();

        if(parameterMap.size() == 0){
            base.append("select m from MockConfig m ");
        }
        else{
            base.append("select m from MockConfig m where ");
            appendParametersToBaseQuery(base);
        }

        base.append("order by m ASC");

        query = em.createQuery(base.toString(), MockConfiguration.class);
        setQueryParameters(query);

        if (getStartingIndex() != null) {
            query.setFirstResult(getStartingIndex());
        }

        if (getMaxResults() != null) {
            query.setMaxResults(getMaxResults()+1);
        }
        return query.getResultList();
    }

    public Long countAllPossibleResults() {
        Query query;
        StringBuilder base = new StringBuilder();

        if(parameterMap.size() == 0) {
            base.append("select count(m) from MockConfig m");
        } else {
            base.append("select count(m) from MockConfig m where ");
            appendParametersToBaseQuery(base);
        }

        query = em.createQuery(base.toString(), Long.class);
        setQueryParameters(query);
        return (Long) query.getSingleResult();
    }

    private void appendParametersToBaseQuery(StringBuilder base) {

        if(parameterMap.containsKey(ID)) {
            base.append("m.id = :id ");
        }

        if(parameterMap.containsKey(NAME)) {
            appendCondition(base, "lower(m.name) like lower(:name) ");
        }

        if(parameterMap.containsKey(PATH)) {
            appendCondition(base, "lower(m.path) like lower(:path) ");
        }

        if(parameterMap.containsKey(PATTERN)){
            appendCondition(base, "lower(m.pattern) like lower(:pattern) ");
        }

        if(parameterMap.containsKey(DESCRIPTION)){
            appendCondition(base, "lower(m.description) like lower(:description) ");
        }

        if(parameterMap.containsKey(ENABLED)){
            appendCondition(base, "m.enabled =");

            if("true".equalsIgnoreCase(parameterMap.get(ENABLED))){
                base.append("1 ");
            } else {
                base.append("0 ");
            }
        }
    }

    private void appendCondition(StringBuilder base, String conditionString) {
        if(!base.toString().endsWith(WHERE)) {
            base.append("and ");
        }
        base.append(conditionString);
    }

    private void setQueryParameters(Query query) {

        if(parameterMap.containsKey(ID)) {
            query.setParameter("id", Long.valueOf(parameterMap.get(ID)));
        }

        if(parameterMap.containsKey(NAME)) {
            query.setParameter("name", PERCENT + parameterMap.get(NAME) + PERCENT);
        }

        if(parameterMap.containsKey(PATH)) {
            query.setParameter("path", PERCENT + parameterMap.get(PATH) + PERCENT);
        }

        if(parameterMap.containsKey(DESCRIPTION)){
            query.setParameter("description", PERCENT + parameterMap.get(DESCRIPTION) + PERCENT);
        }

        if(parameterMap.containsKey(PATTERN)){
            query.setParameter("pattern", PERCENT + parameterMap.get(PATTERN) + PERCENT);
        }
    }

    public void clearParameterMap() {
        parameterMap.clear();
    }

    public void add(String parameter, String value){
        parameterMap.put(parameter,value);
    }

    public Integer getStartingIndex() {
        return startingIndex;
    }

    public void setStartingIndex(Integer startingIndex) {
        this.startingIndex = startingIndex;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }
}

