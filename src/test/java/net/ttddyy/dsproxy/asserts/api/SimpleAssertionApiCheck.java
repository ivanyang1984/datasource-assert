package net.ttddyy.dsproxy.asserts.api;

import net.ttddyy.dsproxy.asserts.BatchExecutionEntry;
import net.ttddyy.dsproxy.asserts.CallableBatchExecution;
import net.ttddyy.dsproxy.asserts.CallableBatchExecutionEntry;
import net.ttddyy.dsproxy.asserts.CallableExecution;
import net.ttddyy.dsproxy.asserts.PreparedBatchExecution;
import net.ttddyy.dsproxy.asserts.PreparedBatchExecutionEntry;
import net.ttddyy.dsproxy.asserts.PreparedExecution;
import net.ttddyy.dsproxy.asserts.ProxyTestDataSource;
import net.ttddyy.dsproxy.asserts.QueryExecution;
import net.ttddyy.dsproxy.asserts.StatementBatchExecution;
import net.ttddyy.dsproxy.asserts.StatementExecution;

import javax.sql.DataSource;
import java.sql.JDBCType;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * API compilation check with assertEquals.
 *
 * @author Tadaya Tsuyukubo
 */
public class SimpleAssertionApiCheck {

    private DataSource actualDataSource;

    public void dataSource() {
        // tag::datasource[]
        ProxyTestDataSource ds = new ProxyTestDataSource(actualDataSource);

        // ... perform application logic with database ...

        // execution count
        assertEquals(3, ds.getQueryExecutions().size());
        assertEquals(3, ds.getStatements().size());
        assertEquals(3, ds.getBatchStatements().size());
        assertEquals(3, ds.getPrepareds().size());
        assertEquals(3, ds.getBatchPrepareds().size());
        assertEquals(3, ds.getCallables().size());
        assertEquals(3, ds.getBatchCallables().size());
        // end::datasource[]
    }

    public void queryExecution() {
        // tag::query[]
        ProxyTestDataSource ds = new ProxyTestDataSource(actualDataSource);

        // ... perform application logic with database ...

        // each execution
        QueryExecution qe = ds.getQueryExecutions().get(0);
        assertTrue(qe.isSuccess());
        assertTrue(qe.isBatch());
        assertTrue("is statement", qe instanceof StatementExecution);
        assertTrue("is batch statement", qe instanceof StatementBatchExecution);
        assertTrue("is prepared", qe instanceof PreparedExecution);
        assertTrue("is batch prepared", qe instanceof PreparedExecution);
        assertTrue("is callable", qe instanceof CallableExecution);
        assertTrue("is batch callable", qe instanceof CallableBatchExecution);
        // end::query[]
    }

    public void statement() {
        // tag::statement[]
        ProxyTestDataSource ds = new ProxyTestDataSource(actualDataSource);

        // ... perform application logic with database ...

        StatementExecution se = ds.getFirstStatement();

        assertTrue(se.isSuccess());
        assertFalse(se.isSuccess());

        assertEquals("FOO", se.getQuery());
        // end::statement[]
    }

    public void batchStatement() {
        // tag::batch-statement[]
        ProxyTestDataSource ds = new ProxyTestDataSource(actualDataSource);

        // ... perform application logic with database ...

        StatementBatchExecution sbe = ds.getFirstBatchStatement();

        assertTrue(sbe.isSuccess());
        assertFalse(sbe.isSuccess());

        List<String> queries = sbe.getQueries();
        assertEquals(3, queries.size());
        assertEquals("FOO", queries.get(0));
        // end::batch-statement[]
    }

