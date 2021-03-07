package study.java1.week07.readwrite.v1.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import study.java1.week07.readwrite.v1.datasource.DataSourceHolder;

public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.get();
    }
}
