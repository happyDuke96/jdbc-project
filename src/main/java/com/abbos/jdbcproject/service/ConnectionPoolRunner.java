package uz.abbos.jdbcproject.service;

import uz.abbos.jdbcproject.utils.MyConnection;

import java.sql.SQLException;

public class ConnectionPoolRunner {
    // ConnectionPool bu huddi Connectionlarni initsializatsiya qilib cache ga joylab qo'yishimizga o'xshaydi
    // Connection lar bizada jida qimmatli operatsiya hisoblanadi shuning uchun ularni har qandaydir method
    // yoki qaysidir app da ishlatishimiz va har doim yangi connectionlar ochishimiz noto'g'ri hisoblanadi
    // Connection larni odatda Queue larda saqlash mantiqliroq va tog'ri hisoblanadi va ularni soni
    // ko'pi bilan 10 15 tagacha borishi mumkin oldingi darslarda DriverManager ni getConnectionidan
    // to'g'ridan to'g'ri foydalanib kelgandik bu darsda properties file va biroz o'zgartirgan holda
    // ko'rib chiqamiz


    public static void main(String[] args) throws SQLException {
        try {
            checkMetaData();
        }
        finally {
            MyConnection.closePool();
        }

    }

    private static void checkMetaData() throws SQLException {
        try (var connection = MyConnection.get()) {
            // Metadata(Метаданные) bu Database mizdagi deylik hamma narsa haqida malumot misol uchun
            // qaysi nomli repository(хранилище) saqlash joyi qaysi nomli table schemasini nomi va hakazolar

            // connection dan metadata ni ovoldik
            var metaData = connection.getMetaData();
            // endi undan catalog(repository) bu yerda Catalog deb berilgan
            var catalogs = metaData.getCatalogs();

            if (catalogs.next()) {
                System.out.println(catalogs.getString(1));
                var catalog = catalogs.getString(1);
                var schemas = metaData.getSchemas();
                if (schemas.next()) {
                    //TABLE_SCHEM maxsus schemas uchun field markerniy
                    // print information_schema
                    //pg_catalog
                    //public
                    System.out.println(schemas.getString("TABLE_SCHEM"));
                    var schema = schemas.getString("TABLE_SCHEM");


                    //bizaga endi schemasini public bo'yicha kerak
                    var tables = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"});
                    if (schema.equals("public")) {
                        while (tables.next()) {
                            System.out.println(tables.getString("TABLE_NAME"));
                        }
                    }
                }
            }
        }
    }
}