    public void prepared() {
        // tag::prepared[]
        ProxyTestDataSource ds = new ProxyTestDataSource(actualDataSource);

        // ... perform application logic with database ...

        PreparedExecution pe = ds.getFirstPrepared();

        assertTrue(pe.isSuccess());
        assertFalse(pe.isSuccess());

        assertEquals("FOO", pe.getQuery());

        // parameter indexes
        List<Integer> indexes = pe.getParamIndexes();
        assertEquals(2, indexes.size());
        assertEquals(Integer.valueOf(1), indexes.get(0));
        assertEquals(Integer.valueOf(2), indexes.get(1));

        // parameters
        Map<Integer, Object> paramsByIndex = pe.getSetParamsByIndex();
        assertEquals(100, paramsByIndex.get(1));
        assertEquals("FOO", paramsByIndex.get(2));

        // setNull parameters
        Map<Integer, Integer> setNullByIndex = pe.getSetNullParamsByIndex();
        assertEquals(Integer.valueOf(Types.VARCHAR), setNullByIndex.get(1));
        assertTrue(setNullByIndex.containsKey(2));
        // end::prepared[]
    }

    public void batchPrepared() {
        // tag::batch-prepared[]
        ProxyTestDataSource ds = new ProxyTestDataSource(actualDataSource);

        // ... perform application logic with database ...

        PreparedBatchExecution pbe = ds.getFirstBatchPrepared();

        assertTrue(pbe.isSuccess());
        assertFalse(pbe.isSuccess());

        assertEquals("FOO", pbe.getQuery());

        // check batch executions
        List<BatchExecutionEntry> batchEntries = pbe.getBatchExecutionEntries();
        assertEquals(3, batchEntries.size());

        BatchExecutionEntry entry = batchEntries.get(0);
        assertTrue(entry instanceof PreparedBatchExecutionEntry);

        PreparedBatchExecutionEntry preparedBatchEntry = (PreparedBatchExecutionEntry) entry;

        // parameter indexes
        List<Integer> indexes = preparedBatchEntry.getParamIndexes();
        assertEquals(3, indexes.size());
        assertEquals(Integer.valueOf(10), indexes.get(0));

        // parameters
        Map<Integer, Object> paramsByIndex = preparedBatchEntry.getSetParamsByIndex();
        assertEquals(100, paramsByIndex.get(1));
        assertEquals("FOO", paramsByIndex.get(2));

        // setNull parameters
        Map<Integer, Integer> setNullByIndex = preparedBatchEntry.getSetNullParamsByIndex();
        assertEquals(Integer.valueOf(Types.VARCHAR), setNullByIndex.get(1));
        assertTrue(setNullByIndex.containsKey(2));
        // end::batch-prepared[]
    }

    public void callable() {
        // tag::callable[]
        ProxyTestDataSource ds = new ProxyTestDataSource(actualDataSource);

        // ... perform application logic with database ...

        CallableExecution ce = ds.getFirstCallable();

        assertTrue(ce.isSuccess());
        assertFalse(ce.isSuccess());

        assertEquals("FOO", ce.getQuery());

        // parameter names/indexes
        List<Integer> indexes = ce.getParamIndexes();
        assertEquals(2, indexes.size());
        assertEquals(Integer.valueOf(1), indexes.get(0));
        assertEquals(Integer.valueOf(2), indexes.get(1));

        List<String> names = ce.getParamNames();
        assertEquals(2, names.size());
        assertEquals("key1", names.get(0));
        assertEquals("key2", names.get(1));


        // registerOut names/indexes
        List<Integer> outIndexes = ce.getOutParamIndexes();
        assertEquals(1, outIndexes.size());
        assertEquals(Integer.valueOf(1), outIndexes.get(0));

        List<String> outNames = ce.getOutParamNames();
        assertEquals(1, outNames.size());
        assertEquals("key1", outNames.get(0));


        // parameters
        Map<Integer, Object> paramsByIndex = ce.getSetParamsByIndex();
        assertEquals(100, paramsByIndex.get(1));
        assertEquals("FOO", paramsByIndex.get(2));

        Map<String, Object> paramsByName = ce.getSetParamsByName();
        assertEquals(100, paramsByName.get("key1"));
        assertEquals("FOO", paramsByName.get("key2"));


        // setNull parameters
        Map<Integer, Integer> setNullByIndex = ce.getSetNullParamsByIndex();
        assertEquals(Integer.valueOf(Types.VARCHAR), setNullByIndex.get(1));
        assertTrue(setNullByIndex.containsKey(2));

        Map<String, Integer> setNullByName = ce.getSetNullParamsByName();
        assertEquals(Integer.valueOf(Types.VARCHAR), setNullByName.get("key1"));
        assertTrue(setNullByName.containsKey("key2"));


        // registerOut parameters
        Map<Integer, Object> outParamsByIndex = ce.getOutParamsByIndex();
        assertEquals(Types.INTEGER, outParamsByIndex.get(0));

        Map<String, Object> outParamsByName = ce.getOutParamsByName();
        assertEquals(JDBCType.INTEGER, outParamsByName.get("key"));
        // end::callable[]
    }

