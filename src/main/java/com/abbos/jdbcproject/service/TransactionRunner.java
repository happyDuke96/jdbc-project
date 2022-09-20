package uz.abbos.jdbcproject.service;

import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import uz.abbos.jdbcproject.utils.MyConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class TransactionRunner {

    static String url = "jdbc:postgresql://localhost:5432/java_db";

    // bu yerda acces olish uchun dtabase ga username va parol
    static String username = "abbos_j";

    static String password = "abbos9113";

    @SneakyThrows
    public static void main(String[] args) {
        // Bilamizki Tranzaksiya yoki butunlay bajarilishi kerak yoki umuman bajarilmasligi kerak
        //
        Integer id = 4;
        var deleteTemlateSql = "DELETE FROM templates where id = ?";
        var deleteSecondSql = "DELETE FROM secondtemp where id = ?";

        Connection connection = null;
        PreparedStatement deleteTemplateStatment = null;
        PreparedStatement deleteSecondStatment = null;
        try {
            connection = MyConnection.get();
            deleteTemplateStatment = connection.prepareStatement(deleteTemlateSql);
            deleteSecondStatment = connection.prepareStatement(deleteSecondSql);
            connection.setAutoCommit(false);

            // bu yerda hatolik yuz beradi chunki deleteSecondSql bizda foreign key
            // va birinchi bo'lib TemlateStatment ochadi keyin secondStatment o'chirmoxchi bo'ladi va u
            // templateStatment dgan table topa olmaydi tranzaksiya chala bajariladi yo uyoli yo buyoli bo'lishi kere
            // shu holatni chetlab o'tish uchun quyidagi usul ko'rib chiqamz
            deleteTemplateStatment.setInt(1, id);
            deleteSecondStatment.setInt(1, id);


            deleteTemplateStatment.executeUpdate();
            if (true) {
                throw new RuntimeException("Ooops");
            }

            deleteSecondStatment.executeUpdate();
            // bu yerda commit  qilib ko'rmoxchi bo'lamiz bu yerda hammasi yaxshi bo'sagina commit bo'ladi
            // aks holda commit bo'lmaydi aks holdagi istisno holini pasdroqda catchda tutib olishimiz kerak bo'ladi
            connection.commit();
        } catch (Exception e) {
            // aks holdagi holat yani ikkalasidan bittasi hechyo'q bajarilmasa shu yerga keladi
            // va bu sql statment(sql so'rov) bajarilmagan holatdagi connection ni rollback otmena qilib yuborlmaymiz
            // uni null ga teng teng emasligini tekshirishimiz kerak bo'ladi
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        }
        finally{

            if (connection != null) {
                connection.close();
            }
            if (deleteSecondStatment != null){
                deleteSecondStatment.close();
            }
            if (deleteTemplateStatment != null){
                deleteTemplateStatment.close();
            }
        }
    }

}