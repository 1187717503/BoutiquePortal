package pk.shoplus.model;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Jeff
 *
 */
public class GeneratorBeans {

	private String[] colnames; // 列名数组
	private String[] colTypes; // 列名类型数组
	private int[] colSizes; // 列名大小数组
	private boolean f_util = false; // 是否需要导入包java.util.*
	private boolean f_sql = false; // 是否需要导入包java.sql.*

	// 数据库连接rm-m5eoch6ldrh3595l7.mysql.rds.aliyuncs.com
	private static final String URL = "jdbc:mysql://rm-m5ez8k11rdsq387fs.mysql.rds.aliyuncs.com:3306/cheezmall_alpha";
	private static final String NAME = "pciladmin";
	private static final String PASS = "P@ssw0rd";
	private static final String DRIVER = "com.mysql.jdbc.Driver";

	/*
	 * 构造函数
	 */
	public GeneratorBeans() {
		// 创建连接
		Connection con;
		// 查要生成实体类的表
		String sql = "select * from ";
		PreparedStatement pStemt = null;
		try {
			try {
				Class.forName(DRIVER);
			} catch (ClassNotFoundException e1) {
				throw new RuntimeException(e1);
			}
			con = DriverManager.getConnection(URL, NAME, PASS);

			List<String> list = getAllTableNames(con, NAME, "cheezmall_alpha");

			for (String tablename : list) {
				System.out.println("Generate the tablename:" + tablename);
				pStemt = con.prepareStatement(sql + tablename);

				ResultSetMetaData rsmd = pStemt.getMetaData();

				int size = rsmd.getColumnCount(); // 统计列

				colnames = new String[size];
				colTypes = new String[size];
				colSizes = new int[size];

				for (int i = 0; i < size; i++) {
					colnames[i] = rsmd.getColumnName(i + 1);
					colTypes[i] = rsmd.getColumnTypeName(i + 1);

					if (colTypes[i].equalsIgnoreCase("datetime")) {
						f_util = true;
					}
					if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
						f_sql = true;
					}
					colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
				}

				String content = parse(colnames, colTypes, colSizes, tablename);

				try {
					File directory = new File("");
					System.out.println("绝对路径：" + directory.getAbsolutePath());
					System.out.println("相对路径：" + directory.getCanonicalPath());
					String path = this.getClass().getResource("").getPath();

					System.out.println(path);
					String[] tableNameArr = tablename.split("_");
					String name = "";
					for (int i = 0; i < tableNameArr.length; i++) {
						name += initcap(tableNameArr[i]);
					}
					System.out.println("/src/main/java/" + path.substring(path.lastIndexOf("/com/", path.length())));
					FileWriter fw = new FileWriter(directory.getAbsolutePath() + "/src/main/java/"
							+ path.substring(path.lastIndexOf("/com/", path.length()), path.length()) + name + ".java");
					PrintWriter pw = new PrintWriter(fw);
					pw.println(content);
					pw.flush();
					pw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// try {
			// con.close();
			// } catch (SQLException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

	}

	/**
	 * 功能：生成实体类主体代码
	 * 
	 * @param colnames
	 * @param colTypes
	 * @param colSizes
	 * @return
	 */
	private String parse(String[] colnames, String[] colTypes, int[] colSizes, String tablename) {
		StringBuffer sb = new StringBuffer();

		// 判断是否导入工具包

		sb.append("package com.cheezmall.model;\r\n");
		if (f_util) {
			sb.append("import java.util.*;\r\n");
		}

		if (f_sql) {
			sb.append("import java.sql.*;\r\n");
		}

		sb.append("import java.math.BigDecimal;\r\n");
		sb.append("import com.cheezmall.model.annotation.*;\r\n");
		sb.append("\r\n");
		// 注释部分
		sb.append("   /**\r\n");
		sb.append("    * " + tablename + " 实体类\r\n");
		sb.append("    * " + new Date() + "\r\n");
		sb.append("    */ \r\n");
		// 实体部分
		sb.append("    @Entity(\"" + tablename + "\") \r\n");
		String[] tableNameArr = tablename.split("_");
		String name = "";
		for (int i = 0; i < tableNameArr.length; i++) {
			name += initcap(tableNameArr[i]);
		}

		sb.append("public class " + name + "{\r\n");
		processAllAttrs(sb);// 属性
		processAllMethod(sb);// get set方法
		sb.append("}\r\n");

		// System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * 功能：生成所有属性
	 * 
	 * @param sb
	 */
	private void processAllAttrs(StringBuffer sb) {

		for (int i = 0; i < colnames.length; i++) {
			sb.append("/**\r\n");
			sb.append("* " + (i + 1) + ":" + colnames[i] + "\r\n");
			sb.append("*/" + "\r\n");
			if (i == 0) {
				sb.append("\t @Id");
				sb.append("\tpublic Long" + " " + colnames[i] + ";\r\n");
			} else {
				sb.append("\t @Column");
				sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " " + colnames[i] + ";\r\n");
			}
		}

	}

	/**
	 * 功能：生成所有方法
	 * 
	 * @param sb
	 */
	private void processAllMethod(StringBuffer sb) {

		for (int i = 0; i < colnames.length; i++) {
			// Setter
			// sb.append("\tpublic void set" + initcap(colnames[i]) + "(" +
			// sqlType2JavaType(colTypes[i]) + " "
			// + colnames[i] + "){\r\n");
			// sb.append("\tthis." + colnames[i] + "=" + colnames[i] + ";\r\n");
			// sb.append("\t}\r\n");
			// Getter
			if (i == 0) {
				sb.append("\tpublic Long get" + initcap(colnames[i]) + "(){\r\n");
			} else {
				sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + initcap(colnames[i]) + "(){\r\n");
			}
			sb.append("\t\treturn " + colnames[i] + ";\r\n");
			sb.append("\t}\r\n");
		}

	}

	/**
	 * 功能：将输入字符串的首字母改成大写
	 * 
	 * @param str
	 * @return
	 */
	private String initcap(String str) {

		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}

		return new String(ch);
	}

