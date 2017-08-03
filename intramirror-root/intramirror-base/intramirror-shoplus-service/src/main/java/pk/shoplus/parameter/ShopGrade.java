
package pk.shoplus.parameter;

/**
 * @author author : Jeff
 * @date create_at : 2016年11月29日 下午10:52:07
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class ShopGrade {
	public static final int NEW_SHOP = 0;
	public static final int A = 1;
	public static final int B = 2;
	public static final int C = 3;

	/**
	 * 降级
	 * 
	 * @param grade
	 * @return
	 */
	public static Integer deGrade(Integer grade) {
		
		if (grade != NEW_SHOP) {
			grade = grade + 1 >= C ? C : grade + 1;
		}
		return grade;
	}

	/**
	 * 升级
	 * 
	 * @param grade
	 * @return
	 */
	public static Integer upGrade(Integer grade) {
		
		if (grade != NEW_SHOP) {
			grade = grade - 1 <= A ? A : grade - 1;
		}
		
		return grade;
	}
	
	public static String changeGrade(Integer grade) {
		String gradeName = "";
		if (grade == NEW_SHOP) {
			gradeName = "New Shop";
		} else if (grade == A) {
			gradeName = "A";
		} else if (grade == B) {
			gradeName = "B";
		} else if (grade == C) {
			gradeName = "C";
		}

		return gradeName;
	}

}