    public void batchCallable() {
        // tag::batch-callable[]
        ProxyTestDataSource ds = new ProxyTestDataSource(actualDataSource);

        // ... perform application logic with database ...

        CallableBatchExecution cbe = ds.getFirstBatchCallable();

        assertTrue(cbe.isSuccess());
        assertFalse(cbe.isSuccess());

        assertEquals("FOO", cbe.getQuery());


        // check batch executions
        List<BatchExecutionEntry> batchEntries = cbe.getBatchExecutionEntries();
        assertEquals(3, batchEntries.size());

        BatchExecutionEntry entry = batchEntries.get(0);
        assertTrue(entry instanceof CallableBatchExecutionEntry);

        CallableBatchExecutionEntry callableBatchEntry = (CallableBatchExecutionEntry) entry;


        // parameter names/indexes
        List<String> names = callableBatchEntry.getParamNames();
        assertEquals(3, names.size());
        assertEquals("foo", names.get(0));

        List<Integer> indexes = callableBatchEntry.getParamIndexes();
        assertEquals(3, indexes.size());
        assertEquals(Integer.valueOf(10), indexes.get(0));


        // registerOut names/indexes
        List<Integer> outIndexes = callableBatchEntry.getOutParamIndexes();
        assertEquals(1, outIndexes.size());
        assertEquals(Integer.valueOf(1), outIndexes.get(0));

        List<String> outNames = callableBatchEntry.getOutParamNames();
        assertEquals(1, outNames.size());
        assertEquals("key1", outNames.get(0));


        // parameters
        Map<String, Object> paramsByName = callableBatchEntry.getSetParamsByName();
        assertEquals(100, paramsByName.get("key1"));
        assertEquals("FOO", paramsByName.get("key2"));

        Map<Integer, Object> paramsByIndex = callableBatchEntry.getSetParamsByIndex();
        assertEquals(100, paramsByIndex.get(1));
        assertEquals("FOO", paramsByIndex.get(2));


        // setNull parameters
        Map<String, Integer> setNullByName = callableBatchEntry.getSetNullParamsByName();
        assertEquals(Integer.valueOf(Types.VARCHAR), setNullByName.get("key1"));
        assertTrue(setNullByName.containsKey("key2"));

        Map<Integer, Integer> setNullByIndex = callableBatchEntry.getSetNullParamsByIndex();
        assertEquals(Integer.valueOf(Types.VARCHAR), setNullByIndex.get(1));
        assertTrue(setNullByIndex.containsKey(2));


        // registerOut parameters
        Map<String, Object> outParamByName = callableBatchEntry.getOutParamsByName();
        assertEquals(Types.VARCHAR, outParamByName.get("key1"));
        assertEquals(JDBCType.INTEGER, outParamByName.get("key2"));

        Map<Integer, Object> outParamByIndex = callableBatchEntry.getOutParamsByIndex();
        assertEquals(Types.VARCHAR, outParamByIndex.get(1));
        assertEquals(JDBCType.INTEGER, outParamByIndex.get(2));
        // end::batch-callable[]
    }
}