	/**
	 * 功能：获得列的数据类型
	 * 
	 * @param sqlType
	 * @return
	 */
	private String sqlType2JavaType(String sqlType) {
		String type = "null";
		if (sqlType.equalsIgnoreCase("bit")) {
			type = "Boolean";
		} else if (sqlType.equalsIgnoreCase("tinyint")) {
			type = "Integer";
		} else if (sqlType.equalsIgnoreCase("smallint")) {
			type = "short";
		} else if (sqlType.equalsIgnoreCase("int")) {
			type = "Integer";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			type = "Long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			type = "Float";
		} else if (sqlType.equalsIgnoreCase("numeric") || sqlType.equalsIgnoreCase("real")
				|| sqlType.equalsIgnoreCase("money") || sqlType.equalsIgnoreCase("smallmoney")
				|| sqlType.equalsIgnoreCase("double")) {
			type = "Double";
		} else if (sqlType.equalsIgnoreCase("decimal")) {
			type = "BigDecimal";
		} else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("blob")) {
			type = "String";
		} else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date")
				|| sqlType.equalsIgnoreCase("timestamp")) {
			type = "Date";
		} else if (sqlType.equalsIgnoreCase("image")) {
			type = "Blod";
		}
		return type;
	}

	/**
	 * 获取指定数据库和用户的所有表名
	 * 
	 * @param conn
	 *            连接数据库对象
	 * @param user
	 *            用户
	 * @param database
	 *            数据库名
	 * @return
	 */
	public static List<String> getAllTableNames(Connection conn, String user, String database) {
		List<String> tableNames = new ArrayList<String>();
		if (conn != null) {
			try {
				DatabaseMetaData dbmd = conn.getMetaData();
				// 表名列表
				ResultSet rest = dbmd.getTables(database, null, null, new String[] { "TABLE" });
				// 输出 table_name
				while (rest.next()) {
					String tableSchem = rest.getString("TABLE_SCHEM");
					System.out.println("tableSchem:" + tableSchem);
					System.out.println("tableNames:" + rest.getString("TABLE_NAME"));

					// if (user.equalsIgnoreCase(tableSchem)) {
					if (!rest.getString("TABLE_NAME").equals("order")) {
						if (!rest.getString("TABLE_NAME").contains("(")) {
							tableNames.add(rest.getString("TABLE_NAME"));
						}
					} else {
						tableNames.add("`order`");
					}
					// }
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tableNames;
	}

	/**
	 * 出口 TODO
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new GeneratorBeans();

	}
}
