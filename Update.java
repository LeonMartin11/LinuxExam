import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/update")
public class Update extends HttpServlet {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String URL =
            "jdbc:mysql://106.12.175.36/linux_final?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8";
    static final String USER = "root";
    static final String PASS = "Ahchc0419#";
    static final String SQL_UPDATE_STUDENT = "UPDATE t_student SET  `name` = ?, age = ?,sex = ?  where id = ? ;";

    static Connection conn = null;
    static Jedis jedis = null;

    // servlet创建时 初始化的东西
    public void init() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASS);
            jedis = new Jedis("180.76.142.74");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 当再次调用servlet销毁之前
    public void destroy() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 重写doGet方法
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // response 返回值类型
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // response 返回
        PrintWriter out = response.getWriter();

        // 获取请求体中内容
        String str = IOUtils.toString(request.getInputStream(), "UTF-8");
        // 将请求体中的内容解析成实体对象
        Gson gson = new Gson();
        Student student = gson.fromJson(str, Student.class);

        // 调用方法
        Boolean delete = update(student);

        // 返回结果
        out.println(delete);

        out.flush();
        out.close();

    }

    // 查询 返回一个泛型为实体类的List集合
    private Boolean update(Student student) {
        Boolean flag = null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(SQL_UPDATE_STUDENT);
            // 填充占位符
            // ps.setString(索引,参数);
            ps.setString(1,student.getName());
            ps.setString(2,student.getAge());
            ps.setString(3,student.getSex());
	    ps.setString(4,student.getId());
            // 执行sql 返回影响数据库的行数
            int i = ps.executeUpdate();

            if (i > 0) { // 操作成功
                flag = true;
                jedis.del("studentlist");
            } else { // 操作失败
                flag = false;
            }

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return flag;
    }

    class Student {
        private String id;
        private String name;
        private String age;
        private String sex;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }



    }
}
