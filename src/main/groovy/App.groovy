import com.opencsv.CSVReader

class App {
    // results.testOperation.csv -> results.testOperation.sql
    static void main(String[] args) {
        args[0].split(',').each {
            h2CsvToSql(it)
        }
    }

    static void h2CsvToSql(String csvFileName) {
        if (!csvFileName) {
            return
        }
        assert csvFileName.endsWith(".csv"): "$csvFileName doesn't end with .csv!"

        // results.testOperation.csv -> testOperation
        String tableName = csvFileName.split("\\.")[-2]

        CSVReader reader = new CSVReader(new BufferedReader(new FileReader(csvFileName), 1024 * 1024))
        BufferedWriter sqlOutput = new BufferedWriter(new FileWriter(csvFileName.replace('csv', 'sql')), 1024 * 1024)

        String[] titles = reader.readNext()

        String[] cols

        int counter = 0
        while (cols = reader.readNext()) {
            if (cols.size() != 0) {
                String sql = buildSql(tableName, titles, cols)
                sqlOutput.append(sql)
            }

            if ((counter++) % 1024 == 0) {
                sqlOutput.flush()
            }
        }
        sqlOutput.flush()
    }

    static String buildSql(String tableName, String[] titles, String[] columns) {
        StringBuilder sb = new StringBuilder("INSERT INTO $tableName (")
        sb.append(titles.join(",")).append(") VALUES (")
        sb.append(columns.collect { "'$it'" }.join(",")).append(");\n")
        return sb.toString()
    }
}
