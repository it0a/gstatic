@Grab('mysql:mysql-connector-java:5.1.25')
@Grab('log4j:log4j:1.2.17')
@GrabConfig(systemClassLoader = true)
import groovy.transform.CompileStatic
import groovy.sql.Sql
import org.apache.log4j.*
import groovy.util.logging.*

@Log4j
class GStatic {

    def perfExec = { name, c ->
        def startTime = System.currentTimeMillis()
        c()
        def endTime = System.currentTimeMillis() - startTime
        println "${name} finished in ${endTime}ms"
    }

    Sql sql = Sql.newInstance("jdbc:mysql://localhost:3306/test_data", "root", "sriq@", "com.mysql.jdbc.Driver")

    static main(args) {
        new GStatic().run()
    }

    void clearTables() {
        sql.withTransaction {[
            "SET FOREIGN_KEY_CHECKS = 0",
            "TRUNCATE TABLE contract",
            "TRUNCATE TABLE invoice",
            "TRUNCATE TABLE line_item",
            "SET FOREIGN_KEY_CHECKS = 1"
        ].each{sql.execute(it)}}
    }

    def generateData() {
        sql.execute("""
            INSERT INTO contract (date_start)
            VALUES (?)
        """, [new Date()])
    }

    def run() {
        perfExec("clearTables", {-> clearTables()})
        perfExec("generateData", {-> generateData()})
    }

}
