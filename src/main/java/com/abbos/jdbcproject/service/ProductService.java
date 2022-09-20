package uz.abbos.jdbcproject.service;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.tokens.StreamEndToken;
import uz.abbos.jdbcproject.model.Product;
import uz.abbos.jdbcproject.utils.MyConnection;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Component
public class ProductService {
    String URL = "jdbc:postgresql://localhost:5432/java_db";

    String USER = "abbos_j";

    String PASSWORD = "abbos9113";

    Connection connection;

    {
        try {
            Class.forName("org.postgresql.Driver");
            connection = MyConnection.get();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public List<Product> getAll() {
        // bu list DB dan qaytgan javobni o'zida saqlaydi u iteratorga o'xshaydi
        // Statment bu query yaratib olish uchun bir class statment query yaratib olish holati desa ham bo'ladi
        String query = "SELECT * FROM P";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            List<Product> response = new LinkedList<>();
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setPid(resultSet.getString("pid"));
                product.setPname(resultSet.getString("pname").trim());
                product.setSize(resultSet.getInt("size"));
                product.setColor(resultSet.getString("color").trim());
                product.setStore(resultSet.getString("store").trim());

                response.add(product);
            }
            return response;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Product get(Integer id) {
        try {
            String pid = "P" + id;
            String query = "SELECT * FROM P WHERE pid = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,pid);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setPid(resultSet.getString("pid"));
                product.setPname(resultSet.getString("pname").trim());
                product.setSize(resultSet.getInt("size"));
                product.setColor(resultSet.getString("color").trim());
                product.setStore(resultSet.getString("store").trim());
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean create(Product product){
        String query = "INSERT INTO  p (pid,pname,color,size,store)  VALUES  (?,?,?,?,?)";
        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, product.getPid());
            ps.setString(2, product.getPname());
            ps.setString(3, product.getColor());
            ps.setInt(4, product.getSize());
            ps.setString(5, product.getStore());
            boolean result = ps.execute();
            return !result;

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public Boolean update(Integer id,Product product){
        String query = "UPDATE TABLE SET pname = ?,color = ?,size = ?,store = ? WHERE pid = ?";
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1,product.getPname());
            ps.setString(2,product.getColor());
            ps.setInt(3,product.getSize());
            ps.setString(4,product.getStore());
            ps.setString(5,"P" + id);
            boolean result = ps.execute();
            return !result;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public Boolean delete(Integer id){
        String query = "DELETE FROM p WHERE pid = ?";
        try(PreparedStatement ps  = connection.prepareStatement(query)) {
            ps.setString(1,"P" + id);
            return !ps.execute();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;

    }



}

