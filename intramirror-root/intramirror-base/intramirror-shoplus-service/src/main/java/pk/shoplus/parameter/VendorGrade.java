
package pk.shoplus.parameter;

/**
 * @author author : Jeff
 * @date create_at : 2016年11月30日 上午11:28:44
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class VendorGrade {
	public static final int NEW_VENDOR = 0;
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
		if (grade != NEW_VENDOR) {
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
		if (grade != NEW_VENDOR) {
			grade = grade - 1 <= A ? A : grade - 1;
		}
		return grade;
	}

	public static String changeGrade(Integer grade) {
		String gradeName = "";
		if (grade == NEW_VENDOR) {
			gradeName = "New Vendor";
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
