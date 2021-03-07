package study.java1.week07.readwrite.v1.datasource;

import com.zaxxer.hikari.HikariConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadWriteDataSourceConfig {
    private HikariConfig master;
    private Map<String, HikariConfig> slaves;
    private List<String> slaveNames;

    public HikariConfig getMaster() {
        return master;
    }

    public void setMaster(HikariConfig master) {
        this.master = master;
    }

    public Map<String, HikariConfig> getSlaves() {
        return slaves;
    }

    public void setSlaves(Map<String, HikariConfig> slaves) {
        this.slaves = slaves;
        this.slaveNames = new ArrayList<>(slaves.keySet());
    }

    public List<String> getSlaveNames(){
        return this.slaveNames;
    }
}
