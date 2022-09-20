package uz.abbos.jdbcproject.service;

import lombok.SneakyThrows;
import org.postgresql.Driver;
import org.springframework.stereotype.Component;
import uz.abbos.jdbcproject.utils.ConnectionManager;
import uz.abbos.jdbcproject.utils.MyConnection;

import java.net.URI;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SupplierService {
    Class<Driver> driverClass = Driver.class;


    // bu yerda url berib yuboramiz Connection berilgan url bo'yicha qaysi SQL ga ulanishni qidiradi
    // agar topolmasa yana bir bora tekshirib ko'radi ungacha Exception tashalamay turadi keyin
    // topolmasa Exception tashlaydi
    static String url = "jdbc:postgresql://localhost:5432/java_db";

    // bu yerda acces olish uchun dtabase ga username va parol
    static String username = "abbos_j";

    static String password = "abbos9113";


    Connection connection;


    String sql2 = "INSERT INTO game(data)VALUES ('Test1'),('Test2'),('Test3'),('Test4')";
    String sql = "CREATE TABLE IF NOT EXISTS game (" +
            "id SERIAL PRIMARY KEY , data TEXT NOT NULL )";

    String sql3 = "SELECT * FROM persons";


    // bu service class imizda har hil query zaproslar bilan ishlaymiz va tozoroq ko'rinishi
    // uchun Connectionmizni boshqa classga ovolsak ham bo'ladi

    {
        try {
            connection = MyConnection.get();
            var statemant = connection.createStatement();
            // bu yerda tranzaksiya statusini ko'rishimiz mumkin bo'ladi defualt holda 2
            // tranzaksiya Lessoniga qaralsin
            System.out.println(connection.getTransactionIsolation());

            // execute faqat select query bajarilganda true qaytaradi boshqa holatlarda faqat false qaytaradi
            // execute ni hamma joyda ishlatsa ham bo'ladi lekin u odatda select da va DDL operatsiyalarda
            // yani create drop alter da DML operaysiyalar uchun esa misol update insert delete uchun executeUdate
            // select uchun esa executeQuery ishlatiladi
//            var executeResult  = statemant.execute(sql);

            // yuqorida etilganidel executeUpdte insert ga ishlatvommiz Postgres qoidalariga ko'ra
            // insert delete update dan keyin nechta o'zgarish bo'lgani ko'rsatilishi kerak va exResult2
            // bizada int qaytaradi agar execute o'zini ishlatganimzda biza qo'shimcha yana quyidagini yozishmz kerak bo'lar edi


//           resultSet(boolean) System.out.println(statemant.getUpdateCount());
            var executeResult2 = statemant.executeUpdate(sql2);


            // bizga 4 soni keldi 4 ta qoshdik va ozgarishlar soni ham 4ta
//            System.out.println(executeResult2);

            // select querysi uchun endi bu yerda executeQuery ishlatamiz bu getzaprosga o'xshash
            // va endi executeResultimiz ham bizga Resultset qaytaradi u iteratorga o'xshaydi

            var executeResult3 = statemant.executeQuery(sql3);


//            while (executeResult3.next()){
//                System.out.println(executeResult3.getInt("id"));
//                System.out.println(executeResult3.getString("name"));
//                System.out.println(executeResult3.getString("surname"));
//                System.out.println(executeResult3.getString("age"));
//                System.out.println("------------");
//
//            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    // tepadigalrni yaxshilab ko'rib tushunib chiq

    // ko'rib bo'lgan bo'sen enid SQL injectionni ko'rib chiqamiz


    public static void main(String[] args) {











        // Statement uchun
        // mana SQL Injection yani usha gap
//        String id = "2";
//
//
//        var result = getByID(Integer.valueOf(id));
//
//        System.out.println(result);
//
        // preperStatment uchun
//        var result = getByBetweenDate(LocalDateTime.of(2022,7,2,0,0),
//                LocalDateTime.of(2022,8,10,0,0));
//        // javob 7 ta chiqdi10.-
//        System.out.println(result);

        checkMetaDate();
    }

    @SneakyThrows
    private static List<String> getByID(Integer id) {

        String sql = "SELECT * FROM persons WHERE id = @id";

        List<String> result = new ArrayList<>();

        // connection qivoldik va zapros berish uchun statment yartdik
        try (var connection = MyConnection.get();
             var statment = connection.createStatement()) {

            // select uchun executeQuery ishlatamiz
            var resultSet = statment.executeQuery(sql);
            while (resultSet.next()) {
                result.add(resultSet.getString("name"));
                // misol uchun id si bo'yicha ismini emas qandayydir long id int id ni olmoxchi
                // bo'lsak resultSet.getInt() primitiv tip qaytaradi agar id kopincha null bo'lmaydi
                // to'g'ri lekin foregn key null bop qosa hatolik tashidi chunki primitiv ozi bilan qanaqadr
                // jovob olib ketishi shart shunda  resultSet.Object() ishlatamz
//                System.out.println(resultSet.getObject("foregin_id",Integer.class));
            }

        }
        return result;
    }



    @SneakyThrows
    private static List<Integer> getByBetweenDate(LocalDateTime start, LocalDateTime end){

        String sql = "SELECT id FROM my_table WHERE visit_date between  ? and ?";

        List<Integer> result = new ArrayList<>();

        try (var connection = MyConnection.get();
             // hop bu yerda preperStatment da endi statment yaratib olib keyin query bermaymiz aksincha
             // statment yaratayotkanimzda(zapros) queryni ham shu joyida berib yuobramiz
             // preperstatmentni kop setter lari bor statmentda esa getter lar kop
             // yana bir yengi narsa bu o'z ichiga sql query olib kirb ketadigan execute method lar
             // preperStatmentda ishlamaydi eslatib o'taman PreperStatment Statmentdan vorislik olgan
             // usha vorislikdagi biroz o'zgarish o'ta olmaganligi sababdan ishlamaydi
             var preperStatment = connection.prepareStatement(sql)) {
            // Lesson_info ga qaralsin
            // yuborilgan so'rov nechchi qatordan o'qilishi kelishi kerakligi
            preperStatment.setFetchSize(10);
            // yuborilgan so'rov uzog'i necha sekund kutib tura olishi
            preperStatment.setQueryTimeout(10);
            // yuborilgan so'rov maximum nechta qatogacha olishi yani bu yerda 100 ta qatordan keyin boshqa olmaydi
            preperStatment.setMaxRows(100);
            // Javdagi DateTime shundoq berib yuborolaymiz PostgreSQL uni tushunmaydi chunki uni Date bilan ishlashi
            // eski Javada esa yengi buni oldini olish uchun ? lar o'rniga parametr berishmiz kerak va bu uchun
            // TimeStamp.valuof parse qiladgan method bor shuni ishaltamiz
            System.out.println(preperStatment);
            preperStatment.setTimestamp(1, Timestamp.valueOf(start));
            // va har bir zaprosda qanday o'zgarishlar yuz berishni ko'rish uchun print qilib chiqamz
            System.out.println(preperStatment);
            preperStatment.setTimestamp(2,Timestamp.valueOf(end));
            System.out.println(preperStatment);

            // va endi executeQuery orqali resultSetga ovolamiz chunki select da odatda executeQuery
            var resultSet = preperStatment.executeQuery();

            while (resultSet.next()){
                // java_db chiqdi
                result.add(resultSet.getInt("id"));

            }

        }
        return result;
    }




    @SneakyThrows
    private static void checkMetaDate(){
        try (var connection = MyConnection.get()){
            // Metadata(Метаданные) bu Database mizdagi deylik hamma narsa haqida malumot misol uchun
            // qaysi nomli repository(хранилище) saqlash joyi qaysi nomli table schemasini nomi va hakazolar

            // connection dan metadata ni ovoldik
            var metaData = connection.getMetaData();
            // endi undan catalog(repository) bu yerda Catalog deb berilgan
            var catalogs = metaData.getCatalogs();

            while (catalogs.next()){
                System.out.println(catalogs.getString(1));
                var catalog = catalogs.getString(1);
                var schemas = metaData.getSchemas();
                while (schemas.next()){
                    //TABLE_SCHEM maxsus schemas uchun field markerniy
                    // print information_schema
                    //pg_catalog
                    //public
                    System.out.println(schemas.getString("TABLE_SCHEM"));
                    var schema = schemas.getString("TABLE_SCHEM");


                    //bizaga endi schemasini public bo'yicha kerak
                    var tables = metaData.getTables(catalog,schema,"%",new String[]{"TABLE"});
                    if (schema.equals("public")){
                        while (tables.next()){
                            System.out.println(tables.getString("TABLE_NAME"));
                        }
                    }
                }
            }
        }
    }

}
