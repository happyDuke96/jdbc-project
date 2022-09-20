package uz.abbos.jdbcproject.utils;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
public class MyConnection {

    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

    private static final String POOL_SIZE_KEY = "db.pool.size";

    private static final Integer DEFUALT_POOL_SIZE = 10;

    // Commection pool uchun Thread lar bilan yaxshi ishlayding Deque ochvolamiz
    private static BlockingQueue<Connection> pool;

    private static List<Connection> sourcConnections;

    static {
        loadDriver();
        // bu yerda connection larni init qilib Deque ga qo'shadigan  method yaratamiz
        initConnectionPool();
    }

    private static void initConnectionPool() {

        // va bu yerda tapadiig DeQueni init qilamiz uni ichiga qiymat berishdan oldin tekshirib olamiz
        // qancha joy olishini agar joy berilmagan bo'lsa o'zimiz yaratgan Defualt qiymatni beramiz
        // aks holda uni parse qilib olamiz

        var poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        var size = poolSize == null ? DEFUALT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        sourcConnections = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            // bu kodda biza pastda get methodida etilganidek ConnectionManager yani usha Connectionimizni
            // close methodini ovveride qilamiz InovcationHandler orqali InvocationHandler u hamma classlardan
            // oldin ishlaydigan invoke methodi bor
            var connection = open();
//            var proxyConnection = Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(), new Class[]{Connection.class}, new InvocationHandler() {
//                @Override
//                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                    return null;
//                }
//            });
            // tepadigi kondni lamda ko'rinishi
            var proxyConnection = (Connection)
                    Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close")
                            ? pool.add((Connection) proxy)
                            : method.invoke(connection,args));
            pool.add(proxyConnection);
            sourcConnections.add(connection);
            // pool mizha connection larni add qilib chiqdik
//            pool.add(open());
        }


    }


    private MyConnection(){}



    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }



    public static Connection get(){
        try {
            // take methodi pool mizdagi hamma connection larni olib chiqib beradi connection lar tuganb qolsa
            // kutib turaveradi
            // endi bu get methodi orqali connection chaqirlganda va u try resourse ga solinadi try resource
            // o'zida close bor connectionlarni close qilib yuboradi usha paytdagi close ni o'rniga biz qaytib
            // pool ga yana add qilib qo'yishimiz kerak bo'ladi shuni ko'rib chiqamiz
            return pool.take();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // bu yerda private qilib qo'yamiz sababi hechkim uni adashib ochib yubormasligi kerak kerakli connection
    // larni get method ochib usha orqali pool mizdan chaqirib olamiz
    private static Connection open(){
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static void closePool(){
        try {
            for (Connection sourcConnection : sourcConnections) {
                sourcConnection.close();
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
