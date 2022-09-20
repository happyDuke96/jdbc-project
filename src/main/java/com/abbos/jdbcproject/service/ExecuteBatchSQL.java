package uz.abbos.jdbcproject.service;

import lombok.SneakyThrows;
import uz.abbos.jdbcproject.utils.MyConnection;

import java.sql.*;

public class ExecuteBatchSQL {
    static String url = "jdbc:postgresql://localhost:5432/java_db";

    static String username = "abbos_j";

    static String password = "abbos9113";

    public static void main(String[] args) throws SQLException {

        String sid = "'S7'";
        String pid = "'P7'";
        String deleteStable = "DELETE FROM s WHERE sid = " +sid;
        String deletPtable = "DELETE FROM p WHERE pid = " + pid;

        // Misol uchun bizani Java bitta Server va Database miz boshqa Server Java Serverimiz Toshkentda
        // Database Serverimiz AQSH da joylashgan bo'lishi mumkin va internet yaxshi ishlagani bilan bunga vaqt ketadi
        // deylik birnechta so'rov jo'natmoqchimiz bilamizki har bir yangi request da yangi Tranzaksiya ochiladi
        // va kotta nagruzka tushishi boshlidi bu holatdan qutilish uchun Batch request(so'rov) dan foydalanamiz
        // batch bizga hamam so'rov larimizni yeg'ib turib keyin database ga jo'natib beradi kodda ko'rib chiqamiz
        Connection connection = null;
        Statement statement = null;
        try {
            connection = MyConnection.get();
            // biz endi bu holatda PreperStatmentdan foydalana olmaymiz bu holatda Statmentdan foydalanamiz
            // chunki PreperStatmentdan o'ziga srazi SQL so'rov so'raydi bu holatda biz bundan oldin bir ikkita narsa
            //  larni to'g'irlab olishimiz kerak bo'ladi
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            // ahamiyatli joyi bu yerda ketma ketlik bo;lishi kerak agar foreign key holatida bo'lsa
            statement.addBatch(deleteStable);
            statement.addBatch(deletPtable);


            var ints = statement.executeBatch();
            connection.commit();

        } catch (Exception e) {

            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {

            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }

        }
    }
}
