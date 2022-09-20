package uz.abbos.jdbcproject.dao;
// DAO(Data Access Object) bu bizaga database bilan bog'lab beruvchi lyaer
// bu lyaer bizada Singletone bo'lishi kerak bir martra object olinishi
// kerak bo'ladi bizada qanaqadr service bor usha service databasega
// shu dao orqali murojaat qila oladi har hil Thread dan bittada murojaat
// bo'lsa ham hichnma qmidi har bitta database uchun dao ochib chiqiladi

import lombok.SneakyThrows;
import uz.abbos.jdbcproject.dto.ProductFilterDto;
import uz.abbos.jdbcproject.entity.Product;
import uz.abbos.jdbcproject.utils.MyConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductDao {
    public static final ProductDao instance = new ProductDao();

    public static final String DELETE_SQL= "DELETE FROM p WHERE pid = ?";

    public static final String SAVE_SQL = "INSERT INTO p (pid, pname, color, size, store) VALUES (?,?,?,?,?)";

    public static final String UPDATE_SQL = "UPDATE p SET pname = ?," +
                                                        "color = ?," +
                                                        "size = ?," +
                                                        "store = ?" +
                                                        "where pid = ?";


    // find id ni pasdagi findAll bilan birlashtirib yuboesa bo'ladi bunaqa qilib
//    public static final String FIND_BY_ID_SQL =  FIND_ALL_SQL + "WHERE pid = ?";
    public static final String FIND_BY_ID_SQL = "SELECT * FROM p WHERE pid = ?";


    public static final String FIND_ALL_SQL = "SELECT * FROM p";







    private ProductDao(){
    }

    // endi delete operatsiya qanaqa bo'lishi ko'rib chiqamiz

    public boolean delete(String  id){
        // ESLATMA MyConnection bizada ConnectionPool bo'lib ishlavotti
        // yani har delete chaqirganimzda va pasda shunga o'xshash
        // save update zapros jo'natganimizda get methodi orqali yengitdan
        // emas balki Connection pool mizdgi Connectionni ishlatvommiz ular bizada hozir 5ta
        // va bu connectionni tryresource ga osek u avtoclose bo'ladi
        // shu avtoclose ni ham biza ovveride qilb ishlatvommiz yani closeni o'rniga
        // qaytib pool ga qo'shilvotti
        try (var connection = MyConnection.get();
        var prepareStatment = connection.prepareStatement(DELETE_SQL)) {
            prepareStatment.setString(1,id);

            // bizada executeUpdate int yani nechta o'zgarish bo'lganini
            // qaytaradi shuning uchun 0 dan kottaligia tekshirvommiz
            return prepareStatment.executeUpdate() > 0;
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public Product save(Product product){
        // bizada hozi id autoinkrement emas agar nobodo avto inkrement bo'sa product objectni
        // id sini ham olish imkoni bo'ladi retrun generetkeys orqali quyida misol
        try (var connection = MyConnection.get();
        var prepareStatment = connection.prepareStatement(SAVE_SQL, Statement.NO_GENERATED_KEYS)) {

            prepareStatment.setString(1,product.getPid());
            prepareStatment.setString(2,product.getPname());
            prepareStatment.setString(3,product.getColor());
            prepareStatment.setInt(4,product.getSize());
            prepareStatment.setString(5,product.getStore());


            prepareStatment.executeUpdate();

            var generetedKeys = prepareStatment.getGeneratedKeys();
//            if (generetedKeys.next()){
//                product.setPid(generetedKeys.getString("pid"));
//            }
            return product;
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public void update(Product product){
        try (var connection = MyConnection.get();
        var prepareStatment = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatment.setString(1, product.getPname());
            prepareStatment.setString(2, product.getColor());
            prepareStatment.setInt(3, product.getSize());
            prepareStatment.setString(4, product.getStore());
            prepareStatment.setString(5, product.getPid());

            prepareStatment.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @SneakyThrows
    // bu findbyid methodimiz bizada optional qaytariadi chunki bizga null qiymat ilib olishimiz
    // bu yerda ko'p noqulayliklar keltiradi Optional esa agar u kelayotgan object nullga
    // teng bo'lib qolsa uni yutib yuboradi va pustoy narsa qaytaradi agar getAll qiladigan
    // bo'lsak va va list olishimiz kerak bo'lib qolsa listga hechnma kelmagan taqdirda ham null ni
    // olmaydi va oddiygina pustoy list qaytaradi pastda buni ko'rib chiqamiz
    public Optional<Product> findById(String id){
        try (var connection = MyConnection.get();
        var prepareStatment = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatment.setString(1,id);

            Product product = null;

            var  resultSet = prepareStatment.executeQuery();
            // buni setterlar bilan ham bersa bo'lardi huddi save update laga o'xshab ketadigan
            // lekin buyoda har hil usullarni qo'lashlik uchun shunaqa qilindi
            if (resultSet.next()){
                    product = new Product(
                            resultSet.getString("pid"),
                            resultSet.getString("pname").trim(),
                            resultSet.getString("color").trim(),
                            resultSet.getInt("size"),
                            resultSet.getString("store").trim()
                );

            }
            return Optional.ofNullable(product);
        }

    }

    @SneakyThrows
    public List<Product> findAll(){
        try (var connection = MyConnection.get();
        var prepareStatment = connection.prepareStatement(FIND_ALL_SQL)) {
            var  resultSet = prepareStatment.executeQuery();
            // findALl ham findByid bilan bir hil bo'ladi shuning uchun construcotor
            // da berib yuborayotgan jarayonni alohida bir methodga olib chiqish mumkin edi
            // lekin tushunarli bo'lishi uchun faqat findAll ni o'zini methodga olingan holatnini
            // ko'ramiz
            List<Product> productList = new ArrayList<>();
            while (resultSet.next()){
                productList.add(buildProduct(resultSet));
            }
            return productList;
        }
    }



    // hop findAll methodini yozib oldik endi etilik bizani productlarmiz bor ular judayam
    // ko'p bo'lishi mumkin ularni bittalab id si bo'yicha ko'rdik va umumiy hammasini olishni
    // ko'rdik deylik internet dokonimiz bor va u dokonda yana qanaqadir filter la turadi
    // usha client belgilagan filter la bo'yicha topishniyam endi ko'rib chiqamiz

    @SneakyThrows
    public List<Product> findAll(ProductFilterDto filterDto){
        // bu findAll imzda endi bizada sql so'rovimiz dynamic bo'ladi chunki filtirlarimiz
        // o'ziga qarab har hil bo'ladi
        // bu yerda listsiz ham qilsa bo'lardi lekin zapros lar ko'p bo'lib qolishi mumkin
        // yani where dan keyingi so'rovlar va buyam bitta usul sifatida qilndi
        List<Object> parameters =  new ArrayList<>();
        List<String> whereSql = new ArrayList<>();
        if (filterDto.getPname() != null){
            whereSql.add("pname = ?");
            parameters.add(filterDto.getPname());
        }
        if (filterDto.getColor() != null){
            whereSql.add("color LIKE ?");
            parameters.add("%" + filterDto.getColor() + "%");
        }

        parameters.add(filterDto.getLimit());
        parameters.add(filterDto.getOffset());

        var where = whereSql.stream()
                .collect(Collectors.joining(" AND "," WHERE "," LIMIT ? OFFSET ? "));


        var sql = FIND_ALL_SQL + where;

        try (var connection = MyConnection.get();
        var prepareStatment = connection.prepareStatement(sql)) {


            for (int i = 0; i < parameters.size(); i++) {
                prepareStatment.setObject(i+1,parameters.get(i));
            }
            System.out.println(prepareStatment);

            var resultSet = prepareStatment.executeQuery();

            List<Product> productList = new ArrayList<>();
            while (resultSet.next()){
                productList.add(buildProduct(resultSet));
            }
            return productList;
        }

    }

    @SneakyThrows
    private Product buildProduct(ResultSet resultSet){

            return new Product(
                    resultSet.getString("pid"),
                    resultSet.getString("pname").trim(),
                    resultSet.getString("color").trim(),
                    resultSet.getInt("size"),
                    resultSet.getString("store").trim()
            );
    }








    public static ProductDao getInstance(){
        return instance;
    }



}
