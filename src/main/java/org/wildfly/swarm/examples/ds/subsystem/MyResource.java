package org.wildfly.swarm.examples.ds.subsystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class MyResource {

    @GET
    @Produces("text/plain")
    public String get() throws NamingException, SQLException {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("jboss/datasources/engine");
        Connection conn = ds.getConnection();
        try {
            String query = "SELECT * FROM vms";
            List<String> vmNames = new ArrayList<>();
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    vmNames.add(rs.getString("vm_name"));
                }
                return "Using connection: " + conn + "\n The VM names are: " + vmNames.toString();
            } catch (SQLException e) {
            }
        } finally {
            conn.close();
        }
        return null;
    }
}
