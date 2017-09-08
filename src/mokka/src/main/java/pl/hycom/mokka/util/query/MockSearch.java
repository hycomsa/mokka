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

        public static final String PATH = "path";
        public static final String DESCRIPTION = "description";
        public static final String PATTERN = "pattern";
        public static final String ENABLED = "enabled";
        public static final String PERCENT = "%";
        public static final String WHERE = "where ";

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
            }

            if(parameterMap.containsKey(PATH)) {
                base.append("lower(m.path) like lower(?1) ");
            }

            if(parameterMap.containsKey(PATTERN)){
                if(base.toString().endsWith(WHERE)) {
                    base.append("lower(m.pattern) like lower(?3) ");
                }
                else {
                    base.append("and lower(m.pattern) like lower(?3) ");
                }
            }

            if(parameterMap.containsKey(DESCRIPTION)){
                if(base.toString().endsWith(WHERE)){
                    base.append("lower(m.description) like lower(?2) ");
                }
                else {
                    base.append("and lower(m.description) like lower(?2) ");
                }
            }

            if(parameterMap.containsKey(ENABLED)){
                if(base.toString().endsWith(WHERE)){
                    base.append("m.enabled =");
                }
                else {
                    base.append("and m.enabled =");
                }

                if("true".equalsIgnoreCase(parameterMap.get(ENABLED))){
                    base.append("1 ");
                }
                else {
                    base.append("0 ");
                }
            }

            base.append("order by m ASC");

            query = em.createQuery(base.toString(), MockConfiguration.class);

            if(parameterMap.containsKey(PATH)) {
                query.setParameter(1, PERCENT + parameterMap.get(PATH) + PERCENT);
            }

            if(parameterMap.containsKey(DESCRIPTION)){
                query.setParameter(2, PERCENT + parameterMap.get(DESCRIPTION) + PERCENT);
            }

            if(parameterMap.containsKey(PATTERN)){
                query.setParameter(3, PERCENT + parameterMap.get(PATTERN) + PERCENT);
            }

            if (getStartingIndex() != null) {
                query.setFirstResult(getStartingIndex());
            }

            if (getMaxResults() != null) {
                query.setMaxResults(getMaxResults()+1);
            }

            parameterMap.clear();
            return query.getResultList();
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

