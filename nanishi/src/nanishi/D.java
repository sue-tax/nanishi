package nanishi;
/**
 *
 */

/**
 * @author sue-t
 *
 */
public final class D {
	/** デバッグプリントの指示
	 * 0 プリントしない
	 * 1 コンソールへプリント
	 */
	private static int c = 1;

	public static <T> void dprint(T value) {
		if (D.c == 1) {
			System.out.println(value);
		}
	}

	public static <T> void dprint_name( String str, T value) {
		if (D.c == 1) {
			System.out.print(str);
			System.out.print(" ");
			System.out.println(value);
		}
	}

	public static void dprint_method_start() {
		if (D.c == 1) {
			System.out.print(Thread.currentThread().
					getStackTrace()[2].getClassName());
			System.out.print(" ");
			System.out.print(Thread.currentThread().
					getStackTrace()[2].getMethodName());
			System.out.println(" start");
		}
	}

	public static void dprint_method_end() {
		if (D.c == 1) {
			System.out.print(Thread.currentThread().
					getStackTrace()[2].getClassName());
			System.out.print(" ");
			System.out.print(Thread.currentThread().
					getStackTrace()[2].getMethodName());
			System.out.println(" end");
		}
	}

}
