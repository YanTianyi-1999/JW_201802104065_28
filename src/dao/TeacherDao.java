//201802104065闫天意
package dao;

import Service.DegreeService;
import Service.DepartmentService;
import Service.ProfTitleService;
import domain.Degree;
import domain.Department;
import domain.ProfTitle;
import domain.Teacher;
import helper.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;

public final class TeacherDao {
	//创建私有的静态的TeacherDao对象
	private static TeacherDao teacherDao = new TeacherDao();
	//创建私有的静态的集合teachers
	private static Collection<Teacher> teachers = new HashSet<Teacher>();
	//定义私有的构造器
	private TeacherDao(){}
	//定义getInstance()方法,返回teacherDao指向对象
	public static TeacherDao getInstance(){
		return teacherDao;
	}
	//定义findAll()方法,返回teachers集合
	public Collection<Teacher> findAll() throws SQLException{
		teachers.clear();
		Connection connection = JdbcHelper.getConn();
		//在该连接上创建语句盒子对象
		Statement stmt = connection.createStatement();
		//执行SQL查询语句并获得结果集对象
		ResultSet resultSet = stmt.executeQuery("select * from Teacher");
		ProfTitle profTitle = null;
		Degree degree = null;
		Department department = null;
		while (resultSet.next()){
			profTitle = ProfTitleService.getInstance().find(resultSet.getInt("title_id"));
			degree = DegreeService.getInstance().find(resultSet.getInt("degree_id"));
			department = DepartmentService.getInstance().find(resultSet.getInt("department_id"));
			Teacher teacher = new Teacher(resultSet.getInt("id"),resultSet.getString("name"),profTitle,degree,department);
			teachers.add(teacher);
		}
		return teachers;
	}
	//定义find()方法
	public Teacher find(Integer id) throws SQLException{
		Connection connection = JdbcHelper.getConn();
		String updateTeacher_sql = "SELECT * FROM Teacher where id = ?";
		PreparedStatement pstmt = connection.prepareStatement(updateTeacher_sql);
		pstmt.setInt(1,id);
		ResultSet resultSet = pstmt.executeQuery();
		Teacher teacher = null;
		if (resultSet.next()) {
			teacher = new Teacher(
					resultSet.getInt("id"),
					resultSet.getString("name"),
					ProfTitleService.getInstance().find(resultSet.getInt("title_id")),
					DegreeService.getInstance().find(resultSet.getInt("degree_id")),
					DepartmentService.getInstance().find(resultSet.getInt("department_id"))
			);
		}
		JdbcHelper.close(pstmt,connection);
		return teacher;
	}
	//通过向集合中添加teacher，实现添加功能
	public boolean add(Teacher teacher) throws SQLException {
		Connection connection = JdbcHelper.getConn();
		//创建sql语句
		String addTeacher_sql="Insert into Teacher (name,title_id,degree_id,department_id) values (?,?,?,?)";
		//在该连接上创建预编译语句对象
		PreparedStatement pstmt =connection.prepareStatement(addTeacher_sql);
		//为预编译参数赋值
		pstmt.setString(1, teacher.getName());
		pstmt.setInt(2, teacher.getProfTitle().getId());
		pstmt.setInt(3, teacher.getDegree().getId());
		pstmt.setInt(4,teacher.getDepartment().getId());
		teachers.add(teacher);
		//执行预编译对象的excuteUpdate方法，获取添加的记录行数
		int affectedRowNum=pstmt.executeUpdate();
		//显示添加的记录的行数
		System.out.println("添加了"+affectedRowNum+"条记录");
		JdbcHelper.close(pstmt,connection);
		return affectedRowNum > 0;
	}
	//定义delete()方法
	public boolean delete(Teacher teacher) throws SQLException{
		Connection connection = JdbcHelper.getConn();
		//创建sql语句
		String deleteTeacher_sql="Delete from Teacher where id=? ";
		//在该连接上创建预编译语句对象
		PreparedStatement pstmt =connection.prepareStatement(deleteTeacher_sql);
		//为预编译参数赋值
		pstmt.setInt(1,teacher.getId());
		//执行预编译对象的excuteUpdate方法
		int affectedRowNum=pstmt.executeUpdate();
		//显示删除的记录的行数
		System.out.println("删除了"+affectedRowNum+"条记录");
		JdbcHelper.close(pstmt,connection);
		return true ;
	}
	public boolean update(Teacher teacher) throws ClassNotFoundException,SQLException{
		Connection connection = JdbcHelper.getConn();
		//写sql语句
		String updateTeacher_sql = " update teacher set name =?,title_id=?,degree_id=? where department_id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(updateTeacher_sql);
		//为预编译参数赋值
		preparedStatement.setString(1, teacher.getName());
		preparedStatement.setInt(2, teacher.getProfTitle().getId());
		preparedStatement.setInt(3, teacher.getDegree().getId());
		preparedStatement.setInt(4,teacher.getDepartment().getId());
		//执行预编译语句，获取改变记录行数并赋值给affectedRowNum
		int affectedRows = preparedStatement.executeUpdate();
		System.out.println("更新了" + affectedRows + "条记录");
		//关闭资源		
		JdbcHelper.close(preparedStatement,connection);
		return affectedRows>0;
	}
}

