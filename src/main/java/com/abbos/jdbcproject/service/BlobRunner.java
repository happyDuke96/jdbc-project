package uz.abbos.jdbcproject.service;

import lombok.SneakyThrows;
import uz.abbos.jdbcproject.utils.ConnectionManager;
import uz.abbos.jdbcproject.utils.MyConnection;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class BlobRunner {
    static String url = "jdbc:postgresql://localhost:5432/java_db";

    static String username = "abbos_j";

    static String password = "abbos9113";

    // blob - Binary(Byte) Large Object -- bu method bizaga audio foto videolarni saqlashga yordam beradi
    // lekin postgreSQL dabu method ishlamaydi MySQL va Oracle da bu method ishlaydi quyida Oracle da qanday
    // yozilishini ko'rib chiqamiz PostgreSQL da bu - bytea deb nomlanadi

    /*********************************************************************************************/
    // clob - Character Large Object bu katta hajimdagi character simvol yoki textlarni olishga yordam beradi
    // huddi blob ga o'xshab u ham PostgreSQL da ishlamaydi uniyam Oracle va PostgreSQL dagisni ko'rib chiqamiz
    // PostgreSQL da u TEXT deb nomlanadi

    public static void main(String[] args) {


//        saveImage();
        getImage();


    }

    @SneakyThrows
    private static void saveImage() {


        var sql = "UPDATE p SET IMAGE = ? WHERE pid = " + "'P2'";

        try (var connection = MyConnection.get();
             var preperStatment = connection.prepareStatement(sql)) {

            Path path = Paths.get("/home/abbo9113/Isystem/Spring/JdbcProject/src/main/resources");
            preperStatment.setBytes(1,Files.readAllBytes(Path.of(String.valueOf(path),"jeans.jpg")));
            preperStatment.executeUpdate();
        }
    }





    @SneakyThrows
    private static void getImage(){
        // Endi database mizga joylagan rasmimizni olishni ko;rib chiqamiz
        // ESLATMA DATABASE DA ODATDA RASM VA VIDEO LAR TURMAYDI ULAR QANDAYDIR CLOUD LARDA MISOL UCHUN
        // AWS(Amazon) GOOGLE CLOUD larda turadi Database da faqat usha video yoki rasmlar ga olib boradigan
        // URL adresslari turadi ularni usha URL orqali olib olishimiz mumkin bo'ladi
        // keyingi darsda Connection larni pool ga joylab turishni ko'rib chiqamiz

        String pid = "'P2'";
        var sql = "SELECT image FROM p WHERE pid = "+ pid;
        try (var connection = MyConnection.get();
             var prepareStatment = connection.prepareStatement(sql)) {
            // eslatma select zaproslarda executeQuery yoziladi usha so'rov orqali resultset ni ovolvommiz
            var resultSet = prepareStatment.executeQuery();
            // hozirgi holatda bizga keladigan javob faqat bitta shuning uchun ham while emas if ni o'zini ishlatib qoi'ya qolamiz
            if (resultSet.next()){
                // qaysi columndan olishimizni beramiz image digan column bizada
                var image = resultSet.getBytes("image");
                Path path = Paths.get("/home/abbo9113/Isystem/Spring/JdbcProject/src/main/resources");
                // tepadigi savImage da qayerda olish kerakligini bilish uchun path ochgandik bu yerda teskariss
                // qayerga joylashimizni yani database mizda uje mavjud usha yerdan qayerga joylash kerakligini berib
                // yuboramiz path dan keyin kelayotgan file ni nma deb nomlashimizni beramiz
                // va qaysi columndan olishimizni STANDARTOPENOPTION O'ZIMGA HAM YENGILIK KO'RIB CHIQISH KERAK
                Files.write(Path.of(String.valueOf(path),"jeans_new.jpg"),image, StandardOpenOption.CREATE);



            }



        }
    }

}


//**********************************************************************
// PostgreSQL da ishlamyadigan lekin boshqa SQL larda ishlaydigan usullar
//            @SneakyThrows
//    private static void saveImage(){
//
//        var sql = "UPDATE p SET IMAGE = ? WHERE pid = "+"'P2'";
//
//        try(var connection = DriverManager.getConnection(url, username, password);
//            var preperStatment = connection.prepareStatement(sql)){
            // bu yerda katta hajmda amlumot kevotti shunichun tranzaksiya o'zimizani qo'limzda bo'gani
            // yaxshi shuning uchun autoCommit false qilamiz
//            connection.setAutoCommit(false);
            // va endi ? o'rniga beriladi qiymat va keyingilari shu rasmni o'qib olish va Path ini berib yuborish
//            var blob = connection.createBlob();
            // bu method orqlai outputStrem bilan ishlasa bo;ladi
//            blob.setBinaryStreamr

            // 1 chi usul PostgreSQL da o'xshamasa ham boshqa SQL larda o'xshaydi
//            blob.setBytes(1, Files.readAllBytes(Path.of("resources","jeans.jpg")));
//
//            preperStatment.setBlob(1,blob);
//            preperStatment.executeUpdate();
//            connection.commit();



            // 2 chi usul MySQL Oracle larda ishlaydi PostgreSQL da ishlamaydi
//            BufferedImage image = ImageIO.read(new File("jeand.jpg"));
//
//            var blob1 = connection.createBlob();
//            try(OutputStream outputStream =  blob1.setBinaryStream(1)){
//                ImageIO.write(image,"jpg",outputStream);
//            }
//
//            preperStatment.setBlob(1,blob1);
//            preperStatment.executeUpdate();
//            connection.commit();

//        }
//
//
//    }
//}
