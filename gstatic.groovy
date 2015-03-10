@Grab('mysql:mysql-connector-java:5.1.25')
@GrabConfig(systemClassLoader = true)
import groovy.transform.CompileStatic
import groovy.sql.Sql

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

    @CompileStatic
    List<Date> generateDates(int n) {
        (1..n).collect{new Date() + n}
    }

    @CompileStatic
    void generateContractData(Sql sql, Closure c) {
        sql.withBatch("""
            INSERT INTO contract (date_start)
            VALUES (?)
        """, c)
    }

    @CompileStatic
    String buildInvoiceQuery(Sql sql) {
        (0..((12*3)-1))
        .collect{ "UNION ALL SELECT id AS id, DATE_ADD(date_start, INTERVAL ${it} MONTH) AS date_invoiced FROM contract"}
        .join(" ")
        .minus("UNION ALL ")
    }

    @CompileStatic
    String buildLineItemQuery() {
        ((1..19)
        .collect{"UNION ALL SELECT id AS id, ROUND(RAND() * 1000, 2) AS amount FROM invoice"}
        .join(' ')
        .minus("UNION ALL"))
    }


    @CompileStatic
    void generateInvoiceData(Sql sql, Closure c) {
        sql.withBatch("""
            INSERT INTO invoice (contract_id, date_invoiced, number)
            VALUES (?, ?, ?)
        """, c)
    }

    @CompileStatic
    void generateLineItemData(Sql sql, Closure c) {
        sql.withBatch("""
            INSERT INTO line_item (invoice_id, amount)
            VALUES (?, ?)
        """, c)
    }

    def generateData(data) {
        sql.withTransaction {
            perfExec("generateContractData", {->
                generateContractData(sql, { stmt -> generateDates(30).each{ d -> stmt.addBatch([d])}})
            })
            perfExec("generateInvoiceData", {->
                generateInvoiceData(sql, { stmt ->
                    sql.rows(buildInvoiceQuery()).eachWithIndex{invoice, i -> stmt.addBatch([invoice.id, invoice.date_invoiced, i + 1])}
                })
            })
            perfExec("generateLineItemData", {->
                generateLineItemData(sql, { stmt -> sql.rows(buildLineItemQuery()).each{stmt.addBatch([it.id, it.amount])} })
            })
        }
    }

    def run() {
        perfExec("clearTables", {->
            clearTables()
        })
        perfExec("generateData", {->
            generateData()
        })
    }

}
