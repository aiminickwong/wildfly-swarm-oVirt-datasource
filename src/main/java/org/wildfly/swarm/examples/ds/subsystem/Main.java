package org.wildfly.swarm.examples.ds.subsystem;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

public class Main {

    static String driverModule;

    public static void main(String[] args) throws Exception {

        Swarm swarm = new Swarm();

        String useDB = System.getProperty("swarm.use.db", "postgresql");

        // Configure the Datasources subsystem with a driver
        // and a datasource
        switch (useDB.toLowerCase()) {
            case "postgresql" :
                swarm.fraction(datasourceWithPostgresql());
                driverModule = "org.postgresql";
                break;
           default:
                swarm.fraction(datasourceWithPostgresql());
                driverModule = "org.postgresql";
        }

        // Start the swarm
        swarm.start();
        JAXRSArchive appDeployment = ShrinkWrap.create(JAXRSArchive.class);
        appDeployment.addResource(MyResource.class);
        appDeployment.addModule(driverModule);

        // Deploy your app
        swarm.deploy(appDeployment);

    }

    private static DatasourcesFraction datasourceWithPostgresql() {
        return new DatasourcesFraction()
                .jdbcDriver("org.postgresql", (d) -> {
                    d.driverClassName("org.postgresql.Driver");
                    d.xaDatasourceClass("org.postgresql.xa.PGXADataSource");
                    d.driverModuleName("org.postgresql");
                })
                .dataSource("engine", (ds) -> {
                    ds.driverName("org.postgresql");
                    ds.connectionUrl("jdbc:postgresql://localhost:5432/engine");
                    ds.userName("engine");
                    ds.password("redhat");
                });
    }
}
