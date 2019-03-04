package com.loya.devi.service.Impl.entityManagment;

import com.loya.devi.service.interfaces.QueryExecutor;
import oracle.jdbc.internal.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Service
public class QueryExecutorImpl implements QueryExecutor {

    @Autowired
    private DataSource dataSource;

    private JdbcOperations jdbcOperations;

    @PostConstruct
    private void init() {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        jdbcOperations = template.getJdbcOperations();
    }

    /**
     * method execute sql request, get result set and will be combine to list of necessary entity.
     *
     * @param sqlQuery is query for oracle db
     * @return parameterized list of entities
     */
    @Override
    public <T> List<T> execute(String sqlQuery, Function<ResultSet, List<T>> function) {
        AtomicReference<List<T>> entities = new AtomicReference<>();
        jdbcOperations.execute(
                con -> {
                    CallableStatement cs = con.prepareCall(sqlQuery);
                    cs.registerOutParameter(1, OracleTypes.CURSOR);
                    return cs;
                },
                (CallableStatementCallback<T>) cs -> {
                    cs.execute();
                    ResultSet result = (ResultSet) cs.getObject(1);
                    entities.set(function.apply(result));
                    return null;
                }
        );
        return entities.get();
    }
}
